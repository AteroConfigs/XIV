package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;

/**
 * Use this template when making modules
 *
 * @author Matthew
 */
public class Example extends Mod implements Listener<SendPacketEvent> {
    public Example() {
        super("Example", Keyboard.KEY_F, 0xFF696969, new String[] {}, new String[] {});
    }

    public void onEventCalled(SendPacketEvent event) {

    }

    @Override
    public void onCommandRan(String message) {

    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
