package net.tracystacktrace.stackem.neptune.container;

import java.awt.image.BufferedImage;
import java.io.File;

public class PreviewTexturePack extends ContainerTexturePack {
    public final String firstLine;
    public final String secondLine;
    public final String sha256;

    protected BufferedImage icon; //icon image
    protected int iconTextureID = -1; //for OpenGL

    public PreviewTexturePack(
            File archiveFile,
            String name,
            String firstLine,
            String secondLine,
            String sha256
    ) {
        super(archiveFile, name);
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.sha256 = sha256;
    }

    /* Icon Management Methods */

    public void setIcon(BufferedImage icon) {
        this.icon = icon;
    }

    public boolean hasIcon() {
        return this.icon != null;
    }

    public BufferedImage getIcon() {
        return this.icon;
    }

    public boolean hasTextureIndex() {
        return this.iconTextureID != -1;
    }

    public int getTextureIndex() {
        return this.iconTextureID;
    }

    public void setTextureIndex(int i) {
        this.iconTextureID = i;
    }

    public int popTextureIndex() {
        if (this.iconTextureID != -1) {
            int returnInt = this.iconTextureID;
            this.iconTextureID = -1;
            this.icon = null;
            return returnInt;
        }
        return this.iconTextureID;
    }
}
