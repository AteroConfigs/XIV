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
public class TestingTheme extends ClickTheme {
    protected NahrFont font;
    public TestingTheme() {
        super("Debug");
        this.font = new NahrFont("Verdana", 18);
    }

    @Override
    public void renderPanel(Panel panel) {
        RenderUtils.drawRect(panel.getX(), panel.getY(), panel.getX() + panel.getWidth(), panel.getY() + panel.getHeight(), 0x55000000);
        font.drawString(panel.getName(), panel.getX() + (panel.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(panel.getName()) / 2), panel.getY() - 1, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);

        if (panel.isOpen()) {
            RenderUtils.drawRect(panel.getX() + 2, panel.getY() + panel.getOpenHeight() - 1, panel.getX() + panel.getWidth() - 2, panel.getY() + panel.getOpenHeight(), 0x55000000);
        }
    }

    @Override
    public void renderValueButton(ValueButton button, int mouseX, int mouseY) {
        RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), button.getValue().getValue() ? button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);
        mc.fontRendererObj.drawStringWithShadow(button.getValuePrettyName(), button.getX() + (button.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(button.getValuePrettyName()) / 2), button.getY() + 2, 0xFFFFFFFF);

    }

    @Override
    public void renderModButton(ModButton button, int mouseX, int mouseY) {
        RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), button.getMod().isEnabled() ? button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);
        font.drawString(button.getMod().getName(), button.getX() + (button.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(button.getMod().getName()) / 2), button.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }

    @Override
    public void renderThemeButton(ThemeButton button, int mouseX, int mouseY) {
        RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight(), GuiClick.getTheme() == button.getTheme() ? button.isOverElement(mouseX, mouseY) ? 0xBB000000 : 0x99000000 : button.isOverElement(mouseX, mouseY) ? 0x66000000 : 0x33000000);
        font.drawString(button.getTheme().getName(), button.getX() + (button.getWidth() / 2) - (mc.fontRendererObj.getStringWidth(button.getTheme().getName()) / 2), button.getY() - 2, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);
    }
}
