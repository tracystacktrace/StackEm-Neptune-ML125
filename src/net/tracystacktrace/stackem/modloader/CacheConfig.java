package net.tracystacktrace.stackem.modloader;

import net.tracystacktrace.stackem.modloader.patch.CompatibilityTools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public final class CacheConfig {

    public static String[] getCacheData(File configFolder) {
        final File stackemCache = new File(configFolder, "stackem.cache");

        if (!stackemCache.exists() || stackemCache.isDirectory()) {
            return new String[0];
        }

        try {
            return Files.readAllLines(stackemCache.toPath()).toArray(new String[0]);
        } catch (IOException e) {
            CompatibilityTools.log("Failed to read cache file properly, expect problems!");
            e.printStackTrace();
            return new String[0];
        }
    }

    public static void writeCacheData(File configFolder, String[] cache) {
        final File stackemCache = new File(configFolder, "stackem.cache");

        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        try {
            Files.write(stackemCache.toPath(), Arrays.asList(cache), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            CompatibilityTools.log("Failed to write cache properly, expect reset next session!");
            e.printStackTrace();
        }
    }

    public static File[] getPossibleTexturePacks(File gameFolder) {
        final File texturepacksFolder = new File(gameFolder, "texturepacks");
        if (texturepacksFolder.exists() && texturepacksFolder.isDirectory()) {
            return texturepacksFolder.listFiles();
        }
        return new File[0];
    }
}
