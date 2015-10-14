package pw.latematt.xiv.mod.mods.player;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.EntityUtils;

import java.util.Objects;

/**
 * @author Rederpz / Jack
 */

public class AntiSuffocate extends Mod implements Listener<MotionUpdateEvent> {
    public AntiSuffocate() {
        super("AntiSuffocate", ModType.PLAYER, Keyboard.KEY_NONE, 0xFFAB8B8B);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
            if (!(mc.thePlayer.isUsingItem()) && BlockUtils.isInsideBlock(EntityUtils.getReference()) && mc.thePlayer.isCollidedVertically && (EntityUtils.getReference().motionX == 0 && EntityUtils.getReference().motionZ == 0)) {
                event.setCancelled(true);
            }
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
