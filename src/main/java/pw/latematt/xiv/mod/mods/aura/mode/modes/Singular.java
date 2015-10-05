package pw.latematt.xiv.mod.mods.aura.mode.modes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.mods.aura.KillAura;
import pw.latematt.xiv.mod.mods.aura.mode.AuraMode;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.Timer;

/**
 * @author Matthew
 */
public class Singular extends AuraMode {
    public EntityLivingBase entityToAttack;
    private Timer timer = new Timer();

    public Singular(KillAura killAura) {
        super("Singular", killAura);
    }

    @Override
    public void onPreMotionUpdate(MotionUpdateEvent event) {
        if (entityToAttack == null) {
            final double[] maxDistance = {killAura.range.getValue()};
            mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).forEach(entity -> {
                EntityLivingBase living = (EntityLivingBase) entity;
                double distance = mc.thePlayer.getDistanceToEntity(living);
                if (distance < maxDistance[0] && killAura.isValidEntity(living)) {
                    maxDistance[0] = distance;
                    entityToAttack = living;
                }
            });
        }

        if (killAura.isValidEntity(entityToAttack)) {
            if (killAura.autoBlock.getValue() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) mc.thePlayer.getCurrentEquippedItem().getItem();
                sword.onItemRightClick(mc.thePlayer.getCurrentEquippedItem(), mc.theWorld, mc.thePlayer);
                mc.playerController.updateController();
            }

            float[] rotations = EntityUtils.getEntityRotations(entityToAttack);
            if (killAura.silent.getValue()) {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            } else {
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }
        } else {
            entityToAttack = null;
        }
    }

    @Override
    public void onPostMotionUpdate(MotionUpdateEvent event) {
        if (entityToAttack != null) {
            if (timer.hasReached(killAura.delay.getValue())) {
                killAura.attack(entityToAttack);
                timer.reset();
            }
        }
    }

    @Override
    public void onMotionPacket(C03PacketPlayer packet) {
        if (entityToAttack != null) {
            float[] rotations = EntityUtils.getEntityRotations(entityToAttack);
            if (killAura.silent.getValue()) {
                packet.yaw = rotations[0];
                packet.pitch = rotations[1];
            }
        }
    }

    @Override
    public boolean isAttacking() {
        return entityToAttack != null;
    }

    @Override
    public void onDisabled() {
        entityToAttack = null;
    }
}
