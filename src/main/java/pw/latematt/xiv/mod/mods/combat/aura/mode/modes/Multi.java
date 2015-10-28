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
