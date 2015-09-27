package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.awt.*;

/**
 * @author Matthew
 */
public class Sprint extends Mod implements Listener<MotionUpdateEvent> {
    public Sprint() {
        super("Sprint", ModType.MOVEMENT, Keyboard.KEY_M, 0xFF72B190);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            mc.thePlayer.setSprinting((mc.thePlayer.movementInput.moveForward > 0 || mc.thePlayer.movementInput.moveStrafe != 0) && !mc.thePlayer.isCollidedHorizontally);
        }
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
