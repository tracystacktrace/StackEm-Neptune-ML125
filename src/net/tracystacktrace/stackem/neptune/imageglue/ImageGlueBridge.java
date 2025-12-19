package net.tracystacktrace.stackem.neptune.imageglue;

import net.tracystacktrace.stackem.neptune.IGameTexturesManager;
import net.tracystacktrace.stackem.neptune.imageglue.segment.SegmentedTexture;
import net.tracystacktrace.stackem.neptune.imageglue.segment.SegmentsProvider;
import net.tracystacktrace.stackem.neptune.container.ZipDrivenTexturePack;
import net.tracystacktrace.stackem.tools.ImageHelper;
import net.tracystacktrace.stackem.tools.SystemIOTools;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageGlueBridge {
    public static void processTexturesSegments(IGameTexturesManager texturesManager) {
        if (SegmentsProvider.TEXTURES == null) {
            SystemIOTools.log("No image glue description file is provided, ignoring!");
            return;
        }

        //first step - segmented textures
        for (final SegmentedTexture value : SegmentsProvider.TEXTURES) {
            //process texture layering, get a layered texture
            texturesManager.push(value.texture, processLayering(value, texturesManager));
        }
    }

    private static BufferedImage processLayering(SegmentedTexture name, IGameTexturesManager texturesManager) {
        final ImageGlueContainer original = new ImageGlueContainer(texturesManager.getDefaultTexture(name.texture));
        final List<BufferedImage> images = new ArrayList<>();

        //fetching texturepacks that can into gluing
        final ZipDrivenTexturePack[] archives = texturesManager.getArchives();
        for (int i = archives.length - 1; i >= 0; i--) {
            final BufferedImage image = archives[i].readImage(name.texture);
            if (ImageHelper.isValidSquareTexture(image)) {
                images.add(image);
            } else if (image != null) {
                image.flush();
            }
        }

        //actual gluing process
        int changesNum = 0;

        for (final BufferedImage attack : images) {
            changesNum += original.makeChanges(attack, name);
        }

        if (changesNum != 0) {
            SystemIOTools.log(String.format("Overwrote %s image segments for %s", changesNum, name.texture));
        } else {
            SystemIOTools.log(String.format("No image segments gluing candidates were found for %s", name.texture));
        }

        //clean-up process
        for (BufferedImage image : images) {
            image.flush();
        }
        images.clear();

        original.flush();
        return original.canvas;
    }
}
