package pw.latematt.xiv.ui.clickgui.theme.themes;

import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.elements.ModButton;
import pw.latematt.xiv.ui.clickgui.element.elements.ThemeButton;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

import java.awt.*;

/**
 * @author Matthew
 */
public class DefaultTheme extends ClickTheme {
    protected NahrFont titleFont;
    protected NahrFont modFont;
    public DefaultTheme() {
        super("Default", 24, 8);
        this.titleFont = new NahrFont("Segoe UI", 20);
        this.modFont = new NahrFont("Verdana", 17);
    }

    @Override
    public void renderPanel(Panel panel) {
        RenderUtils.drawRect(panel.getX() - 2, panel.getY() + 1, panel.getX() + panel.getWidth() + 2, panel.getY() + panel.getOpenHeight(), 0xFF4DB3FF);
        titleFont.drawString(panel.getName(), panel.getX() + 2, panel.getY() - 3, NahrFont.FontType.NORMAL, 0xFFFFFFFF);
        if (panel.isOpen()) {
            RenderUtils.drawRect(panel.getX(), panel.getY() + panel.getOpenHeight(), panel.getX() + panel.getWidth(), panel.getY() + panel.getHeight(), 0xFFFFFFFF);
        }
    }

    @Override
    public void renderValueButton(ValueButton button, int mouseX, int mouseY) {
        boolean enabled = button.getValue().getValue();
        String textToDraw = button.getValuePrettyName();
        int color = button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000;
        if (enabled) {
            color = button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000;
        }
        RenderUtils.drawRect(button.getX(), button.getY() + 2, button.getX() + button.getWidth(), button.getY() + button.getHeight() + 2, color);
        modFont.drawString(textToDraw, button.getX() + button.getWidth() + 2, button.getY() - 2, NahrFont.FontType.NORMAL, 0xFF1E1E1E);
    }

    @Override
    public void renderModButton(ModButton button, int mouseX, int mouseY) {
        boolean enabled = button.getMod().isEnabled();
        String textToDraw = button.getMod().getName();
        RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), enabled ? button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);
        modFont.drawString(textToDraw, button.getX() + (button.getWidth() / 2) - (modFont.getStringWidth(textToDraw) / 2), button.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }

    @Override
    public void renderThemeButton(ThemeButton button, int mouseX, int mouseY) {
        boolean enabled = GuiClick.getTheme() == button.getTheme();
        String textToDraw = button.getTheme().getName();
        RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), enabled ? button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);
        modFont.drawString(textToDraw, button.getX() + (button.getWidth() / 2) - (modFont.getStringWidth(textToDraw) / 2), button.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }
}
