package net.tracystacktrace.stackem.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipFileHelper {
    @FunctionalInterface
    public interface FunctionException<T, R, E extends Exception> {
        R apply(T a) throws E;
    }

    public static <T> T readTextFile(
            ZipFile file,
            String location,
            FunctionException<BufferedReader, T, IOException> generator
    ) {
        if (location.startsWith("/")) {
            location = location.substring(1);
        }

        final ZipEntry entry = file.getEntry(location);
        if (entry == null) {
            return null;
        }

        try (InputStream inputStream = file.getInputStream(entry);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return generator.apply(reader);
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage readImage(
            ZipFile file,
            String location
    ) {
        if (location.startsWith("/")) {
            location = location.substring(1);
        }

        final ZipEntry entry = file.getEntry(location);
        if (entry == null) {
            return null;
        }

        try (InputStream inputStream = file.getInputStream(entry)) {
            return ImageIO.read(inputStream);
        } catch (IOException ignored) {
            return null;
        }
    }
}
