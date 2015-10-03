package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;

import java.io.IOException;

public class ThemeButton extends Element {
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected final ClickTheme theme;

    public ThemeButton(ClickTheme theme, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.theme = theme;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        GuiClick.getTheme().renderButton(getTheme().getName(), GuiClick.getTheme() == getTheme(), getX(), getY(), getWidth(), getHeight(), isOverElement(mouseX, mouseY), this);
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isOverElement(mouseX, mouseY) && mouseButton == 0) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            GuiClick.setTheme(theme);
            try {
                GuiClick.themeConfig.save();
            } catch (IOException e) {
                XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not load, a stack trace has been printed.", GuiClick.themeConfig.getName(), GuiClick.themeConfig.getExtension()));
                e.printStackTrace();
            }
        }
    }

    public ClickTheme getTheme() {
        return theme;
    }

    @Override
    public void onGuiClosed() {
    }
}
