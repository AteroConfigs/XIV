package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class Sprint extends Mod implements Listener<MotionUpdateEvent> {
    public Sprint() {
        super("Sprint", ModType.MOVEMENT, Keyboard.KEY_B, 0xFF72B190);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
            boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
            boolean moving = movingForward || strafing;

            boolean sneaking = mc.thePlayer.isSneaking();
            boolean collided = mc.thePlayer.isCollidedHorizontally;
            boolean hungry = mc.thePlayer.getFoodStats().getFoodLevel() <= 6;
            mc.thePlayer.setSprinting(moving && !sneaking && !collided && !hungry);
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
