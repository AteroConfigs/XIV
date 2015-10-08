package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.ui.clickgui.element.Element;

public class ModButton extends Element {
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected final Mod mod;

    public ModButton(Mod mod, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.mod = mod;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        XIV.getInstance().getGuiClick().getTheme().renderButton(mod.getName(), mod.isEnabled(), getX(), getY(), getWidth(), getHeight(), isOverElement(mouseX, mouseY), this);
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
