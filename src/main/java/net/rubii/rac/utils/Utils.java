package net.rubii.rac.utils;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.rubii.rac.Config;
import net.rubii.rac.RubiisAntiCheat;
import net.rubii.rac.utils.result.ForbiddenModsResult;
import net.rubii.rac.utils.result.GetModsResult;
import net.rubii.rac.utils.result.RequiredModsResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Utils {
    public static String encodeServerData(){
        String data = "";

        if (Config.ENABLE_SCREENSHOTS.get()) data += "§a";
        if (Config.ENABLE_MOD_ALTERATION_DETECTION.get() || Config.ENABLE_FORBIDDEN_MOD_FILES_LIST.get() || Config.ENABLE_REQUIRED_MOD_FILES_LIST.get()) data += "§b";
        if (Config.PRIVATE_CHAT_PERMISSION.get() != 0) data += "§c";
        if (Config.ENABLE_BRIGHTNESS.get()) data += "§d";
        if (Config.ENABLE_CAVE_LIGHTING_MULTIPLIER.get()) data += "§e";
        if (Config.ENABLE_SHADER_WHITELIST.get()) data += "§f";

        data += "§r";

        return data + "§c[RAC]§r ";
    }

    public static Component decodeServerData(String identifier){
        return Component.translatable(switch (identifier){
            case "a" -> "features.rac.screenshot";
            case "b" -> "features.rac.mod_files_view";
            case "c" -> "features.rac.private_chat_permission_override";
            case "d" -> "features.rac.brightness";
            case "e" -> "features.rac.cave_light_multiplier";
            case "f" -> "features.rac.shader_id";
            default -> "";
        });
    }

    public static GetModsResult getMods(File modsFolder) {
        if (!modsFolder.exists() || !modsFolder.isDirectory()) {
            return new GetModsResult(false, List.of());
        }

        List<File> mods = Lists.newArrayList();

        for (File file : modsFolder.listFiles()) {
            if (file.isFile()) {
                if (file.getName().endsWith(".jar")) {
                    mods.add(file);
                }
            }
        }

        if (mods.isEmpty()) return new GetModsResult(false, List.of());

        return new GetModsResult(true, mods);
    }

    public static List<String> getModList(File modsFolder) {
        GetModsResult getModsResult = getMods(modsFolder);
        if (!getModsResult.success) return null;

        List<String> ids = Lists.newArrayList();

        for (File mod : getModsResult.mods) {
            ids.add(mod.getName());
        }

        return ids;
    }

    public static Map<String, Integer> hashModFiles(List<File> mods) throws IOException {
        Map<String, Integer> hashList = Map.of();

        for(File mod : mods){
            List<String> modLines = Files.readAllLines(Path.of(mod.getAbsolutePath()));
            String data = encodeList(modLines);
            hashList.put(mod.getName(), data.hashCode());
        }

        return hashList;
    }

    public static RequiredModsResult hasRequiredModFiles(List<String> modIds) {
        List<String> requiredMods = Config.REQUIRED_MOD_FILES_LIST.get();
        List<String> missingMods = Lists.newArrayList();
        boolean success = true;

        for(String requiredMod : requiredMods) {
            if(!modIds.contains(requiredMod)){
                missingMods.add(requiredMod);
                success = false;
            }
        }

        return new RequiredModsResult(success, missingMods);
    }

    public static ForbiddenModsResult hasForbiddenModFiles(List<String> modIds){
        List<String> forbiddenMods = Config.FORBIDDEN_MOD_FILES_LIST.get();
        List<String> additionalMods = Lists.newArrayList();
        boolean success = true;

        for(String forbiddenMod : forbiddenMods) {
            if (modIds.contains(forbiddenMod)){
                additionalMods.add(forbiddenMod);
                success = false;
            }
        }

        return new ForbiddenModsResult(success, additionalMods);
    }

    public static String encodeList(List<String> strings) {
        String result = "";
        for (String string : strings) {
            result += string + "§";
        }
        return result;
    }

    public static List<String> decodeList(String list) {
        return List.of(list.split("§"));
    }

    public static String encodeHashMap(Map<String, Integer> map) {
        String result = "";

        for (String key : map.keySet()) {
            Integer value = map.get(key);
            result += key + "@" + value + "§";
        }

        return result;
    }

    public static Map<String, Integer> decodeHashMap(String list) {
        Map<String, Integer> result = Map.of();

        for (String key : decodeList(list)) {
            String[] parts = key.split("@");
            result.put(parts[0], Integer.parseInt(parts[1]));
        }

        return result;
    }

    public static byte[] compress(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

        if (!writers.hasNext()) throw new IllegalStateException("No JPG Writer found");

        ImageWriter writer = writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        writer.write(null, new IIOImage(image, null, null), param);

        ios.close();
        writer.dispose();

        return baos.toByteArray();
    }
}
