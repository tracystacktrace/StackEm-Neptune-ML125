package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.tracystacktrace.stackem.modloader.ModLoaderStackedImpl;
import net.tracystacktrace.stackem.modloader.gui.GuiTextureStack;
import net.tracystacktrace.stackem.neptune.IGameTexturesManager;
import net.tracystacktrace.stackem.neptune.imageglue.ImageGlueBridge;
import net.tracystacktrace.stackem.neptune.imageglue.segment.SegmentsProvider;
import net.tracystacktrace.stackem.modloader.patch.CompatibilityTools;
import net.tracystacktrace.stackem.modloader.patch.MyGameTexturesManager;
import net.tracystacktrace.stackem.modloader.patch.QuickEntityRenderer;
import net.tracystacktrace.stackem.tools.CacheConfig;
import net.tracystacktrace.stackem.tools.SystemIOTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class mod_StackEmNeptune extends BaseMod {

    private static IGameTexturesManager mod1;

    public static IGameTexturesManager getGameTextureManager() {
        if (mod1 == null) {
            mod1 = new MyGameTexturesManager(ModLoader.getMinecraftInstance().renderEngine);
        }
        return mod1;
    }

    public static void applyCachedTexturepackStack(Minecraft client, boolean init, File configFolder) {
        // Fallback to default texturepack

        final List<File> collector = new ArrayList<>();
        final String[] candidates = CacheConfig.getCacheData(configFolder);
        final File[] files = CacheConfig.getPossibleTexturePacks(Minecraft.getMinecraftDir());

        // Stream to collect enough data
        Arrays.stream(candidates)
                .map(c -> Arrays.stream(files)
                        .filter(f -> f.getName().toLowerCase().endsWith(".zip"))
                        .filter(f -> f.getName().contains(c))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .forEach(collector::add);

        if (init) {
            SystemIOTools.log("How many texturepacks were pre-fetched? " + collector.size());
        }

        if (client.texturePackList.selectedTexturePack != null) {
            client.texturePackList.selectedTexturePack.closeTexturePackFile();
        }
        client.texturePackList.selectedTexturePack = new TexturePackDefault();

        // Force set current texturepack as StackEm internal implementation
        if (init) {
            client.texturePackList.selectedTexturePack = new ModLoaderStackedImpl(client.texturePackList.selectedTexturePack, collector);
            client.texturePackList.selectedTexturePack.func_6482_a();
        } else {
            client.texturePackList.setTexturePack(new ModLoaderStackedImpl(client.texturePackList.selectedTexturePack, collector));
            client.renderEngine.refreshTextures();
        }

        ImageGlueBridge.processTexturesSegments(mod_StackEmNeptune.getGameTextureManager());
    }

    @Override
    public String getVersion() {
        return "1.2";
    }

    @Override
    public String getName() {
        return "Stack 'Em Neptune";
    }

    @Override
    public void load() {
        // Apply quick proxy for faster tick processing
        final Minecraft client = ModLoader.getMinecraftInstance();
        client.entityRenderer = new QuickEntityRenderer(client);
    }

    @Override
    public void modsLoaded() {
        if (!(ModLoader.getMinecraftInstance().entityRenderer instanceof QuickEntityRenderer)) {
            SystemIOTools.log("Warning! Something cancelled custom EntityRenderer code; are you using OverrideAPI?");
            ModLoader.setInGameHook(this, true, false);
            ModLoader.setInGUIHook(this, true, false);
        }
    }

    @Override
    public boolean onTickInGUI(float tick, Minecraft client, GuiScreen gui) {
        if (gui != null && gui.getClass().isAssignableFrom(GuiTexturePacks.class)) {
            client.displayGuiScreen(new GuiTextureStack(((GuiTexturePacks) gui).guiScreen));
        }
        return true;
    }

    @Override
    public boolean onTickInGame(float tick, Minecraft client) {
        if (client.currentScreen != null && client.currentScreen.getClass().isAssignableFrom(GuiTexturePacks.class)) {
            client.displayGuiScreen(new GuiTextureStack(((GuiTexturePacks) client.currentScreen).guiScreen));
        }
        return true;
    }

    static {
        SystemIOTools.log("Preparing the environment, thinking very hard!");
        CompatibilityTools.getKnownWithEnvironment();
        CompatibilityTools.obtainCurrentLang();
        SegmentsProvider.loadSegmentsData(mod_StackEmNeptune.class.getResourceAsStream("/assets/stackemneptune/stackem.segments.txt"));

        if (!CompatibilityTools.OBFUSCATED_ENV) {
            SystemIOTools.log("Running in DEV environment, no obfuscation present!");
        }

        SystemIOTools.log("Initializing mod, applying required patches");
        final Minecraft client = ModLoader.getMinecraftInstance();

        // Quickly form config folder
        final File configFolder = new File(Minecraft.getMinecraftDir(), "config");
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        applyCachedTexturepackStack(client, true, configFolder);
    }
}
