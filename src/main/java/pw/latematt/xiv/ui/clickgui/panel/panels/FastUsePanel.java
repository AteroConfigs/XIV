package pw.latematt.xiv.ui.clickgui.panel.panels;

import pw.latematt.xiv.ui.clickgui.panel.Panel;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class FastUsePanel extends Panel {
    public FastUsePanel(float x, float y, float width, float height) {
        super("FastUse", new ArrayList<>(), x, y, width, height);

        addValueElements("fastuse_");
    }
}
