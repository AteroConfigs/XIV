package pw.latematt.xiv.mod.mods.combat.aura.mode.modes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;
import pw.latematt.xiv.mod.mods.combat.aura.mode.AuraMode;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.Timer;

import java.util.Optional;

/**
 * @author Rederpz / Matthew
 */
public class Multi extends AuraMode {
    private EntityLivingBase entityToAttack;
    private final Timer timer = new Timer();

    public Multi(KillAura killAura) {
        super("Multi", killAura);
    }

    @Override
    public void onPreMotionUpdate(MotionUpdateEvent event) {
        if (entityToAttack == null) {
            Optional<Entity> firstValidEntity = mc.theWorld.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .filter(entity -> killAura.isValidEntity((EntityLivingBase) entity))
                    .sorted((entity1, entity2) -> {
                        double entity1Distance = mc.thePlayer.getDistanceToEntity(entity1);
                        double entity2Distance = mc.thePlayer.getDistanceToEntity(entity2);
                        return entity1Distance > entity2Distance ? 1 : entity2Distance > entity1Distance ? -1 : 0;
                    }).findFirst();
            if (firstValidEntity.isPresent()) {
                entityToAttack = (EntityLivingBase) firstValidEntity.get();
            }
        }

        if (killAura.isValidEntity(entityToAttack)) {
            if (killAura.autoBlock.getValue() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) mc.thePlayer.getCurrentEquippedItem().getItem();
                sword.onItemRightClick(mc.thePlayer.getCurrentEquippedItem(), mc.theWorld, mc.thePlayer);
                mc.playerController.updateController();
            }

            int count = 0;
            for (Object o : mc.theWorld.loadedEntityList) {
                if (o instanceof EntityLivingBase) {
                    EntityLivingBase entity = (EntityLivingBase) o;

                    if (killAura.isValidEntity(entity) && entity != entityToAttack) {
                        if (EntityUtils.getAngle(EntityUtils.getEntityRotations(entity)) <= 10 && count < 5) {
                            mc.playerController.attackEntity(mc.thePlayer, entity);
                            count++;
                        }
                    }
                }
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
            if (timer.hasReached(killAura.getDelay())) {
                killAura.attack(entityToAttack);
                timer.reset();
            }
        }
    }

    @Override
    public void onMotionPacket(C03PacketPlayer packet) {
        if (entityToAttack != null) {
            float[] rotations = EntityUtils.getEntityRotations(entityToAttack);
            packet.setYaw(rotations[0]);
            packet.setPitch(rotations[1]);
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
