package pw.latematt.xiv.ui.clickgui.theme.themes;

import net.minecraft.client.gui.FontRenderer;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

import java.awt.*;

/**
 * @author Matthew
 */
public class NorthStarTheme extends ClickTheme {
    protected NahrFont font;

    protected GuiClick gui;

    public NorthStarTheme(GuiClick gui) {
        super("NorthStar", 96, 12, gui);

        this.font = new NahrFont("Arial", 18);

        this.gui = gui;
    }

    @Override
    public void renderPanel(Panel panel) {
        RenderUtils.drawBorderedRect(panel.getX(), panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + (panel.isOpen() ? panel.getHeight() : panel.getOpenHeight()), 0xBB515151, 0xFF000000);
        font.drawString(panel.getName(), panel.getX() + 3, panel.getY() + 1, NahrFont.FontType.SHADOW_THIN, 0xFFFFFFFF);

        RenderUtils.drawRect(panel.getX() + panel.getWidth() - panel.getOpenHeight() + 1, panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + panel.getOpenHeight(), panel.isOpen() ? 0xFF5AACEB : 0xFF313131);
    }

    @Override
    public void renderButton(String name, boolean enabled, float x, float y, float width, float height, boolean overElement, Element element) {
        element.setWidth(this.getElementWidth());
        element.setHeight(this.getElementHeight());

        RenderUtils.drawBorderedRect(x, y, x + 96, y + height, 0xFF212121, enabled ? 0xFF5AACEB : 0xFF212121);

        font.drawString(name, x + 2, y - 1.5F, NahrFont.FontType.SHADOW_THIN, enabled ? 0xFFFFFFFF : 0xFFFFF0F0);
    }

    @Override
    public void renderSlider(String name, float value, float x, float y, float width, float height, float sliderX, boolean overElement, Element element) {
        element.setWidth(96);
        element.setHeight(this.getElementHeight());

        RenderUtils.drawBorderedRect(x, y + 1, x + element.getWidth(), y + height, 0x801E1E1E, 0xFF212121);
        RenderUtils.drawBorderedRect(x, y + 1, x + sliderX, y + height, 0x801E1E1E, 0xFF5AACEB);

        font.drawString(name, x + 2, y - 1, NahrFont.FontType.SHADOW_THIN, 0xFFFFF0F0);
        font.drawString(value + "", x + element.getWidth() - font.getStringWidth(value + "") - 2, y - 1, NahrFont.FontType.SHADOW_THIN, 0xFFFFF0F0);
    }
}
