package net.tracystacktrace.stackem.modloader.imageglue;

import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.TexturePackDefault;
import net.tracystacktrace.stackem.modloader.ModLoaderStackedImpl;
import net.tracystacktrace.stackem.modloader.imageglue.segment.SegmentedTexture;
import net.tracystacktrace.stackem.modloader.imageglue.segment.SegmentsProvider;
import net.tracystacktrace.stackem.modloader.patch.CompatibilityTools;
import net.tracystacktrace.stackem.modloader.patch.RenderEngineHacks;
import net.tracystacktrace.stackem.neptune.container.ZipDrivenTexturePack;
import net.tracystacktrace.stackem.tools.ImageHelper;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageGlueBridge {
    public static void processTexturesSegments(RenderEngine renderEngine) {
        if (SegmentsProvider.TEXTURES == null) {
            CompatibilityTools.log("No image glue description file is provided, ignoring!");
            return;
        }

        //first step - segmented textures
        for (final SegmentedTexture value : SegmentsProvider.TEXTURES) {
            //process texture layering, get a layered texture
            final BufferedImage image = processLayering(value);


            if (RenderEngineHacks.textureMap_containsKey(renderEngine, value.texture)) {
                //hack solution - simply overwrite the texture with the in-built code
                final int id = RenderEngineHacks.textureMap_getInt(renderEngine, value.texture);
                renderEngine.setupTexture(image, id);
            } else {
                //no id present - we will add it then
                final int loc = renderEngine.allocateAndSetupTexture(image);
                RenderEngineHacks.textureMap_setInt(renderEngine, value.texture, loc);
            }
        }
    }

    private static BufferedImage processLayering(SegmentedTexture name) {
        final ModLoaderStackedImpl stacked = ((ModLoaderStackedImpl) ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack);
        final TexturePackDefault defaultPack = (TexturePackDefault) stacked.getDefaultTexturePack();

        final ImageGlueContainer original = new ImageGlueContainer(ImageHelper.readImage(defaultPack::getResourceAsStream, name.texture));
        final List<BufferedImage> images = new ArrayList<>();

        //fetching texturepacks that can into gluing
        final ZipDrivenTexturePack[] archives = stacked.getArchives();
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
            CompatibilityTools.log(String.format("Overwrote %s image segments for %s", changesNum, name.texture));
        } else {
            CompatibilityTools.log(String.format("No image segments gluing candidates were found for %s", name.texture));
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
