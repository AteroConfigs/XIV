package pw.latematt.xiv.mod.mods.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.EntityUtils;

/**
 * @author Jack
 */

public class AntiDrown extends Mod implements Listener<SendPacketEvent> {
    public AntiDrown() {
        super("AntiDrown", ModType.PLAYER, Keyboard.KEY_NONE, 0xFF4682B4);
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (!(mc.thePlayer.isUsingItem()) && BlockUtils.isInLiquid(EntityUtils.getReference()) && EntityUtils.getReference().isCollidedVertically && (EntityUtils.getReference().motionX == 0 && EntityUtils.getReference().motionZ == 0)) {
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
