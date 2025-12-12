package net.tracystacktrace.stackem.neptune.container;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipDrivenTexturePack extends ContainerTexturePack {
    protected ZipFile archive;

    public ZipDrivenTexturePack(File archiveFile, String name) {
        super(archiveFile, name);
    }

    public void openArchive() throws IOException {
        if (this.archive != null) {
            System.out.println("Method \"openArchive\" failed: the archive is already open!");
            return;
        }
        this.archive = new ZipFile(this.file);
    }

    public void closeArchive() throws IOException {
        if (this.archive != null) {
            this.archive.close();
        }
    }

    public URL getFullUrlOf(ZipEntry entry) throws MalformedURLException {
        return new URL("jar:file:" + this.file.getAbsolutePath() + "!/" + entry.getName());
    }

    public ZipEntry getEntry(String path) {
        if (this.archive == null) {
            return null;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return this.archive.getEntry(path);
    }

    public InputStream getInputStreamOf(String path) throws IOException {
        final ZipEntry entry = this.getEntry(path);
        if (entry == null) {
            throw new IOException(String.format("Failed to find entry for: %s", path));
        }
        return this.getInputStreamOf(entry);
    }

    public InputStream getInputStreamOf(ZipEntry entry) throws IOException {
        if (this.archive == null) {
            throw new IllegalStateException("Cannot access getEntry when the archive is not opened!");
        }
        return this.archive.getInputStream(entry);
    }

    public boolean hasEntry(String path) {
        return this.getEntry(path) != null;
    }

    public BufferedImage readImage(String location) {
        final ZipEntry entry = this.getEntry(location);

        if (entry == null) {
            return null;
        }

        try (InputStream inputStream = this.getInputStreamOf(entry)) {
            return ImageIO.read(inputStream);
        } catch (IOException ignored) {
            return null;
        }
    }
}
