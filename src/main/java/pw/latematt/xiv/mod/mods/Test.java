package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;

/**
 * @author Matthew
 */
public class Test extends Mod implements Listener<SendPacketEvent> {
    public Test() {
        super("Test", Keyboard.KEY_F, 0xFF696969);
    }

    public void onEventCalled(SendPacketEvent event) {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisabled() {

    }
}
