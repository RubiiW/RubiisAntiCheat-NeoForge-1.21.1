package net.rubii.rac.utils;

import com.google.common.collect.Lists;
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
    //private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String encodeServerData(){
        String data = "";

        if (Config.ENABLE_SCREENSHOTS.get()) data += "§a";
        if (Config.ENABLE_MOD_ALTERATION_DETECTION.get() || Config.ENABLE_FORBIDDEN_MOD_FILES_LIST.get() || Config.ENABLE_REQUIRED_MOD_FILES_LIST.get()) data += "§b";
        if (Config.ENABLE_BRIGHTNESS.get()) data += "§c";
        if (Config.ENABLE_CAVE_LIGHTING_MULTIPLIER.get()) data += "§d";
        if (Config.ENABLE_SHADER_WHITELIST.get()) data += "§e";

        data += "§r";

        return data + "§c[RAC]§r ";
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

    /*public static void saveConsent(MinecraftServer server, Player player, boolean accepted) {
        try {
            Path dataDir = getOrCreateDataDir(server);
            Path file = dataDir.resolve(player.getStringUUID() + ".dat");

            byte[] content = new byte[25];
            int offset = 0;

            long mostSig = player.getUUID().getMostSignificantBits();
            long leastSig = player.getUUID().getLeastSignificantBits();
            for (int i = 0; i < 8; i++) content[offset++] = (byte) (mostSig >>> (8 * (7 - i)));
            for (int i = 0; i < 8; i++) content[offset++] = (byte) (leastSig >>> (8 * (7 - i)));

            content[offset++] = (byte) (accepted ? 1 : 0);

            long timestamp = System.currentTimeMillis();
            for (int i = 0; i < 8; i++) content[offset++] = (byte) (timestamp >>> (8 * (7 - i)));

            byte[] hmac = calculateHmac(content);

            byte[] fullData = new byte[content.length + hmac.length];
            System.arraycopy(content, 0, fullData, 0, content.length);
            System.arraycopy(hmac, 0, fullData, content.length, hmac.length);

            Files.write(file, fullData);
            RubiisAntiCheat.LOGGER.info("Saved consent of " + player.getName());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            RubiisAntiCheat.LOGGER.error("Failed to save consent data for " + player.getName());
        }
    }

    public static boolean getConsent(MinecraftServer server, Player player) {
        try {
            Path dataDir = getOrCreateDataDir(server);
            if (!Files.exists(dataDir)) return false;

            Path file = dataDir.resolve(player.getStringUUID() + ".dat");
            if (!Files.exists(file)) return false;

            byte[] fullData = Files.readAllBytes(file);
            if (fullData.length != 57) {
                RubiisAntiCheat.LOGGER.warn("Invalid consent file size for " + player.getStringUUID() + ". Deleting");
                Files.delete(file);
                return false;
            }

            byte[] content = Arrays.copyOfRange(fullData, 0, 25);
            byte[] storedHmac = Arrays.copyOfRange(fullData, 25, 57);

            byte[] calculatedHmac = calculateHmac(content);
            if (!Arrays.equals(storedHmac, calculatedHmac)) {
                RubiisAntiCheat.LOGGER.warn("Consent file tampered for player " + player.getStringUUID() + "! HMAC mismatch. Resetting consent.");
                Files.delete(file);
                return false;
            }

            return content[16] == 1;

        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            RubiisAntiCheat.LOGGER.error("Error reading consent for " + player.getStringUUID());
            RubiisAntiCheat.LOGGER.error(e.getMessage());
            return false;
        }
    }

    private static Path getOrCreateDataDir(MinecraftServer server) throws IOException {
        Path dir = server.getServerDirectory().resolve("rac").resolve("data");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    private static byte[] calculateHmac(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        String key = sha256(RubiisAntiCheat.MOD_PATH.toString());

        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), HMAC_ALGORITHM);
        mac.init(keySpec);
        return mac.doFinal(data);
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found " + e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }*/
}
