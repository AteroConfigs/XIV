package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;

/**
 * @author Jack
 */

public class AntiDrown extends Mod implements Listener<MotionUpdateEvent> {
    public AntiDrown() {
        super("AntiDrown", ModType.PLAYER, Keyboard.KEY_NONE, 0xFF4682B4);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (BlockUtils.isInLiquid(mc.thePlayer) && mc.thePlayer.isCollidedVertically && (mc.thePlayer.motionX == 0 && mc.thePlayer.motionZ == 0)) {
            event.setCancelled(true);
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
