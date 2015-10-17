package pw.latematt.xiv.ui.clickgui.panel.panels;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class HUBPanel extends Panel {
    public HUBPanel(float x, float y) {
        super("HUB", new ArrayList<>(), x, y, 20, 10);
        this.setOpen(false);

        addPanelElements();
    }
}
