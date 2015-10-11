package pw.latematt.xiv.ui.clickgui.panel.panels;

import pw.latematt.xiv.ui.clickgui.panel.Panel;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class NameProtectPanel extends Panel {
    public NameProtectPanel(float x, float y, float width, float height) {
        super("NameProtect", new ArrayList<>(), x, y, width, height);

        addValueElements("nameprotect_");
    }
}
