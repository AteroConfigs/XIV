package pw.latematt.xiv.ui.clickgui.theme.themes;

import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

import java.awt.*;

/**
 * @author Matthew
 */
public class IXTheme extends ClickTheme {
    protected NahrFont titleFont;
    protected NahrFont modFont;

    public IXTheme() {
        super("IX", 96, 12);
        this.titleFont = new NahrFont(new Font("Verdana", Font.BOLD, 20), 20);
        this.modFont = new NahrFont("Tahoma", 17);
    }

    @Override
    public void renderPanel(Panel panel) {
        RenderUtils.drawBorderedRect(panel.getX(), panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + (panel.isOpen() ? panel.getHeight() : panel.getOpenHeight()), 0xBB515151, 0xBB232323);
        titleFont.drawString(panel.getName(), panel.getX() + 3, panel.getY() - 2, NahrFont.FontType.NORMAL, 0xFFFFFFFF);

        RenderUtils.drawRect(panel.getX() + 1, panel.getY() + panel.getOpenHeight(), panel.getX() + panel.getWidth() - 1, panel.getY() + panel.getOpenHeight() + 1, 0xBB313131);

        RenderUtils.drawRect(panel.getX() + panel.getWidth() - panel.getOpenHeight() + 2, panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + panel.getOpenHeight() - 2, 0xBB313131);
    }

    @Override
    public void renderButton(String name, boolean enabled, float x, float y, float width, float height, boolean overElement) {
        RenderUtils.drawBorderedRect(x, y, x + 12, y + height, enabled ? 0xFFFF9900 : 0xBB313131, enabled ? 0xFFB18716 : 0xBB232323);
        RenderUtils.drawBorderedRect(x, y, x + 96, y + height, enabled ? 0xFFFF9900 : 0xBB212121, 0x00000000);

        modFont.drawString(name, x + 14.5F, y - 2, NahrFont.FontType.EMBOSS_BOTTOM, enabled ? 0xFFFFAA00 : 0xFFFFFFFF);
    }
}
