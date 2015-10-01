package pw.latematt.xiv.ui.clickgui.theme.themes;

import org.lwjgl.input.Mouse;
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
        super("Default", 12, 12);
        this.titleFont = new NahrFont(new Font("Tahoma", Font.BOLD, 22), 22);
        this.modFont = new NahrFont("Tahoma", 17);
    }

    @Override
    public void renderPanel(Panel panel) {
        RenderUtils.drawBorderedRect(panel.getX(), panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + panel.getOpenHeight(), 0x801E1E1E, 0xFF3E434C);
        titleFont.drawString(panel.getName(), panel.getX() + 2, panel.getY() - 2, NahrFont.FontType.NORMAL, 0xFFFFFFFF);
//        int startColor = panel.isOpen() ? 0xFF344A64 : 0xFF515658;
//        int endColor = panel.isOpen() ? 0xFF263649 : 0xFF44494B;
//
//        RenderUtils.drawBorderedGradientRect(panel.getX() + panel.getWidth() - 12, panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + panel.getOpenHeight() - 1, 0xFF555A61, startColor, endColor);
        if (panel.isOpen()) {
            RenderUtils.drawBorderedRect(panel.getX(), panel.getY() + panel.getOpenHeight(), panel.getX() + panel.getWidth(), panel.getY() + panel.getHeight(), 0x801E1E1E, 0xFF3C3F41);
        }
    }

    @Override
    public void renderButton(String name, boolean enabled, float x, float y, float width, float height, boolean overElement) {
        int startColor = enabled ? 0xFF344A64 : 0xFF515658;
        int endColor = enabled ? 0xFF263649 : 0xFF44494B;
        int borderColor = overElement && Mouse.isButtonDown(0) ? 0xFF4E78A2 : 0xFF555A61;

        RenderUtils.drawBorderedGradientRect(x, y + 1, x + width, y + height - 1, borderColor, startColor, endColor);
        modFont.drawString(name, x + width + 2, y - 2, NahrFont.FontType.EMBOSS_BOTTOM, 0xFFBBBBBB);
    }
}
