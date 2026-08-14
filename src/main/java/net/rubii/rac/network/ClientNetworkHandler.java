package net.rubii.rac.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rubii.rac.Config;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.network.payload.ModFilesLoggingReportPayload;
import net.rubii.rac.network.payload.ModsIntegrityReportPayload;
import net.rubii.rac.network.payload.ScreenshotReportPayload;
import net.rubii.rac.network.payload.GraphicsSettingsReportPayload;
import net.neoforged.fml.ModList;
import net.rubii.rac.utils.Utils;
import net.rubii.rac.utils.result.GetModsResult;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientNetworkHandler {

    public static void sendScreenshotReport(boolean silent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        NativeImage image = Screenshot.takeScreenshot(minecraft.getMainRenderTarget());

        trySendImage(image, silent);
    }

    public static void trySendImage(NativeImage image, boolean silent) {
        try {
            BufferedImage bufferedImage = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            //REMOVE ALPHA
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int rgba = image.getPixelRGBA(x, y);
                    int b = (rgba >> 16) & 0xFF;
                    int g = (rgba >> 8) & 0xFF;
                    int r = (rgba) & 0xFF;
                    int rgb = (r << 16) | (g << 8) | b;

                    bufferedImage.setRGB(x, y, rgb);
                }
            }

            byte[] imageData = Utils.compress(bufferedImage, 0.7f);

            if (imageData.length > 1_500_000) {
                int newW = bufferedImage.getWidth() / 2;
                int newH = bufferedImage.getHeight() / 2;

                BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
                resized.getGraphics().drawImage(bufferedImage.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

                imageData = Utils.compress(resized, 0.5f);
            }

            PacketDistributor.sendToServer(new ScreenshotReportPayload(imageData, silent));
        } catch (Exception e) {
            RubiisAntiCheat.LOGGER.error(e.getMessage());
        }
    }

    public static void sendModsIntegrityReport(boolean silent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        File modsFolder = RubiisAntiCheat.MOD_PATH.toFile().getParentFile();
        List<String> modIds;

        try {
            modIds = Utils.getModList(modsFolder);
        } catch (Exception e) {
            RubiisAntiCheat.LOGGER.error(e.getMessage());
            return;
        }

        if (modIds == null || modIds.isEmpty()){
            RubiisAntiCheat.LOGGER.error("Failed to get mods ids in ClientNetworkHandler.sendModsReport()");
            return;
        }

        Map<String, Integer> hashMap;
        try {
            GetModsResult result = Utils.getMods(modsFolder);
            if (!result.success) {
                RubiisAntiCheat.LOGGER.error("Failed to get mods list in ClientNetworkHandler.sendModsReport()");
                return;
            }
            hashMap = Utils.hashModFiles(result.mods);
        }catch (Exception e) {
            RubiisAntiCheat.LOGGER.error(e.getMessage());
            return;
        }

        PacketDistributor.sendToServer(new ModsIntegrityReportPayload(Utils.encodeList(modIds), Utils.encodeHashMap(hashMap), silent));
    }

    public static void sendModFilesLoggingReport(boolean silent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        File modsFolder = RubiisAntiCheat.MOD_PATH.toFile().getParentFile();
        List<String> modFiles;

        try {
            modFiles = Utils.getModList(modsFolder);
        } catch (Exception e) {
            RubiisAntiCheat.LOGGER.error(e.getMessage());
            return;
        }

        if (modFiles == null || modFiles.isEmpty()){
            RubiisAntiCheat.LOGGER.error("Failed to get mods ids in ClientNetworkHandler.sendModFilesLoggingReport()");
            return;
        }

        PacketDistributor.sendToServer(new ModFilesLoggingReportPayload(Utils.encodeList(modFiles), silent));
    }

    public static void sendGraphicsReport(String namesList, String valuesList, boolean silent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        float currentGamma = minecraft.options.gamma().get().floatValue();

        String currentShaderName = "";
        float caveLightingMultiplier = -1;
        if (ModList.get().isLoaded("iris")) {
            IrisApi api = IrisApi.getInstance();
            if (api.isShaderPackInUse()){
                currentShaderName = Iris.getCurrentPackName();
                if (Iris.getCurrentPack().isPresent()){
                    ShaderPackOptions options = Iris.getCurrentPack().get().getShaderPackOptions();

                    boolean found = false;
                    List<String> names = Utils.decodeList(namesList);
                    List<Integer> values = new java.util.ArrayList<>(List.of());

                    for (String value : Utils.decodeList(valuesList)) {
                        values.add(Integer.parseInt(value));
                    }

                    for (int i = 0; i < names.size(); i++) {
                        String name = names.get(i);
                        int value = values.get(i);
                        RubiisAntiCheat.LOGGER.info("Testing shader option " + name + " with max value " + value);

                        Optional<String> option = options.getOptionValues().getStringValue(name);
                        if (option.isPresent()) {
                            RubiisAntiCheat.LOGGER.info("Found shader option " + name + " with max value " + value);
                            caveLightingMultiplier = Float.parseFloat(option.get()) / value;
                            found = true;
                            break;
                        }
                    }

                    if (!found) caveLightingMultiplier = -2;
                }
            }
        }

        PacketDistributor.sendToServer(new GraphicsSettingsReportPayload(currentShaderName, caveLightingMultiplier, currentGamma, silent));
    }
}