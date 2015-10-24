package pw.latematt.xiv.event.events;

import net.minecraft.client.Minecraft;
import pw.latematt.xiv.event.Event;

/**
 * @author Rederpz
 */
public class RenderChatEvent extends Event {

    private String text;

    public RenderChatEvent(String text) {
        this.text = text;
    }

    public String getString() {
        return text;
    }
}
