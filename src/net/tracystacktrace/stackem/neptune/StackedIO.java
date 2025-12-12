package net.tracystacktrace.stackem.neptune;

import net.tracystacktrace.stackem.neptune.container.ZipDrivenTexturePack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class StackedIO {
    private final ZipDrivenTexturePack[] archives;
    private boolean initialized = false;

    public StackedIO(List<File> texturepackArchives) {
        this.archives = new ZipDrivenTexturePack[texturepackArchives.size()];

        for (int i = 0; i < this.archives.length; i++) {
            final File candidate = texturepackArchives.get(i);
            this.archives[i] = new ZipDrivenTexturePack(candidate, candidate.getName());
            this.archives[i].order = i;
        }
    }

    public ZipDrivenTexturePack[] getArchives() {
        return this.archives;
    }

    public void initialize() {
        if (!this.initialized) {
            for (int i = 0; i < this.archives.length; i++) {
                final ZipDrivenTexturePack archive = this.archives[i];
                try {
                    archive.openArchive();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.printf("[Stack 'Em] Failed to OPEN texturepack \"%s\" at [%s], reason: %s\n", archive.getName(), archive.getAbsolutePath(), e.getMessage());
                }
            }
            this.initialized = true;
        }
    }

    public void collapse() {
        for (int i = 0; i < this.archives.length; i++) {
            final ZipDrivenTexturePack archive = this.archives[i];
            try {
                archive.closeArchive();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("[Stack 'Em] Failed to CLOSE texturepack \"%s\" at [%s], reason: %s\n", archive.getName(), archive.getAbsolutePath(), e.getMessage());
            }
        }
    }

    public InputStream getInputStream(String resourcePath) throws IOException {
        this.initialize();
        for (int i = 0; i < this.archives.length; i++) {
            final ZipEntry entry = this.archives[i].getEntry(resourcePath);
            if (entry != null) {
                return this.archives[i].getInputStreamOf(entry);
            }
        }
        throw new FileNotFoundException(resourcePath);
    }

    public URL find(String resourcePath) {
        try {
            this.initialize();
        } catch (Exception e) {
            return null;
        }

        for (int i = 0; i < this.archives.length; i++) {
            final ZipEntry entry = this.archives[i].getEntry(resourcePath);
            if (entry != null) {
                try {
                    final URL url = this.archives[i].getFullUrlOf(entry);

                    final String friendlyOutput = url.toString();
                    System.out.printf("[Stack 'Em] Loading resource from: %s\n", friendlyOutput.substring(friendlyOutput.lastIndexOf(File.separatorChar, friendlyOutput.indexOf("!/")) + 1).replace("!", "\": "));

                    return url;
                } catch (MalformedURLException ignored) {
                }
            }
        }

        return null;
    }

    public boolean fileExists(String resourcePath) {
        try {
            this.initialize();
            for (int i = 0; i < this.archives.length; i++) {
                final ZipEntry entry = this.archives[i].getEntry(resourcePath);
                if (entry != null && !entry.isDirectory()) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean directoryExists(String resourcePath) {
        try {
            this.initialize();
            for (int i = 0; i < this.archives.length; i++) {
                final ZipEntry entry = this.archives[i].getEntry(resourcePath);
                if (entry != null && entry.isDirectory()) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean isEmpty() {
        return this.archives == null || this.archives.length == 0;
    }
}
