package net.tracystacktrace.stackem.modloader.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.TexturePackDefault;
import net.minecraft.src.mod_StackEmNeptune;
import net.tracystacktrace.stackem.modloader.ModLoaderStackedImpl;
import net.tracystacktrace.stackem.neptune.imageglue.ImageGlueBridge;
import net.tracystacktrace.stackem.modloader.patch.CompatibilityTools;
import net.tracystacktrace.stackem.neptune.container.PreviewTexturePack;
import net.tracystacktrace.stackem.neptune.fetch.FetchMaster;
import net.tracystacktrace.stackem.tools.CacheConfig;
import net.tracystacktrace.stackem.tools.SystemIOTools;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiTextureStack extends GuiScreen {
    public List<PreviewTexturePack> sequoiaCache;

    private final String hint1;
    private final String hint2;
    private final String actions;
    private final GuiScreen parentScreen;

    private GuiTextureStackSlot slotManager;
    private GuiButtonHover buttonMoveUp;
    private GuiButtonHover buttonMoveDown;
    private GuiButtonHover buttonToggle;
    private GuiButtonHover buttonSecurityString;

    private boolean clickedAtLeastOnce;
    private boolean anyChangesMade = false;

    public GuiTextureStack(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;

        this.hint1 = CompatibilityTools.translateKey("stackem.gui.hint1");
        this.hint2 = CompatibilityTools.translateKey("stackem.gui.hint2");
        this.actions = CompatibilityTools.translateKey("stackem.gui.actions");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        this.clickedAtLeastOnce = false;
        this.controlList.clear();
        this.fetchCacheFromOuterworld();

        // texture pack folder
        final GuiButtonHover openFolder = new GuiButtonHover(-1, this.width - 150, this.height - 25, 20, 20, CompatibilityTools.translateKey("stackem.icon.folder"), CompatibilityTools.translateKey("stackem.gui.folder"));
        openFolder.canDisplayInfo = true;
        this.controlList.add(openFolder);

        // save & close
        this.controlList.add(new GuiButtonHover(-2, this.width - 125, this.height - 25, 120, 20, CompatibilityTools.translateKey("stackem.gui.done"), null));

        // slot manager
        this.slotManager = new GuiTextureStackSlot(this.mc, this, this.width, this.height);
        this.slotManager.registerScrollButtons(null, 7, 8);

        // action buttons
        this.controlList.add(this.buttonToggle = new GuiButtonHover(-105, 5, 20 + 46, 16, 16, CompatibilityTools.translateKey("stackem.icon.cross"), CompatibilityTools.translateKey("stackem.button.remove")));
        this.controlList.add(this.buttonSecurityString = new GuiButtonHover(-106, 5, 20 + 64, 16, 16, CompatibilityTools.translateKey("stackem.icon.info"), CompatibilityTools.translateKey("stackem.button.sha256")));
        this.controlList.add(this.buttonMoveDown = new GuiButtonHover(-104, 5, 20 + 18, 16, 16, CompatibilityTools.translateKey("stackem.icon.moveDown"), CompatibilityTools.translateKey("stackem.button.movedown")));
        this.controlList.add(this.buttonMoveUp = new GuiButtonHover(-103, 5, 20, 16, 16, CompatibilityTools.translateKey("stackem.icon.moveUp"), CompatibilityTools.translateKey("stackem.button.moveup")));


        this.buttonToggle.enabled = false;
        this.buttonToggle.drawButton = false;
        this.buttonToggle.canDisplayInfo = true;
        this.buttonMoveUp.enabled = false;
        this.buttonMoveUp.drawButton = false;
        this.buttonMoveUp.canDisplayInfo = true;
        this.buttonMoveDown.enabled = false;
        this.buttonMoveDown.drawButton = false;
        this.buttonMoveDown.canDisplayInfo = true;
        this.buttonSecurityString.enabled = false;
        this.buttonSecurityString.drawButton = false;
        this.buttonSecurityString.canDisplayInfo = true;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {

            if (button.id == -1) {
                try {
                    Desktop.getDesktop().open(new File(Minecraft.getMinecraftDir(), "texturepacks"));
                } catch (IOException ignored) {
                }
                return;
            }

            if (button.id == -2) {
                this.anyChangesMade = true;
                this.pushChangesGlobally();
                this.mc.displayGuiScreen(this.parentScreen);
                return;
            }

            if (button.id == -103) {
                this.moveUpElement(slotManager.selectedIndex);
                return;
            }

            if (button.id == -104) {
                this.moveDownElement(slotManager.selectedIndex);
                return;
            }

            if (button.id == -105) {
                this.slotManager.elementClicked(slotManager.selectedIndex, true);
                return;
            }

            if (button.id == -106) {
                final String securityString = sequoiaCache.get(this.slotManager.selectedIndex).sha256;
                if (securityString != null && !securityString.equals("N/A")) {
                    SystemIOTools.setClipboardText(securityString);
                }
                return;
            }

            this.slotManager.actionPerformed(button);
        }
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void drawScreen(int mouseX, int mouseY, float deltaTicks) {
        this.drawDefaultBackground();
        this.slotManager.drawScreen(mouseX, mouseY, deltaTicks);

        if (this.clickedAtLeastOnce) {
            drawString(fontRenderer, actions, 5, 7, 0xFFFFFFFF);
        }

        drawString(fontRenderer, hint1, 3, this.height - 14, 0xFFFFFFFF);
        drawString(fontRenderer, hint2, 3, this.height - 26, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, deltaTicks);

        // draw hover strings
        for (int i = 0; i < controlList.size(); i++) {
            final GuiButton button = (GuiButton) controlList.get(i);
            if (button instanceof GuiButtonHover) {
                ((GuiButtonHover) button).drawHoverString(fontRenderer, mouseX, mouseY);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.sequoiaCache.forEach(pack -> mc.renderEngine.deleteTexture(pack.popTextureIndex()));
        if (!this.anyChangesMade) {
            //This is initially caused by the way how initGui of original GuiTexturePacks works
            //it calls the updateAvailableTextures() that resets the current internal stacked instance, borking everything
            //we need to force push to fix it
            mod_StackEmNeptune.applyCachedTexturepackStack(mc, false, new File(Minecraft.getMinecraftDir(), "config"));
        }
    }

    public void updateMoveButtonsState(int index) {
        this.clickedAtLeastOnce = true;
        if (sequoiaCache.get(index).isInStack()) {
            this.buttonToggle.enabled = true;
            this.buttonToggle.drawButton = true;
            this.buttonToggle.displayString = CompatibilityTools.translateKey("stackem.icon.cross");
            this.buttonToggle.hoverString = CompatibilityTools.translateKey("stackem.button.remove");

            this.buttonMoveUp.drawButton = true;
            this.buttonMoveDown.drawButton = true;

            this.buttonMoveUp.enabled = index > 0;
            this.buttonMoveDown.enabled = index + 1 < this.countInStackElements();

        } else {
            this.buttonToggle.enabled = true;
            this.buttonToggle.drawButton = true;
            this.buttonToggle.displayString = CompatibilityTools.translateKey("stackem.icon.tick");
            this.buttonToggle.hoverString = CompatibilityTools.translateKey("stackem.button.add");

            this.buttonMoveUp.enabled = false;
            this.buttonMoveUp.drawButton = false;
            this.buttonMoveDown.enabled = false;
            this.buttonMoveDown.drawButton = false;
        }

        final PreviewTexturePack texturePack = sequoiaCache.get(index);
        this.buttonSecurityString.enabled = texturePack.sha256 != null && !"N/A".equals(texturePack.sha256);
        this.buttonSecurityString.drawButton = true;
    }

    /* code to obtain info from outside */

    private void fetchCacheFromOuterworld() {
        final String[] previousCached = CacheConfig.getCacheData(new File(Minecraft.getMinecraftDir(), "config"));
        final File texturepacksFolder = new File(Minecraft.getMinecraftDir(), "texturepacks");
        final List<PreviewTexturePack> candidates = FetchMaster.collectPreviews(texturepacksFolder);

        if (candidates == null) {
            this.sequoiaCache = new ArrayList<>();
            return;
        }

        for (int i = 0; i < previousCached.length; i++) {
            String s = previousCached[i];
            for (PreviewTexturePack q : candidates) {
                if (q.getName().equals(s)) {
                    q.order = i;
                }
            }
        }

        this.sequoiaCache = candidates;
        this.pushSequoiaCacheSort();
    }

    /* slot interactive code */

    public int getSequoiaCacheSize() {
        return sequoiaCache.size();
    }

    public void pushSequoiaCacheSort() {
        sequoiaCache.sort((o1, o2) -> {
            if (o1.isInStack() && o2.isInStack()) return Integer.compare(o1.order, o2.order);
            if (o1.isInStack()) return -1;
            if (o2.isInStack()) return 1;
            return o1.getName().compareTo(o2.getName());
        });
    }

    public void recalculateStack() {
        for (int i = 0; i < sequoiaCache.size(); i++) {
            if (sequoiaCache.get(i).isInStack()) {
                sequoiaCache.get(i).order = i;
            }
            //skip
            if (sequoiaCache.get(i).order == -1) {
                break;
            }
        }
    }

    public PreviewTexturePack getSequoiaCacheElement(int index) {
        return sequoiaCache.get(index);
    }

    public boolean isSequoiaCacheElementInStack(int index) {
        return sequoiaCache.get(index).isInStack();
    }

    public int countInStackElements() {
        return (int) sequoiaCache.stream().filter(PreviewTexturePack::isInStack).count();
    }

    public void addElementToStack(int index) {
        sequoiaCache.get(index).order = countInStackElements();
        this.pushSequoiaCacheSort();
    }

    public void removeElementFromStack(int index) {
        sequoiaCache.get(index).order = -1;
        this.pushSequoiaCacheSort();
        this.recalculateStack();
    }

    public void pushChangesGlobally() {
        this.pushSequoiaCacheSort();
        List<File> files = new ArrayList<>();
        List<String> stackemList = new ArrayList<>();

        for (int i = 0; i < this.countInStackElements(); i++) {
            files.add(this.sequoiaCache.get(i).getFile());
            stackemList.add(this.sequoiaCache.get(i).getName());
        }

        this.mc.texturePackList.selectedTexturePack.closeTexturePackFile();

        final ModLoaderStackedImpl stacked = new ModLoaderStackedImpl(new TexturePackDefault(), files);
        CacheConfig.writeCacheData(new File(Minecraft.getMinecraftDir(), "config"), stackemList.toArray(new String[0]));

        this.mc.texturePackList.setTexturePack(stacked);

        this.mc.renderEngine.refreshTextures();
        ImageGlueBridge.processTexturesSegments(mod_StackEmNeptune.getGameTextureManager());

        this.mc.renderGlobal.loadRenderers();

        //leftovers from StackEm ReIndev
        //this.mc.fontRenderer = new FontRenderer(StackEm.getContainerInstance(), this.mc.renderEngine);
        //SoundCleanupHelper.cleanupSoundSources(this.mc.sndManager);
        //this.mc.sndManager.refreshSounds(stacked);
        //this.mc.sndManager.onSoundOptionsChanged();

        Display.update();
    }

    public void moveUpElement(int index) {
        sequoiaCache.get(index - 1).order = index;
        sequoiaCache.get(index).order = index - 1;
        this.pushSequoiaCacheSort();
        slotManager.elementClicked(index - 1, false);
    }

    public void moveDownElement(int index) {
        sequoiaCache.get(index + 1).order = index;
        sequoiaCache.get(index).order = index + 1;
        this.pushSequoiaCacheSort();
        slotManager.elementClicked(index + 1, false);
    }

    public void drawGradientRectPublic(int var1, int var2, int var3, int var4, int var5, int var6) {
        this.drawGradientRect(var1, var2, var3, var4, var5, var6);
    }
}
