package pw.latematt.xiv.event.events;

import net.minecraft.client.gui.GuiScreen;
import pw.latematt.xiv.event.Event;

/**
 * @author Rederpz
 */
public class GuiScreenEvent extends Event {
    private final GuiScreen screen;

    public GuiScreenEvent(GuiScreen screen) {
        this.screen = screen;
    }

    public GuiScreen getScreen() {
        return screen;
    }
}
