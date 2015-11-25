package pw.latematt.xiv.mod.mods.combat.aura.mode.modes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;
import pw.latematt.xiv.mod.mods.combat.aura.mode.AuraMode;

/**
 * @author Matthew
 */
public class Multi extends AuraMode {
    private final Timer timer = new Timer();

    public Multi(KillAura killAura) {
        super("Multi", killAura);
    }

    @Override
    public void onPreMotionUpdate(MotionUpdateEvent event) {

    }

    @Override
    public void onPostMotionUpdate(MotionUpdateEvent event) {
        if (isEntityInRange() && !killAura.isHealing()) {
            if (timer.hasReached(killAura.getDelay())) {
                mc.theWorld.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityLivingBase)
                        .filter(entity -> killAura.isValidEntity((EntityLivingBase) entity))
                        .forEach(entity -> killAura.attack((EntityLivingBase) entity));
                timer.reset();
            }
        }
    }

    @Override
    public void onMotionPacket(C03PacketPlayer packet) {
        if (!packet.isRotating())
            return;
        if (isEntityInRange() && !killAura.isHealing()) {
            packet.setPitch(95);
        }
    }

    @Override
    public void onDisabled() {

    }

    private boolean isEntityInRange() {
        return mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(entity -> killAura.isValidEntity((EntityLivingBase) entity))
                .findFirst().isPresent();
    }
}
