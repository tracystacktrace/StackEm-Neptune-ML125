package net.tracystacktrace.stackem.modloader.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;
import net.tracystacktrace.stackem.neptune.container.PreviewTexturePack;
import org.lwjgl.opengl.GL11;

public class GuiTextureStackSlot extends GuiSlot {
    protected GuiTextureStack parentScreen;
    protected Minecraft minecraft;
    public int selectedIndex = -1;

    protected int right;

    public GuiTextureStackSlot(Minecraft minecraft, GuiTextureStack parentScreen, int width, int height) {
        super(minecraft, width, height, 0, height - 30, 36);
        this.parentScreen = parentScreen;
        this.minecraft = minecraft;
        this.right = width;
    }

    @Override
    protected int getSize() {
        return parentScreen.getSequoiaCacheSize();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        this.selectedIndex = index;
        if (doubleClick) {
            if (parentScreen.isSequoiaCacheElementInStack(index)) {
                parentScreen.removeElementFromStack(index);
            } else {
                parentScreen.addElementToStack(index);
            }
        }
        parentScreen.updateMoveButtonsState(index);
    }

    private void bindTexturePackIcon(RenderEngine renderEngine, PreviewTexturePack pack) {
        if (!pack.hasTextureIndex()) {
            pack.setTextureIndex(renderEngine.allocateAndSetupTexture(pack.getIcon()));
        }
        renderEngine.bindTexture(pack.getTextureIndex());
    }

    @Override
    protected boolean isSelected(int index) {
        return this.selectedIndex == index;
    }

    @Override
    protected int getContentHeight() {
        return getSize() * 36;
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    protected void drawSlot(int index, int x, int y, int iconHeight, Tessellator tessellator) {
        final PreviewTexturePack tag = parentScreen.getSequoiaCacheElement(index);
        final boolean isSelectedOne = this.selectedIndex == index;

        if (tag.hasIcon()) {
            bindTexturePackIcon(minecraft.renderEngine, tag);
        } else {
            minecraft.renderEngine.bindTexture(minecraft.renderEngine.getTexture("/gui/unknown_pack.png"));
        }

        if (tag.isInStack()) {
            parentScreen.drawGradientRectPublic(x, y, x + 216, y + 32, 0xC0903AA2, 0xC0903AA2);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(16777215);
        tessellator.addVertexWithUV(x, (y + iconHeight), 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV((x + iconHeight), (y + iconHeight), 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV((x + iconHeight), y, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(x, y, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        String showName = tag.getName();
        if (tag.isInStack()) {
            showName = "[\u00A7e" + (tag.order + 1) + "\u00A7r] " + showName;
        }

        if (!isSelectedOne) {
            showName = limitString(showName, 44, true);
        }

        minecraft.fontRenderer.drawString(showName, x + iconHeight + 2, y + 1, 16777215);
        minecraft.fontRenderer.drawString(tag.firstLine, x + iconHeight + 2, y + 12, 0x00D0D0D0);
        minecraft.fontRenderer.drawString(tag.secondLine, x + iconHeight + 2, y + 12 + 11, 0x00D0D0D0);
    }

    public String limitString(String line, final int length, final boolean endDots) {
        if (line == null || line.length() < length) {
            return line;
        }

        int maxLength = length;
        int colorCodeCount = (int) line.chars().limit(maxLength - 1).filter(c -> c == '\u00A7').count();

        maxLength += colorCodeCount * 2;

        String result = line.length() > maxLength ? line.substring(0, maxLength) : line;
        if (endDots) {
            result += "...";
        }

        return result;
    }
}
