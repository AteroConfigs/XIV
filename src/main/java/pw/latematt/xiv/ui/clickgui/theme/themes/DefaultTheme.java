package pw.latematt.xiv.ui.clickgui.theme.themes;

import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

/**
 * @author Matthew
 */
public class DefaultTheme extends ClickTheme {
    protected NahrFont titleFont;
    protected NahrFont modFont;
    public DefaultTheme() {
        super("Default", 20, 11);
        this.titleFont = new NahrFont("Segoe UI", 22);
        this.modFont = new NahrFont("Segoe UI", 20);
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
    public void renderButton(String name, boolean enabled, float x, float y, float width, float height, boolean overElement) {
        int color = overElement ? 0xFFD25842 : 0xFFD22300;
        if (enabled) {
            color = overElement ? 0xFF57D280 : 0xFF00D246;
        }
        int shadowColor = (color & 16579836) >> 2 | color & -16777216;
        RenderUtils.drawRect(x, y + 1, x + width + 1, y + height, shadowColor);
        RenderUtils.drawRect(x, y + 1, x + width, y + height - 1, color);
        modFont.drawString(name, x + width + 2, y - 4, NahrFont.FontType.NORMAL, 0xFF1E1E1E);
    }
}
