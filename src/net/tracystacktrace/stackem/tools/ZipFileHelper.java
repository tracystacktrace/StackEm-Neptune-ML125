package net.tracystacktrace.stackem.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipFileHelper {
    @SuppressWarnings("Convert2Diamond")
    public static String[] readTextFile(ZipFile file, String location) {
        if (location.startsWith("/")) {
            location = location.substring(1);
        }

        final ZipEntry entry = file.getEntry(location);
        if (entry == null) {
            return null;
        }

        try {
            final InputStream inputStream = file.getInputStream(entry);
            //noinspection CharsetObjectCanBeUsed
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            final List<String> collector = new ArrayList<String>();

            String line;
            while ((line = reader.readLine()) != null) {
                collector.add(line);
            }

            reader.close();
            inputStream.close();

            return collector.toArray(new String[0]);
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage readImage(ZipFile file, String location) {
        if (location.startsWith("/")) {
            location = location.substring(1);
        }

        final ZipEntry entry = file.getEntry(location);
        if (entry == null) {
            return null;
        }

        try {
            final InputStream inputStream = file.getInputStream(entry);
            final BufferedImage returnValue = ImageIO.read(inputStream);
            inputStream.close();
            return returnValue;
        } catch (IOException ignored) {
            return null;
        }
    }
}
