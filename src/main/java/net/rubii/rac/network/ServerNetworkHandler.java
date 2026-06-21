package net.rubii.rac.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.rubii.rac.Config;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.network.payload.ModsReportPayload;
import net.rubii.rac.network.payload.ScreenshotReportPayload;
import net.rubii.rac.network.payload.SettingsReportPayload;
import net.rubii.rac.utils.Checks;
import net.rubii.rac.utils.result.CheckResult;
import net.rubii.rac.utils.result.ForbiddenModsResult;
import net.rubii.rac.utils.result.GetModsResult;
import net.rubii.rac.utils.result.RequiredModsResult;
import net.rubii.rac.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        Path baseDir = server.getServerDirectory().resolve("rac").resolve("screenshots").resolve(sourcePlayer.getStringUUID());
        Files.createDirectories(baseDir);

        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy-HHmm")) + ".png";
        Path filePath = baseDir.resolve(fileName);

        if (!ImageIO.write(image, "png", filePath.toFile())) {
            throw new IOException("Failed to write image file to " + filePath);
        }

        if (!payload.silent()) sourcePlayer.sendSystemMessage(Component.translatable("rac.screenshot_check_passed").setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));
    }

    public static void handleModsReport(ModsReportPayload payload, ServerPlayer player) throws IOException {
        boolean isValid = true;
        Component reason = Component.literal("Unknown Error: Ask RubiiW to check ServerNetworkHandler.handleModsReport()");

        List<String> modFilesList = Utils.decodeList(payload.modFilesList());
        Map<String, Integer> clientHashMap = Utils.decodeHashMap(payload.modHashList());

        CheckResult requiredModsResult = Checks.requiredMods(modFilesList);
        if (!requiredModsResult.success){
            isValid = false;
            reason = requiredModsResult.reason;
        }

        CheckResult forbiddenModsResult = Checks.forbiddenMods(modFilesList);
        if (!forbiddenModsResult.success){
            isValid = false;
            reason = forbiddenModsResult.reason;
        }

        CheckResult hashResult = Checks.compareModFilesHash(clientHashMap);
        if (!hashResult.success){
            isValid = false;
            reason = hashResult.reason;
        }

        if (!isValid) {
            player.connection.disconnect(reason);
        } else {
            if (!payload.silent()) player.sendSystemMessage(Component.translatable("rac.mods_check_passed").setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));
        }
    }

    public static void handleSettingsReport(SettingsReportPayload payload, ServerPlayer player) {
        boolean isValid = true;
        Component reason = Component.literal("Unknown Error: Ask RubiiW to check ServerNetworkHandler.handleSettingsReport()");

        String shaderName = payload.shaderName();
        int caveLightingMultiplier = payload.caveLightingMultiplier();
        float gamma = payload.gamma();

        CheckResult validShaderResult = Checks.validShader(shaderName);
        if (!validShaderResult.success){
            isValid = false;
            reason = validShaderResult.reason;
        }

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