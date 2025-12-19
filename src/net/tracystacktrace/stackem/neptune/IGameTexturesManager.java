package net.tracystacktrace.stackem.neptune;

import net.tracystacktrace.stackem.neptune.container.ZipDrivenTexturePack;

import java.awt.image.BufferedImage;

public interface IGameTexturesManager {
    /**
     * This is a simple, yet big function that is required to either
     * create a texture or replace the existing texture in the game
     * @param texture Texture path
     * @param image New texture image
     */
    void push(String texture, BufferedImage image);

    ZipDrivenTexturePack[] getArchives();

    BufferedImage getDefaultTexture(String texture);
}
