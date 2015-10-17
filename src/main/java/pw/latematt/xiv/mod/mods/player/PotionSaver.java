package pw.latematt.xiv.mod.mods.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.PotionIncrementEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.EntityUtils;

/**
 * @author Rederpz
 */

public class PotionSaver extends Mod implements Listener<SendPacketEvent> {
    private final Listener potionIncrementListener;

    public PotionSaver() {
        super("PotionSaver", ModType.PLAYER, Keyboard.KEY_NONE, 0xFF008800);

        potionIncrementListener = new Listener<PotionIncrementEvent>() {
            @Override
            public void onEventCalled(PotionIncrementEvent event) {
                if (!(mc.thePlayer.isUsingItem()) && !(mc.thePlayer.isSwingInProgress) && (EntityUtils.getReference().motionX == 0 && !(mc.gameSettings.keyBindJump.getIsKeyPressed() && EntityUtils.getReference() == mc.thePlayer || EntityUtils.getReference() != mc.thePlayer) && EntityUtils.getReference().motionZ == 0)) {
                    event.setCancelled(true);
                }
            }
        };
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (!(mc.thePlayer.isUsingItem()) && !(mc.thePlayer.isSwingInProgress) && (EntityUtils.getReference().motionX == 0 && EntityUtils.getReference().isCollidedVertically && !(mc.gameSettings.keyBindJump.getIsKeyPressed() && EntityUtils.getReference() == mc.thePlayer || EntityUtils.getReference() != mc.thePlayer) && EntityUtils.getReference().motionZ == 0)) {
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
