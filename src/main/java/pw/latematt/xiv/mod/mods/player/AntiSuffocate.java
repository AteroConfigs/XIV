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
 * @author Rederpz / Jack
 */

public class AntiSuffocate extends Mod implements Listener<SendPacketEvent> {
    public AntiSuffocate() {
        super("AntiSuffocate", ModType.PLAYER, Keyboard.KEY_NONE, 0xFFAB8B8B);
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
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
