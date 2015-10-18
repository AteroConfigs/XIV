package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;

/**
 * @author Matthew
 * @author Jack
 */
public class Criticals extends Mod implements Listener<AttackEntityEvent> {
    // private float fallDist;

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);
    }

    @Override
    public void onEventCalled(AttackEntityEvent event) { // this was send packet event before
        /*if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
            if (!isSafe())
                fallDist += mc.thePlayer.fallDistance;

            if (fallDist >= 3.0F || isSafe()) {
                player.setOnGround(true);
                fallDist = 0.0F;
                mc.thePlayer.fallDistance = 0.0F;
                setTag(getName());
            } else if (fallDist > 0.0F) {
                player.setOnGround(false);
                setTag(String.format("%s \2477%s", getName(), "*"));
            } else {
                setTag(getName());
            }
        }*/

        if (!BlockUtils.isOnLiquid(mc.thePlayer)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.05, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.012511, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        } // Might need to lower timer if speed is enabled
    }

    private boolean isSafe() {
        return mc.thePlayer.isInWater()
                || mc.thePlayer.isInsideOfMaterial(Material.lava)
                || mc.thePlayer.isOnLadder()
                || mc.thePlayer.getActivePotionEffects().contains(
                Potion.BLINDNESS) || mc.thePlayer.ridingEntity != null;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        // fallDist = 0.0F;
    }
}
