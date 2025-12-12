package net.tracystacktrace.stackem.modloader.gui;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;

public class GuiButtonHover extends GuiButton {

    protected String hoverString;
    public boolean canDisplayInfo = false;

    private int fixButtonLoc = -1;

    public GuiButtonHover(int id, int x, int y, int w, int h, String s, String hoverString) {
        super(id, x, y, w, h, s);
        this.hoverString = hoverString;
    }

    public void drawHoverString(FontRenderer fontRenderer, int mouseX, int mouseY) {
        if (!this.canDisplayInfo || !this.drawButton) {
            return;
        }

        if (!this.mouseHovered(mouseX, mouseY)) {
            return;
        }

        final int string_width = fontRenderer.getStringWidth(this.hoverString);
        drawRect(mouseX + 5, mouseY + 5, mouseX + 10 + string_width + 2, mouseY + 10 + 4 + 6, -1073741824);
        fontRenderer.drawString(this.hoverString, mouseX + 8, mouseY + 8, 16777120);
    }

    @Override
    public void drawCenteredString(FontRenderer fontRenderer, String s, int x, int y, int color) {
        if (fixButtonLoc == -1) {
            fixButtonLoc = countColorChars(s) * 3;
        }
        super.drawCenteredString(fontRenderer, s, x + fixButtonLoc, y, color);
    }

    protected boolean mouseHovered(int mouseX, int mouseY) {
        return mouseX >= this.xPosition && mouseX < this.xPosition + this.field_52008_a && mouseY >= this.yPosition && mouseY < this.yPosition + this.field_52007_b;
    }

    private static short countColorChars(String s) {
        if (s == null || s.isEmpty()) return 0;

        short count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '§') count++;
        }

        return count;
    }
}
