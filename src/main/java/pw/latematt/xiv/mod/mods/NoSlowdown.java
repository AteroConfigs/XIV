package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.UsingItemSlowdownEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class NoSlowdown extends Mod implements Listener<UsingItemSlowdownEvent> {
    public NoSlowdown() {
        super("NoSlowdown", ModType.MOVEMENT, Keyboard.KEY_NONE);
        setEnabled(true);
    }

    @Override
    public void onEventCalled(UsingItemSlowdownEvent event) {
        event.setCancelled(true);
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
