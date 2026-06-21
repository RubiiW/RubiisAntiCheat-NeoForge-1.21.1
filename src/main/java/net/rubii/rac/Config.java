package net.rubii.rac;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue PRIVATE_CHAT_PERMISSION = BUILDER
            .comment(" The new permissions that /msg, /w and /tell needs.")
            .defineInRange("privateChat.permission", 4, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ENABLE_PERIODIC_MONITORING = BUILDER
            .comment(" The interval between graphics check payloads (in ticks)")
            .define("periodicMontioring.enable", true);

    public static final ModConfigSpec.IntValue PERIODIC_MONITORING_INTERVAL = BUILDER
            .comment(" The interval between graphics check payloads (in ticks)")
            .defineInRange("periodicMontioring.interval", 60, 20, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ENABLE_SCREENSHOTS = BUILDER
            .comment(" Enable the client view screenshots feature")
            .define("screenshots.enable", true);

    public static final ModConfigSpec.BooleanValue ENABLE_REQUIRED_MOD_FILES_LIST = BUILDER
            .comment(" The jar name of the required mods")
            .define("requiredModFilesList.enable", false);

    public static final ModConfigSpec.ConfigValue<ArrayList<String>> REQUIRED_MOD_FILES_LIST = BUILDER
            .comment(" The jar name of the required mods")
            .define("requiredModFilesList.list", new ArrayList<>(List.of("example-mod-file.jar", "other-mod-file.jar")) );

    public static final ModConfigSpec.BooleanValue ENABLE_FORBIDDEN_MOD_FILES_LIST = BUILDER
            .comment(" The jar name of the forbidden mods")
            .define("forbiddenModFilesList.enable", true);

    public static final ModConfigSpec.ConfigValue<ArrayList<String>> FORBIDDEN_MOD_FILES_LIST = BUILDER
            .comment(" The jar name of the forbidden mods")
            .define("forbiddenModFilesList.list", new ArrayList<>(List.of("example-mod-file.jar", "other-mod-file.jar")));

    public static final ModConfigSpec.BooleanValue ENABLE_MOD_ALTERATION_DETECTION = BUILDER
            .comment(" The file name of the allowed shaders")
            .define("modAlterationDetection.enable", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SHADER_WHITELIST = BUILDER
            .comment(" The file name of the allowed shaders")
            .define("shaderWhitelist.enable", true);

    public static final ModConfigSpec.ConfigValue<ArrayList<String>> SHADER_WHITELIST = BUILDER
            .comment(" The file name of the allowed shaders")
            .define("shaderWhitelist.list", new ArrayList<>(List.of("shaderpack-file-name.zip")));

    public static final ModConfigSpec.BooleanValue ENABLE_CAVE_LIGHTING_MULTIPLIER = BUILDER
            .comment(" The max allowed cave lighting multiplier")
            .define("caveLightMultiplier.enable", true);

    public static final ModConfigSpec.IntValue CAVE_LIGHTING_MULTIPLIER = BUILDER
            .comment(" The max allowed cave lighting multiplier")
            .defineInRange("caveLightMultiplier.max", 0, 0, 1400);

    public static final ModConfigSpec.BooleanValue ENABLE_BRIGHTNESS = BUILDER
            .comment(" The max allowed brightness")
            .define("brightness.enable", true);

    public static final ModConfigSpec.DoubleValue MAX_BRIGHTNESS = BUILDER
            .comment(" The max allowed brightness")
            .defineInRange("brightness.max", 0.0f, 0.0, 1.0);



    static final ModConfigSpec SPEC = BUILDER.build();

}
