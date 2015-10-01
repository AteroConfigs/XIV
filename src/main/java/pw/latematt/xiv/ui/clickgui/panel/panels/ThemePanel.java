package pw.latematt.xiv.ui.clickgui.panel.panels;

import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.elements.ThemeButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class ThemePanel extends Panel {
    public ThemePanel(float x, float y, float width, float height) {
        super("Themes", new ArrayList<>(), x, y, width, height);

        float elementY = 4;
        for (ClickTheme theme : GuiClick.getThemes()) {
            getElements().add(new ThemeButton(theme, x + 2, elementY + 2, GuiClick.getTheme().getElementWidth(), GuiClick.getTheme().getElementHeight()));
            elementY += GuiClick.getTheme().getElementHeight() + 1;
        }
    }
}
