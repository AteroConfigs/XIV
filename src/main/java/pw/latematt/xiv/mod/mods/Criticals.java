package pw.latematt.xiv.mod.mods;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BreakingBlockEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class Criticals extends Mod {
    private final Listener sendPacketListener;
    private final Listener breakingBlockListener;
    private float fallDist;

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);
        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (!isSafe()) {
                        fallDist += mc.thePlayer.fallDistance;
                    }

                    player.onGround = false;
                    player.moving = true;
//                    if (fallDist >= 4.0F || isSafe()) {
//                        player.onGround = true;
//                        player.moving = true;
//                        fallDist = 0.0F;
//                        mc.thePlayer.fallDistance = 0.0F;
//                        setTag(getName());
//                    } else if (fallDist > 0.0F) {
//                        player.onGround = false;
//                        player.moving = true;
//                        setTag(String.format("%s \2477%s", getName(), "*"));
//                    } else {
//                        setTag(getName());
//                    }
                }
            }
        };

        breakingBlockListener = new Listener<BreakingBlockEvent>() {
            @Override
            public void onEventCalled(BreakingBlockEvent event) {
                fallDist = 4.0F;
            }
        };
    }

    public boolean isSafe() {
        return mc.thePlayer.isInWater()
                || mc.thePlayer.isInsideOfMaterial(Material.lava)
                || mc.thePlayer.isOnLadder()
                || mc.thePlayer.getActivePotionEffects().contains(
                Potion.blindness) || mc.thePlayer.ridingEntity != null;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(sendPacketListener);
        XIV.getInstance().getListenerManager().add(breakingBlockListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        XIV.getInstance().getListenerManager().remove(breakingBlockListener);
        fallDist = 0.0F;
    }
}
