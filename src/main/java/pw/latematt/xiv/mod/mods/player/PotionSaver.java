package pw.latematt.xiv.mod.mods.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.PotionIncrementEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.EntityUtils;

import java.util.Objects;

/**
 * @author Rederpz
 */

public class PotionSaver extends Mod implements Listener<MotionUpdateEvent> {
    private final Listener potionIncrementListener;
    public PotionSaver() {
        super("PotionSaver", ModType.PLAYER, Keyboard.KEY_NONE, 0xFF008800);

        potionIncrementListener = new Listener<PotionIncrementEvent>() {
            @Override
            public void onEventCalled(PotionIncrementEvent event) {
                if (!(mc.thePlayer.isUsingItem()) && (EntityUtils.getReference().motionX == 0 && EntityUtils.getReference().isCollidedVertically && !mc.gameSettings.keyBindJump.getIsKeyPressed() && EntityUtils.getReference().motionZ == 0)) {
                    event.setCancelled(true);
                }
            }
        };
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
            if (!(mc.thePlayer.isUsingItem()) && (EntityUtils.getReference().motionX == 0 && EntityUtils.getReference().isCollidedVertically && !mc.gameSettings.keyBindJump.getIsKeyPressed() && EntityUtils.getReference().motionZ == 0)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(potionIncrementListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(potionIncrementListener);
    }
}
