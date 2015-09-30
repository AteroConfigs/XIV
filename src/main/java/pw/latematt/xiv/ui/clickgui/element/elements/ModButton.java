package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.utils.NahrFont;

public class ModButton extends Element {

    private static Minecraft mc = Minecraft.getMinecraft();
    private NahrFont font;

    private final Mod mod;

    public ModButton(Mod mod, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.mod = mod;
        this.font = new NahrFont("Verdana", 18);
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        Gui.drawRect((int) getX(), (int) getY(), (int) getX() + (int) getWidth(), (int) getY() + (int) getHeight(), mod.isEnabled() ? isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);

        font.drawString(mod.getName(), getX() + (getWidth() / 2) - (mc.fontRendererObj.getStringWidth(mod.getName()) / 2), getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
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
