package pw.latematt.xiv.mod.mods.aura.mode.modes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.mods.aura.KillAura;
import pw.latematt.xiv.mod.mods.aura.mode.AuraMode;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.Timer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Matthew
 */
public class Switch extends AuraMode {
    private final List<EntityLivingBase> entities;
    private EntityLivingBase entityToAttack;
    private final Timer timer = new Timer();

    public Switch(KillAura killAura) {
        super("Switch", killAura);
        entities = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onPreMotionUpdate(MotionUpdateEvent event) {
        if (entities.isEmpty()) {
            mc.theWorld.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .filter(entity -> killAura.isValidEntity((EntityLivingBase) entity))
                    .sorted((entity1, entity2) -> {
                        double entity1YawDistance = EntityUtils.getYawChange((EntityLivingBase) entity1);
                        double entity2YawDistance = EntityUtils.getYawChange((EntityLivingBase) entity2);
                        return entity1YawDistance > entity2YawDistance ? -1 : entity2YawDistance > entity1YawDistance ? 1 : 0;
                    }).forEach(entity -> entities.add((EntityLivingBase) entity));
        }

        if (!entities.isEmpty()) {
            EntityLivingBase firstInArray = entities.get(0);
            if (killAura.isValidEntity(firstInArray)) {
                entityToAttack = firstInArray;
            } else {
                entities.remove(firstInArray);
            }
        }

        if (killAura.isValidEntity(entityToAttack)) {
            if (killAura.autoBlock.getValue() && Objects.nonNull(mc.thePlayer.getCurrentEquippedItem()) && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
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
            entities.remove(entityToAttack);
        }
    }

    @Override
    public void onPostMotionUpdate(MotionUpdateEvent event) {
        if (entityToAttack != null) {
            if (timer.hasReached(killAura.delay.getValue())) {
                killAura.attack(entityToAttack);
                entities.remove(entityToAttack);
                entityToAttack = null;
                timer.reset();
            }
        }
    }

    @Override
    public void onMotionPacket(C03PacketPlayer packet) {
        if (entityToAttack != null) {
            float[] rotations = EntityUtils.getEntityRotations(entityToAttack);
            if (killAura.silent.getValue()) {
                packet.setYaw(rotations[0]);
                packet.setPitch(rotations[1]);
            }
        }
    }

    @Override
    public boolean isAttacking() {
        return entityToAttack != null;
    }

    @Override
    public void onDisabled() {
        entities.clear();
        entityToAttack = null;
    }
}
