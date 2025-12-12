package net.tracystacktrace.stackem.modloader;

import net.minecraft.client.Minecraft;
import net.minecraft.src.TexturePackBase;
import net.tracystacktrace.stackem.neptune.StackedIO;
import net.tracystacktrace.stackem.neptune.container.ZipDrivenTexturePack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ModLoaderStackedImpl extends TexturePackBase {
    private final TexturePackBase defaultTexturePack;
    private final StackedIO engine;

    public ModLoaderStackedImpl(
            TexturePackBase defaultTexturePack,
            List<File> texturepackArchives
    ) {
        this.texturePackFileName = "stackem-" + Math.random();
        this.defaultTexturePack = defaultTexturePack;
        this.engine = new StackedIO(texturepackArchives);
    }

    @Override
    public void func_6482_a() {
        this.engine.initialize();
    }

    @Override
    public void closeTexturePackFile() {
        this.engine.collapse();
    }

    @Override
    public void func_6485_a(Minecraft client) throws IOException {
        this.firstDescriptionLine = "Stack 'Em Internal Object";
        this.secondDescriptionLine = "Do not touch, use or look.";
    }

    @Override
    public void unbindThumbnailTexture(Minecraft var1) {}

    @Override
    public void bindThumbnailTexture(Minecraft var1) {}

    @Override
    public InputStream getResourceAsStream(String resourcePath) {
        try {
            return this.engine.getInputStream(resourcePath);
        } catch (IOException ignored) {
        }
        return defaultTexturePack.getResourceAsStream(resourcePath);
    }

    public TexturePackBase getDefaultTexturePack() {
        return this.defaultTexturePack;
    }

    public ZipDrivenTexturePack[] getArchives() {
        return this.engine.getArchives();
    }
}
