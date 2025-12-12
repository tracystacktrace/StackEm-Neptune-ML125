package net.tracystacktrace.stackem.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class ImageHelper {
    @FunctionalInterface
    public interface InnerFunc1<T, R, E extends Exception> {
        R apply(T a) throws E;
    }

    /**
     * Creates a full copy of the ARGB image that is separate object with no any kind of attachment. A simple {@link Object#clone()} won't really work in most cases, so why not?
     * <br>
     * This is useful when a manipulation on the image is required with the preservation of the original in other place.
     * @param original Original image to be duplicated
     * @return A duplicated image object
     */
    public static BufferedImage fullCopy(BufferedImage original) {
        final BufferedImage copy = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = copy.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        return copy;
    }

    /**
     * A simple check if the image is square and not null, useful for some item or block textures to be analysed quickly.
     * @param check Image object to check
     * @return True of the image is square and not null
     */
    public static boolean isValidSquareTexture(BufferedImage check) {
        return check != null && check.getWidth() == check.getHeight();
    }

    public static BufferedImage scaleImage(
            BufferedImage original,
            int targetWidth,
            int targetHeight
    ) {
        final Image resultingImage = original.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        final BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

    public static BufferedImage readImage(InnerFunc1<String, InputStream, IOException> getResourceAsStream, String name) {
        try {
            final InputStream inputStream = getResourceAsStream.apply(name);
            final BufferedImage image = ImageIO.read(inputStream);
            inputStream.close();
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

