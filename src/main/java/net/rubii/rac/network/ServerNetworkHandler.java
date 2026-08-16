package net.rubii.rac.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.rubii.rac.Config;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.network.payload.ModFilesLoggingReportPayload;
import net.rubii.rac.network.payload.ModsIntegrityReportPayload;
import net.rubii.rac.network.payload.ScreenshotReportPayload;
import net.rubii.rac.network.payload.GraphicsSettingsReportPayload;
import net.rubii.rac.utils.Checks;
import net.rubii.rac.utils.result.CheckResult;
import net.rubii.rac.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ServerNetworkHandler {

    public static void handleScreenshotReport(ScreenshotReportPayload payload, ServerPlayer sourcePlayer) throws IOException {
        if (!Config.ENABLE_SCREENSHOTS.get()) return;

        byte[] imageData = payload.image();
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage image = ImageIO.read(bais);

        if (image == null) {
            throw new IOException("Failed to decode image from player " + sourcePlayer.getName().getString());
        }

        MinecraftServer server = sourcePlayer.getServer();
        if (server == null) {
            RubiisAntiCheat.LOGGER.error("Server is null in ServerNetworkHandler.handleScreenshotReport()");
        }

        Path baseDir = Utils.getScreenshotDirectory(sourcePlayer);
        Files.createDirectories(baseDir);

        Path filePath = baseDir.resolve(Utils.getTimestampFile(".png"));

        if (!ImageIO.write(image, "png", filePath.toFile())) {
            throw new IOException("Failed to write image file to " + filePath);
        }

        if (!payload.silent()) sourcePlayer.sendSystemMessage(Component.translatable("rac.screenshot_check_passed").setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));
    }

    public static void handleModsIntegrityReport(ModsIntegrityReportPayload payload, ServerPlayer player) throws IOException {
        boolean isValid = true;
        Component reason = Component.literal("Unknown Error: ServerNetworkHandler.handleModsReport()");


        Map<String, Integer> clientHashMap = Utils.decodeHashMap(payload.modHashList());

        /*CheckResult requiredModsResult = Checks.requiredMods(modFilesList);
        if (!requiredModsResult.success){
            isValid = false;
            reason = requiredModsResult.reason;
        }

        CheckResult forbiddenModsResult = Checks.forbiddenMods(modFilesList);
        if (!forbiddenModsResult.success){
            isValid = false;
            reason = forbiddenModsResult.reason;
        }*/

        CheckResult hashResult = Checks.compareModFilesHash(clientHashMap);
        if (!hashResult.success){
            isValid = false;
            reason = hashResult.reason;
        }

        if (!isValid) {
            player.connection.disconnect(reason);
        } else {
            if (!payload.silent()) player.sendSystemMessage(Component.translatable("rac.mods_integrity_check_passed").setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));
        }
    }

    public static void handleModFilesLoggingReport(ModFilesLoggingReportPayload payload, ServerPlayer player) throws IOException {
        Path baseDir = Utils.getModsLoggingDirectory(player);
        Files.createDirectories(baseDir);
        Path filePath = baseDir.resolve(Utils.getTimestampFile(".log"));

        Files.write(filePath, Utils.decodeList(payload.modFilesList()));

        if (!payload.silent()) player.sendSystemMessage(Component.translatable("rac.mod_list_passed").setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));
    }

    public static void handleGraphicsReport(GraphicsSettingsReportPayload payload, ServerPlayer player) throws IOException {
        boolean isValid = true;
        Component reason = Component.literal("Unknown Error: Ask RubiiW to check ServerNetworkHandler.handleSettingsReport()");

        String shaderName = payload.shaderName();
        float caveLightingMultiplier = payload.caveLightingMultiplier();
        float gamma = payload.gamma();

        Path baseDir = Utils.getShadersLoggingDirectory(player);
        Files.createDirectories(baseDir);
        Path filePath = baseDir.resolve(Utils.getTimestampFile(".log"));

        if (!shaderName.isBlank()) Files.writeString(filePath, shaderName);

        CheckResult validCaveLight = Checks.validCaveLightMultiplier(caveLightingMultiplier);
        if (!validCaveLight.success){
            isValid = false;
            reason = validCaveLight.reason;
        }

        CheckResult validBrightness = Checks.validBrightness(gamma);
        if (!validBrightness.success){
            isValid = false;
            reason = validBrightness.reason;
        }

        if (!isValid) {
            player.connection.disconnect(reason);
        } else {
            if (!payload.silent()) player.sendSystemMessage(Component.translatable("rac.graphics_check_passed")
                            .setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));
        }
    }
}