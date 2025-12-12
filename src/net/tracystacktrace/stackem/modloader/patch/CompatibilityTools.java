package net.tracystacktrace.stackem.modloader.patch;

import net.minecraft.src.StringTranslate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CompatibilityTools {
    private static final Map<String, String> ownTranslateKey = new HashMap<>();
    public static boolean OBFUSCATED_ENV = true;
    private static final DateTimeFormatter HHMMSS_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void log(String message) {
        System.out.printf("[%s] [Stack 'Em] %s\n", LocalTime.now().format(HHMMSS_FORMAT), message);
    }

    private static boolean classExists(String s) {
        try {
            Class.forName(s);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void getKnownWithEnvironment() {
        OBFUSCATED_ENV = !classExists("net.minecraft.src.ModLoader");
        //TODO: Add compatibility for other mods
    }

    public static void obtainCurrentLang() {
        final InputStream inputStream = CompatibilityTools.class.getResourceAsStream("/assets/stackemneptune/default.lang");
        if (inputStream == null) {
            CompatibilityTools.log("Couldn't find default.lang! Corrupted mod zip?");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#") || !line.contains("=")) {
                    continue;
                }

                final String[] rawSplit = line.split("=");
                ownTranslateKey.put(rawSplit[0], rawSplit[1]);
            }
        } catch (IOException e) {
            CompatibilityTools.log("Failed to load default.lang, expect problems: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String translateKey(String key) {
        final String result = StringTranslate.getInstance().translateKey(key);
        if (key.equals(result)) {
            return ownTranslateKey.getOrDefault(key, "N/A " + key);
        }
        return result;
    }

    public static String translateKey(String key, String arg1) {
        final String result = StringTranslate.getInstance().translateKey(key);
        if (key.equals(result)) {
            return String.format(ownTranslateKey.getOrDefault(key, "N/A " + key), arg1);
        }
        return String.format(result, arg1);
    }
}
