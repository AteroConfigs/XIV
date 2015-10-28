package pw.latematt.xiv.mod.mods.combat.aura.mode.modes;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;
import pw.latematt.xiv.mod.mods.combat.aura.mode.AuraMode;

import java.util.Objects;

/**
 * @author Matthew
 */
public class Multi extends AuraMode {
    public Multi(KillAura killAura) {
        super("Multi", killAura);
    }

    @Override
    public void onPreMotionUpdate(MotionUpdateEvent event) {
        if (isAttacking()) {
            if (killAura.autoBlock.getValue() && Objects.nonNull(mc.thePlayer.getCurrentEquippedItem()) && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) mc.thePlayer.getCurrentEquippedItem().getItem();
                sword.onItemRightClick(mc.thePlayer.getCurrentEquippedItem(), mc.theWorld, mc.thePlayer);
                mc.playerController.updateController();
            }

            if (killAura.criticals.getValue()) {
                boolean canStrikeCrits = !mc.thePlayer.isInWater() &&
                        !mc.thePlayer.isInsideOfMaterial(Material.lava) &&
                        !mc.thePlayer.isOnLadder() &&
                        !mc.thePlayer.isPotionActive(Potion.BLINDNESS) &&
                        mc.thePlayer.ridingEntity == null;
                if (canStrikeCrits && mc.thePlayer.onGround) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625101, mc.thePlayer.posZ, false));
                    boolean moving = mc.thePlayer.movementInput.moveForward != 0;
                    boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                    moving = moving && strafing || moving;

                    if (!moving)
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                }
            }
        }
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
