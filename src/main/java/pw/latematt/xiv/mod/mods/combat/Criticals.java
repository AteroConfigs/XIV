package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class Criticals extends Mod {
    private float fallDist;
    private Listener sendPacketListener;

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (!isSafe())
                        fallDist += mc.thePlayer.fallDistance;

                    if (isSafe() || fallDist >= 3.0F) {
                        player.setOnGround(true);
                        fallDist = 0.0F;
                        mc.thePlayer.fallDistance = 0.0F;
                    } else if (fallDist > 0.0F) {
                        player.setOnGround(false);
                    }
                }
            }
        };
    }

    private boolean isSafe() {
        return mc.thePlayer.isInWater() || mc.thePlayer.isInsideOfMaterial(Material.lava) || mc.thePlayer.isOnLadder() || mc.thePlayer.getActivePotionEffects().contains(Potion.BLINDNESS) || mc.thePlayer.ridingEntity != null;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        fallDist = 0.0F;
    }
}
