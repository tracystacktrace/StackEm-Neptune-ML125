package net.tracystacktrace.stackem.modloader.patch;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityRendererProxy;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTexturePacks;
import net.tracystacktrace.stackem.modloader.gui.GuiTextureStack;
import net.tracystacktrace.stackem.tools.UnsafeInstance;

/**
 * A very very very crude but nice way to make the GUI swap be done quicker!
 * <br>
 * However, I remember that some mods do this too, so OnTickInGUI and OnTickInGame were also added
 */
public class QuickEntityRenderer extends EntityRendererProxy {
    private final Minecraft client;

    public QuickEntityRenderer(Minecraft client) {
        super(client);
        this.client = client;
    }

    public void updateCameraAndRender(float f) {
        if (client.currentScreen != null && client.currentScreen.getClass().isAssignableFrom(GuiTexturePacks.class)) {
            client.displayGuiScreen(new GuiTextureStack(this.getHomeScreen(client.currentScreen)));
        }
        super.updateCameraAndRender(f);
    }

    private GuiScreen getHomeScreen(GuiScreen screen) {
        return UnsafeInstance.getObject(GuiTexturePacks.class, (GuiTexturePacks) screen, CompatibilityTools.OBFUSCATED_ENV ? "a" : "guiScreen");
    }
}
