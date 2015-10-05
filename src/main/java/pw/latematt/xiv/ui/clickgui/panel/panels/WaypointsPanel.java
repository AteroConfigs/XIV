package pw.latematt.xiv.ui.clickgui.panel.panels;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class WaypointsPanel extends Panel {
    public WaypointsPanel(float x, float y, float width, float height) {
        super("Waypoints", new ArrayList<>(), x, y, width, height);

        float elementY = 4;
        for (Value value : XIV.getInstance().getValueManager().getContents()) {
            if (!value.getName().startsWith("waypoints_"))
                continue;
            String actualName = value.getName().replaceAll("waypoints_", "");
            String prettyName = "";
            String[] actualNameSplit = actualName.split("_");
            if (actualNameSplit.length > 0) {
                for (String arg : actualNameSplit) {
                    arg = arg.substring(0, 1).toUpperCase() + arg.substring(1, arg.length());
                    prettyName += arg + " ";
                }
            } else {
                prettyName = actualNameSplit[0].substring(0, 1).toUpperCase() + actualNameSplit[0].substring(1, actualNameSplit[0].length());
            }
            if (value.getValue() instanceof Boolean) {
                getElements().add(new ValueButton(value, prettyName, x + 2, elementY + 2, GuiClick.getTheme().getElementWidth(), GuiClick.getTheme().getElementHeight()));
            }
            elementY += GuiClick.getTheme().getElementHeight() + 1;
        }
    }
}