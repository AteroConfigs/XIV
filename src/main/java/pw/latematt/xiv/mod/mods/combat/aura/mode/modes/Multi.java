package pw.latematt.xiv.mod.mods.combat.aura.mode.modes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;
import pw.latematt.xiv.mod.mods.combat.aura.mode.AuraMode;

/**
 * @author Matthew
 */
public class Multi extends AuraMode {
    public Multi(KillAura killAura) {
        super("Multi", killAura);
    }

    @Override
    public void onPreMotionUpdate(MotionUpdateEvent event) {

    }

    @Override
    public void onPostMotionUpdate(MotionUpdateEvent event) {
        mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(entity -> killAura.isValidEntity((EntityLivingBase) entity))
                .forEach(entity -> killAura.attack((EntityLivingBase) entity));
    }

    @Override
    public void onMotionPacket(C03PacketPlayer packet) {

    }

    @Override
    public boolean isAttacking() {
        return mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).filter(entity -> killAura.isValidEntity((EntityLivingBase) entity)).findAny().isPresent();
    }

    @Override
    public void onDisabled() {

    }
}
