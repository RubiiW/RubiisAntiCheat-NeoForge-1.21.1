package net.rubii.rac;

import net.minecraft.util.Tuple;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> CUSTOM_DATA_PATH = BUILDER
            .comment(" The path to the directory where all player data will be stored\nLeave blank for default (/<server>/rac/)")
            .define("customDataPath.path", "");

    public static final ModConfigSpec.IntValue PRIVATE_CHAT_PERMISSION = BUILDER
            .comment(" The new permissions that /msg, /w and /tell needs")
            .defineInRange("privateChat.permission", 4, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ENABLE_PERIODIC_MONITORING = BUILDER
            .comment(" Enables the automatic graphics checks")
            .define("periodicMontioring.enable", true);

    public static final ModConfigSpec.IntValue PERIODIC_MONITORING_INTERVAL = BUILDER
            .comment(" The interval between graphics checks (in ticks)")
            .defineInRange("periodicMontioring.interval", 60, 20, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue ENABLE_SCREENSHOTS = BUILDER
            .comment(" Enables the client screenshots feature")
            .define("screenshots.enable", true);

    /*public static final ModConfigSpec.BooleanValue ENABLE_REQUIRED_MOD_FILES_LIST = BUILDER
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
            .define("forbiddenModFilesList.list", new ArrayList<>(List.of("example-mod-file.jar", "other-mod-file.jar")));*/

    public static final ModConfigSpec.BooleanValue ENABLE_MOD_ALTERATION_DETECTION = BUILDER
            .comment(" EXPERIMENTAL: Enables the mod alteration detection")
            .define("modAlterationDetection.enable", true);

    public static final ModConfigSpec.BooleanValue ENABLE_LOG_MOD_FILES_LIST = BUILDER
            .comment(" Enables logging the mod files when joining")
            .define("logModFilesList.enable", true);

    public static final ModConfigSpec.BooleanValue ENABLE_CAVE_LIGHTING_MULTIPLIER = BUILDER
            .comment(" The max allowed cave lighting multiplier")
            .define("caveLightMultiplier.enable", true);

    public static final ModConfigSpec.DoubleValue CAVE_LIGHTING_MULTIPLIER = BUILDER
            .comment(" The max allowed cave lighting multiplier (percentage)")
            .defineInRange("caveLightMultiplier.max", 0.0, 0.0, 1.0);

    public static final ModConfigSpec.ConfigValue<ArrayList<String>> CAVE_LIGHT_MULTIPLIER_NAMES = BUILDER
            .comment("The names of the shaders' cave lighting multiplier variables")
            .define("caveLightMultiplier.names", new ArrayList<>(List.of("CAVE_LIGHTING_I", "CAVE_LIGHTING")));

    public static final ModConfigSpec.ConfigValue<ArrayList<Integer>> CAVE_LIGHT_MULTIPLIER_VALUES = BUILDER
            .comment("The max values for the corresponding cave lighting variables (must match order of names)")
            .define("caveLightMultiplier.values", new ArrayList<>(List.of(2, 1600)));

    public static final ModConfigSpec.BooleanValue ENABLE_BRIGHTNESS = BUILDER
            .comment(" The max allowed brightness")
            .define("brightness.enable", true);

    public static final ModConfigSpec.DoubleValue MAX_BRIGHTNESS = BUILDER
            .comment(" The max allowed brightness")
            .defineInRange("brightness.max", 0.0, 0.0, 1.0);



    static final ModConfigSpec SPEC = BUILDER.build();

}
