package pw.latematt.xiv.mod.mods;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.util.Objects;

/**
 * @author Jack
 */

public class Zoot extends Mod implements Listener<MotionUpdateEvent> {
    public Zoot() {
        super("Zoot", ModType.PLAYER, Keyboard.KEY_NONE, 0xFFD298ED);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            if (mc.thePlayer.isBurning() && !mc.thePlayer.isInsideOfMaterial(Material.fire) && !mc.thePlayer.isInsideOfMaterial(Material.lava) && mc.thePlayer.getActivePotionEffect(Potion.FIRE_RESISTANCE) == null) {
                for (int x = 0; x < 20; x++) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
                }
            }

            if (mc.thePlayer.isPotionActive(Potion.BLINDNESS.getId())) {
                mc.thePlayer.removePotionEffect(Potion.BLINDNESS.getId());
            }

            if (mc.thePlayer.isPotionActive(Potion.NAUSEA.getId())) {
                mc.thePlayer.removePotionEffect(Potion.NAUSEA.getId());
            }

            if (mc.thePlayer.isPotionActive(Potion.MINING_FATIGUE.getId())) {
                mc.thePlayer.removePotionEffect(Potion.MINING_FATIGUE.getId());
            }

            final Potion[] potionTypes;
            for (int length = (potionTypes = Potion.potionTypes).length, i = 0; i < length; i++) {
                final Potion potion = potionTypes[i];
                if (Objects.nonNull(potion) && potion.isBadEffect() && mc.thePlayer.isPotionActive(potion)) {
                    final PotionEffect effect = mc.thePlayer.getActivePotionEffect(potion);
                    for (int x = 0; x < effect.getDuration() / 20; x++) {
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                }
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
