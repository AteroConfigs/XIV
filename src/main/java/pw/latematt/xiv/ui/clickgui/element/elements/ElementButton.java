package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.ui.clickgui.element.Element;

public class ElementButton extends Element {

    private static Minecraft mc = Minecraft.getMinecraft();

    private final Mod mod;

    public ElementButton(Mod mod, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.mod = mod;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        Gui.drawRect((int) getX(), (int) getY(), (int) getX() + (int) getWidth(), (int) getY() + (int) getHeight(), mod.isEnabled() ? 0x99000000 : 0x33000000);

        mc.fontRendererObj.drawStringWithShadow(mod.getName(), getX() + (getWidth() / 2) - (mc.fontRendererObj.getStringWidth(mod.getName()) / 2), getY() + 2, 0xFFFFFFFF);
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isOverElement(mouseX, mouseY) && mouseButton == 0) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            mod.toggle();
        }
    }

    public Mod getMod() {
        return mod;
    }

    @Override
    public void onGuiClosed() {
    }
}
