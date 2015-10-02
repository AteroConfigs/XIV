package pw.latematt.xiv.mod.mods.aura;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.mods.KillAura;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.Timer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Matthew
 */
public class Switch extends AuraMode {
    private KillAura killAura;
    private final List<EntityLivingBase> entities;
    public EntityLivingBase entityToAttack;
    private boolean aimed;
    private Timer timer = new Timer();

    public Switch(KillAura killAura) {
        super("Switch");
        this.killAura = killAura;
        entities = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onPreMotionUpdate(MotionUpdateEvent event) {
        if (entities.isEmpty()) {
            mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).forEach(entity -> {
                EntityLivingBase living = (EntityLivingBase) entity;
                if (killAura.isValidEntity(living)) {
                    entities.add(living);
                }
            });
        }

        if (!entities.isEmpty()) {
            if (killAura.autoBlock.getValue() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) mc.thePlayer.getCurrentEquippedItem().getItem();
                sword.onItemRightClick(mc.thePlayer.getCurrentEquippedItem(), mc.theWorld, mc.thePlayer);
                mc.playerController.updateController();
            }

            EntityLivingBase firstInArray = entities.get(0);
            if (killAura.isValidEntity(firstInArray)) {
                entityToAttack = firstInArray;
            } else {
                entities.remove(firstInArray);
            }
        }

        if (entityToAttack != null) {
            float[] rotations = EntityUtils.getEntityRotations(entityToAttack);

            if (killAura.silent.getValue()) {
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
            } else {
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }
        }
    }

    @Override
    public void onPostMotionUpdate(MotionUpdateEvent event) {
        if (entityToAttack != null && aimed) {
            if (timer.hasReached(killAura.delay.getValue())) {
                killAura.attack(entityToAttack);
                aimed = false;
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
                packet.yaw = rotations[0];
                packet.pitch = rotations[1];
                packet.rotating = true;
            }
            aimed = true;
        }
    }

    @Override
    public boolean isAttacking() {
        return entityToAttack != null && aimed;
    }

    @Override
    public void onDisabled() {
        entities.clear();
        entityToAttack = null;
        aimed = false;
    }
}
