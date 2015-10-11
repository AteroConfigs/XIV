package pw.latematt.xiv.mod.mods.player;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.EntityUtils;

/**
 * @author Jack
 */

public class AntiDrown extends Mod implements Listener<MotionUpdateEvent> {
    public AntiDrown() {
        super("AntiDrown", ModType.PLAYER, Keyboard.KEY_NONE, 0xFF4682B4);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (!(mc.thePlayer.isUsingItem()) && BlockUtils.isInLiquid(EntityUtils.getReference()) && EntityUtils.getReference().isCollidedVertically && (EntityUtils.getReference().motionX == 0 && EntityUtils.getReference().motionZ == 0)) {
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
