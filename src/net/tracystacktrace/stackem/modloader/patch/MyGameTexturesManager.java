package net.tracystacktrace.stackem.modloader.patch;

import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderEngine;
import net.tracystacktrace.stackem.modloader.ModLoaderStackedImpl;
import net.tracystacktrace.stackem.neptune.IGameTexturesManager;
import net.tracystacktrace.stackem.neptune.container.ZipDrivenTexturePack;
import net.tracystacktrace.stackem.tools.ImageHelper;
import net.tracystacktrace.stackem.tools.UnsafeInstance;

import java.awt.image.BufferedImage;
import java.util.Map;

public class MyGameTexturesManager implements IGameTexturesManager {
    protected final RenderEngine renderEngine;

    public MyGameTexturesManager(RenderEngine renderEngine) {
        this.renderEngine = renderEngine;
    }

    @Override
    public void push(String texture, BufferedImage image) {
        if (this.textureMap_containsKey(texture)) {
            //hack solution - simply overwrite the texture with the in-built code
            final int id = textureMap_getInt(texture);
            renderEngine.setupTexture(image, id);
        } else {
            //no id present - we will add it then
            final int loc = renderEngine.allocateAndSetupTexture(image);
            textureMap_setInt(texture, loc);
        }
    }

    @Override
    public ZipDrivenTexturePack[] getArchives() {
        return ((ModLoaderStackedImpl)ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack).getArchives();
    }

    @Override
    public BufferedImage getDefaultTexture(String texture) {
        return ImageHelper.readImageAndClose(((ModLoaderStackedImpl)ModLoader.getMinecraftInstance().texturePackList.selectedTexturePack).getDefaultTexturePack().getResourceAsStream(texture));
    }

    /**
     * Deobfuscated name: textureMap
     * <br>
     * Current obfuscated name: d
     */
    @SuppressWarnings("rawtypes")
    protected Map getTextureMap() {
        return UnsafeInstance.getObject(RenderEngine.class, renderEngine, CompatibilityTools.OBFUSCATED_ENV ? "d" : "textureMap");
    }

    protected boolean textureMap_containsKey(String s) {
        return getTextureMap().containsKey(s);
    }

    protected int textureMap_getInt(String s) {
        return (int) getTextureMap().get(s);
    }

    @SuppressWarnings("unchecked")
    protected void textureMap_setInt(String s, int i) {
        getTextureMap().put(s, i);
    }
}
