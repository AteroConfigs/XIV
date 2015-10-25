package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;

/**
 * @author Matthew
 */
public class Criticals extends Mod {
    private Listener motionUpdateListener, attackEntityListener;
    private int delay;

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);

        attackEntityListener = new Listener<AttackEntityEvent>() {
            @Override
            public void onEventCalled(AttackEntityEvent event) {
                if (!isSafe())
                    return;
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625101, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            }
        };

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (!isSafe())
                        return;
                    int endDelay = 3;
                    event.setCancelled(++delay >= endDelay);
                    delay = (event.isCancelled() ? 0 : delay);
                }
            }
        };
    }

    private boolean isSafe() {
        return !mc.thePlayer.isInWater()
                && !mc.thePlayer.isInsideOfMaterial(Material.lava)
                && !mc.thePlayer.isOnLadder()
                && !mc.thePlayer.getActivePotionEffects().contains(Potion.BLINDNESS)
                && mc.thePlayer.ridingEntity == null
                && mc.thePlayer.onGround;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        XIV.getInstance().getListenerManager().add(attackEntityListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(attackEntityListener);
    }
}
