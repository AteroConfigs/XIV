package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class Sneak extends Mod implements Listener<MotionUpdateEvent> {
    public Sneak() {
        super("Sneak", ModType.MOVEMENT, Keyboard.KEY_Z, 0xFF33C452);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        boolean sneaking = mc.thePlayer.isSneaking();
        boolean moving = mc.thePlayer.movementInput.moveForward != 0;
        boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
        moving = moving || strafing;
        if (!moving || sneaking) {
            if (event.getCurrentState() == MotionUpdateEvent.State.PRE)
                sneak();
        } else {
            sneak();
            if (event.getCurrentState() == MotionUpdateEvent.State.PRE)
                unsneak();
        }
    }

    private void sneak() {
        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
    }

    private void unsneak() {
        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        if (mc.thePlayer != null && !mc.thePlayer.isSneaking())
            unsneak();
    }
}
