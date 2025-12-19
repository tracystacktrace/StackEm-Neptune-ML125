package net.tracystacktrace.stackem.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class CacheConfig {
    public static String[] getCacheData(File configFolder) {
        final File stackemCache = new File(configFolder, "stackem.cache");

        if (!stackemCache.exists() || stackemCache.isDirectory()) {
            return new String[0];
        }

        try {
            final FileReader reader = new FileReader(stackemCache);
            final BufferedReader bufferedReader = new BufferedReader(reader);
            final List<String> collector = new ArrayList<String>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                collector.add(line);
            }

            bufferedReader.close();
            reader.close();

            return collector.toArray(new String[0]);
        } catch (IOException e) {
            SystemIOTools.log("Failed to read cache file properly, expect problems!");
            e.printStackTrace();
            return new String[0];
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static void writeCacheData(File configFolder, String[] cache) {
        final File stackemCache = new File(configFolder, "stackem.cache");

        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        try {
            final FileWriter writer = new FileWriter(stackemCache);

            for (int i = 0; i < cache.length; i++) {
                writer.write(cache[i]);
                writer.write('\n');
            }

            writer.close();
        } catch (IOException e) {
            SystemIOTools.log("Failed to write cache properly, expect reset next session!");
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
