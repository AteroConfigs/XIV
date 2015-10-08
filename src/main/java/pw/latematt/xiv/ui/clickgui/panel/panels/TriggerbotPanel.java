package pw.latematt.xiv.ui.clickgui.panel.panels;

import pw.latematt.xiv.ui.clickgui.panel.Panel;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class TriggerbotPanel extends Panel {
    public TriggerbotPanel(float x, float y, float width, float height) {
        super("Triggerbot", new ArrayList<>(), x, y, width, height);

        addValueElements("triggerbot_");
    }
}
