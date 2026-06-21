package net.rubii.rac.network;

import com.mojang.blaze3d.platform.NativeImage;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.shaderpack.option.ShaderPackOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.network.payload.ModsReportPayload;
import net.rubii.rac.network.payload.ScreenshotReportPayload;
import net.rubii.rac.network.payload.SettingsReportPayload;
import net.neoforged.fml.ModList;
import net.rubii.rac.utils.Utils;
import net.rubii.rac.utils.result.GetModsResult;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class ClientNetworkHandler {

    /*public static void openConsentScreen() {
        Minecraft minecraft = Minecraft.getInstance();
        Player player  = minecraft.player;
        if (player == null) return;

        ConsentScreen screen = new ConsentScreen((accepted) -> {
            PacketDistributor.sendToServer(new ConsentReportPayload(accepted));
        });

        minecraft.setScreen(screen);
    }*/

    public static void sendScreenshotReport(boolean silent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        NativeImage image = Screenshot.takeScreenshot(minecraft.getMainRenderTarget());

        trySend(image, silent);
    }

    public static void trySend(NativeImage image, boolean silent) {
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

    public static void sendModsReport(boolean silent) {
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

        Map<String, Integer> hashMap = Map.of();
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

        PacketDistributor.sendToServer(new ModsReportPayload(Utils.encodeList(modIds), Utils.encodeHashMap(hashMap), silent));
    }

    public static void sendSettingsReport(boolean silent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        float currentGamma = minecraft.options.gamma().get().floatValue();

        String currentShaderName = "";
        int caveLightingMultiplier = -1;
        if (ModList.get().isLoaded("iris")) {
            IrisApi api = IrisApi.getInstance();
            if (api.isShaderPackInUse()){
                currentShaderName = Iris.getCurrentPackName();
                if (Iris.getCurrentPack().isPresent()){
                    ShaderPackOptions options = Iris.getCurrentPack().get().getShaderPackOptions();
                    if (options.getOptionValues().getStringValue("CAVE_LIGHTING").isPresent()){
                        caveLightingMultiplier = Integer.parseInt(options.getOptionValues().getStringValue("CAVE_LIGHTING").get());
                    } else if (options.getOptionValues().getStringValue("CAVE_LIGHTING_I").isPresent()){
                        caveLightingMultiplier = Integer.parseInt(options.getOptionValues().getStringValue("CAVE_LIGHTING_I").get());
                    } else {
                        caveLightingMultiplier = -1;
                    }
                }
            }
        }

        PacketDistributor.sendToServer(new SettingsReportPayload(currentShaderName, caveLightingMultiplier, currentGamma, silent));
    }
}