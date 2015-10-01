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
public class ESPPanel extends Panel {
    public ESPPanel(float x, float y, float width, float height) {
        super("ESP", new ArrayList<>(), x, y, width, height);

        float elementY = 4;
        for (Value value : XIV.getInstance().getValueManager().getContents()) {
            if (value.getName().startsWith("esp_") && value.getValue() instanceof Boolean) {
                String actualName = value.getName().split("_")[1];
                String prettyName = actualName.substring(0, 1).toUpperCase() + actualName.substring(1, actualName.length());
                getElements().add(new ValueButton(value, prettyName, x + 2, elementY + 2, GuiClick.getTheme().getElementWidth(), GuiClick.getTheme().getElementHeight()));
                elementY += GuiClick.getTheme().getElementHeight() + 1;
            }
        }
    }
}
