package net.rubii.rac.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.rubii.rac.Config;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.utils.result.CheckResult;
import net.rubii.rac.utils.result.ForbiddenModsResult;
import net.rubii.rac.utils.result.GetModsResult;
import net.rubii.rac.utils.result.RequiredModsResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Checks {
    /*public static CheckResult requiredMods(List<String> modFilesList) {
        if (!Config.ENABLE_REQUIRED_MOD_FILES_LIST.get()) return new CheckResult(true, Component.literal("Disabled"));

        RequiredModsResult requiredResult = Utils.hasRequiredModFiles(modFilesList);
        return new CheckResult(
                requiredResult.success,
                Component.translatable("kickReason.missing_mods", requiredResult.missingMods.toString())
                        .setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
        );
    }

    public static CheckResult forbiddenMods(List<String> modFilesList) {
        if (!Config.ENABLE_FORBIDDEN_MOD_FILES_LIST.get()) return new CheckResult(true, Component.literal("Disabled"));

        ForbiddenModsResult forbiddenResult = Utils.hasForbiddenModFiles(modFilesList);
        return new CheckResult(
                forbiddenResult.success,
                Component.translatable("kickReason.forbidden_mods", forbiddenResult.forbiddenMods.toString())
                        .setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
        );
    }*/

    public static CheckResult compareModFilesHash(Map<String, Integer> clientHashMap){
        if (!Config.ENABLE_MOD_ALTERATION_DETECTION.get()) return new CheckResult(true, Component.literal("Disabled"));

        CheckResult error = new CheckResult(false,  Component.translatable("kickReason.error_server_side"));
        GetModsResult modsResult = Utils.getMods(RubiisAntiCheat.MOD_PATH.toFile().getParentFile());

        if (!modsResult.success) return error;
        else {
            try {
                Map<String, Integer> serverHashMap = Utils.hashModFiles(modsResult.mods);
                boolean stillValid = true;
                String faultyModId = "";

                for (String modId : serverHashMap.keySet()) {
                    if (clientHashMap.containsKey(modId)) {
                        Integer clientHash = clientHashMap.get(modId);
                        Integer serverHash = serverHashMap.get(modId);

                        if (!Objects.equals(clientHash, serverHash)) {
                            stillValid = false;
                            faultyModId = modId;
                        }
                    }
                }

                return new CheckResult(
                        stillValid,
                        Component.translatable("kickReason.modified_mod", faultyModId)
                );
            } catch (Exception e){
                RubiisAntiCheat.LOGGER.error(e.getMessage());
                return error;
            }
        }
    }

    public static CheckResult validCaveLightMultiplier(float caveLightingMultiplier){
        if (!Config.ENABLE_CAVE_LIGHTING_MULTIPLIER.get()) return new CheckResult(true, Component.literal("Disabled"));

        if (caveLightingMultiplier == -1) return new CheckResult(true, Component.literal("No Shader Enabled"));
        if (caveLightingMultiplier == -2) return new CheckResult(false, Component.translatable("kickReason.lighting_multiplier_not_supported")
                .setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT)));

        float maxMultiplier = Config.CAVE_LIGHTING_MULTIPLIER.get().floatValue();

        return new CheckResult(
                caveLightingMultiplier <= maxMultiplier,
                Component.translatable("kickReason.invalid_lighing_multiplier", caveLightingMultiplier, maxMultiplier)
                        .setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
        );
    }

    public static CheckResult validBrightness(float brightness){
        if (!Config.ENABLE_BRIGHTNESS.get()) return new CheckResult(true, Component.literal("Disabled"));

        float maxBrightness = Config.MAX_BRIGHTNESS.get().floatValue();

        return new CheckResult(
                Math.abs(brightness - maxBrightness) < 0.05f,
                Component.translatable("kickReason.invalid_brightness", brightness, maxBrightness)
                        .setStyle(Style.EMPTY.withFont(RubiisAntiCheat.ICON_FONT))
        );
    }
}
