package pw.latematt.xiv.ui.clickgui.theme;

import net.minecraft.client.Minecraft;
import pw.latematt.xiv.ui.clickgui.element.elements.ModButton;
import pw.latematt.xiv.ui.clickgui.element.elements.ThemeButton;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;

/**
 * @author Matthew
 */
public abstract class ClickTheme {
    protected final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private float elementWidth, elementHeight;

    public ClickTheme(String name, float elementWidth, float elementHeight) {
        this.name = name;
        this.elementWidth = elementWidth;
        this.elementHeight = elementHeight;
    }

    public String getName() {
        return name;
    }

    public float getElementWidth() {
        return elementWidth;
    }

    public void setElementWidth(float elementWidth) {
        this.elementWidth = elementWidth;
    }

    public float getElementHeight() {
        return elementHeight;
    }

    public void setElementHeight(float elementHeight) {
        this.elementHeight = elementHeight;
    }

    public abstract void renderPanel(Panel panel);
    public abstract void renderValueButton(ValueButton button, int mouseX, int mouseY);
    public abstract void renderModButton(ModButton button, int mouseX, int mouseY);
    public abstract void renderThemeButton(ThemeButton themeButton, int mouseX, int mouseY);
}
