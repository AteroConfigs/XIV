package pw.latematt.xiv.ui.alt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

public class AltSlot extends GuiSlot {

    private GuiAltManager screen;
    private int selected;

    public AltSlot(GuiAltManager screen, Minecraft mcIn, int width, int height, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_) {
        super(mcIn, width, height, p_i1052_4_, p_i1052_5_, p_i1052_6_);

        this.screen = screen;
    }

    @Override
    protected int getSize() {
        if (mc.currentScreen instanceof GuiAltManager) {
            GuiAltManager screen = (GuiAltManager) mc.currentScreen;
            return screen.getAccounts().size();
        } else {
            return 0;
        }
    }

    @Override
    public int getSlotHeight() {
        return super.getSlotHeight();
    }

    @Override
    protected void elementClicked(int slot, boolean var2, int var3, int var4) {
        this.selected = slot;
    }

    @Override
    protected boolean isSelected(int slot) {
        return selected == slot;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int slot) {
        this.selected = slot;
    }

    public AltAccount getAlt() {
        return getAlt(getSelected());
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    protected void drawSlot(int slot, int x, int y, int var4, int var5, int var6) {
        AltAccount alt = getAlt(slot);

        if (alt != null) {
            mc.fontRendererObj.drawStringWithShadow(alt.getUsername(), x + 1, y + 2, 0xFFFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(alt.getPassword().replaceAll("(?s).", "*"), x + 1, y + 12, 0xFFFFFFFF);
        }
    }

    public AltAccount getAlt(int slot) {
        int count = 0;

        if (mc.currentScreen instanceof GuiAltManager) {
            GuiAltManager screen = (GuiAltManager) mc.currentScreen;

            for (AltAccount alt : screen.getAccounts()) {
                if (count == slot) {
                    return alt;
                }
                count++;
            }
        } else {
            return null;
        }

        return null;
    }
}
