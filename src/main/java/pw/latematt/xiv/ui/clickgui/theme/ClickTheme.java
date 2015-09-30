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

    public ClickTheme(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void renderPanel(Panel panel);
    public abstract void renderValueButton(ValueButton button, int mouseX, int mouseY);
    public abstract void renderModButton(ModButton button, int mouseX, int mouseY);
    public abstract void renderThemeButton(ThemeButton themeButton, int mouseX, int mouseY);
}
