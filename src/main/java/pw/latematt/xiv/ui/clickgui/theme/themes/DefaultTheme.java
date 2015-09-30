package pw.latematt.xiv.ui.clickgui.theme.themes;

import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.elements.ModButton;
import pw.latematt.xiv.ui.clickgui.element.elements.ThemeButton;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

/**
 * @author Matthew
 */
public class DefaultTheme extends ClickTheme {
    protected NahrFont font;
    public DefaultTheme() {
        super("Default");
        this.font = new NahrFont("Verdana Bold", 16);
    }

    @Override
    public void renderPanel(Panel panel) {
        RenderUtils.drawBorderedRect(panel.getX(), panel.getY(), panel.getX() + panel.getWidth(), panel.getY() + panel.getOpenHeight() - 1.5D, 0xFF000000, 0xBB323232);
        RenderUtils.drawBorderedRect(panel.getX(), panel.getY() + panel.getOpenHeight(), panel.getX() + panel.getWidth(), panel.getY() + panel.getHeight(), 0xFF000000, 0xBB323232);
        font.drawString(panel.getName(), panel.getX() + 1, panel.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }

    @Override
    public void renderValueButton(ValueButton button, int mouseX, int mouseY) {
        RenderUtils.drawBorderedRect(button.getX() + button.getWidth() - 35, button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), 0xFF000000, button.getValue().getValue() ? button.isOverElement(mouseX, mouseY) ? 0xBB008888 : 0xBB006666 : button.isOverElement(mouseX, mouseY) ? 0xBB545454 : 0xBB323232);
        font.drawString(button.getValuePrettyName(), button.getX() + 1, button.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }

    @Override
    public void renderModButton(ModButton button, int mouseX, int mouseY) {
        RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), button.getMod().isEnabled() ? button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);
        font.drawString(button.getMod().getName(), button.getX() + (button.getWidth() / 2) - (font.getStringWidth(button.getMod().getName()) / 2), button.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }

    @Override
    public void renderThemeButton(ThemeButton button, int mouseX, int mouseY) {
        RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), GuiClick.getTheme() == button.getTheme() ? button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);
        font.drawString(button.getTheme().getName(), button.getX() + (button.getWidth() / 2) - (font.getStringWidth(button.getTheme().getName()) / 2), button.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }
}
