package net.tracystacktrace.stackem.modloader.imageglue;

import net.minecraft.client.Minecraft;
import net.tracystacktrace.stackem.modloader.imageglue.segment.SegmentedTexture;
import net.tracystacktrace.stackem.tools.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageGlueContainer {
    public BufferedImage original;
    public BufferedImage canvas;

    private final int original_width;

    public ImageGlueContainer(final BufferedImage image) {
        this.original = image;
        //basically copy the image into an independent object
        this.canvas = ImageHelper.fullCopy(image);

        this.original_width = original.getWidth();
    }

    private boolean rescaleContainer(int width, int height) {
        //refer to scaling the result
        if (original.getWidth() < width || original.getHeight() < height) {
            this.original = ImageHelper.scaleImage(original, width, height);
            this.canvas = ImageHelper.scaleImage(canvas, width, height);
            return true;
        }
        return false; //signal to scale not the original, but modified version
    }

    public int makeChanges(BufferedImage attempt, SegmentedTexture holder) {
        if (attempt.getWidth() != this.original.getWidth()) {
            if (!rescaleContainer(attempt.getWidth(), attempt.getHeight())) {
                attempt = ImageHelper.scaleImage(attempt, original.getWidth(), original.getHeight());
            }
        }

        final int width = this.original.getWidth();
        final int height = this.original.getHeight();
        final int scale = width / original_width;

        final boolean[] modParts = holder.genEmptyArray();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int origPixel = this.original.getRGB(x, y);
                int modPixel = attempt.getRGB(x, y);

                if (origPixel != modPixel) {
                    int test1 = holder.getSegmentIndex(x, y, scale);
                    if (test1 > -1) {
                        modParts[test1] = true;
                    }
                }
            }
        }

        int amount = 0;
        for (int i = 0; i < modParts.length; i++) {
            if (modParts[i]) {
                final int x = holder.segments[i][0] * scale;
                final int y = holder.segments[i][1] * scale;
                final int endX = (x + holder.segments[i][2]) * scale;
                final int endY = (y + holder.segments[i][3]) * scale;

                for (int movY = y; movY < endY; movY++) {
                    for (int movX = x; movX < endX; movX++) {
                        this.canvas.setRGB(movX, movY, attempt.getRGB(movX, movY));
                    }
                }
                amount++;
            }
        }

        return amount;
    }

    public void flush() {
        this.original.flush();
    }

    public void debugSave(String name) {
        try {
            ImageIO.write(canvas, "png", new File(Minecraft.getMinecraftDir(), name + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
