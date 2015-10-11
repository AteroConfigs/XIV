package pw.latematt.xiv.utils;

import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.value.Value;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Matthew
 */
public class EntityUtils {
    private static Minecraft mc = Minecraft.getMinecraft();

    /**
     *  Latematt's not gonna like how I do this, but if he doesn't like it then he can fix it. This is for tracers with freecam/other mods that have external cams.
     */

    private static boolean set = false;
    private static Entity reference;

    public static Entity getReference() {
        return reference == null ? reference = mc.thePlayer : ((set || !((Value<Boolean>) XIV.getInstance().getValueManager().find("render_tracer_entity")).getValue()) ? mc.thePlayer : reference);
    }

    public static boolean isReferenceSet() {
        return !set;
    }

    public static void setReference(Entity ref) {
        reference = ref;

        if(reference == mc.thePlayer) {
            set = true;
        }else{
            set = false;
        }
    }

    public static float[] getEntityRotations(Entity target) {
        final double var4 = target.posX - mc.thePlayer.posX;
        final double var6 = target.posZ - mc.thePlayer.posZ;
        final double var8 = target.posY + target.getEyeHeight() / 1.3 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        final double var14 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
        final float yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
        final float pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
        return new float[]{yaw, pitch};
    }

    public static float getAngle(float[] original, float[] rotations) {
        float curYaw = normalizeAngle(original[0]);
        rotations[0] = normalizeAngle(rotations[0]);
        float curPitch = normalizeAngle(original[1]);
        rotations[1] = normalizeAngle(rotations[1]);
        float fixedYaw = normalizeAngle(curYaw - rotations[0]);
        float fixedPitch = normalizeAngle(curPitch - rotations[1]);
        return Math.abs(normalizeAngle(fixedYaw) + Math.abs(fixedPitch));
    }

    public static float getAngle(float[] rotations) {
        return getAngle(new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch }, rotations);
    }

    public static float normalizeAngle(float angle) {
        return MathHelper.wrapAngleTo180_float((angle + 180.0F) % 360.0F - 180.0F);
    }

    public static float getPitchChange(final EntityLivingBase entity){
        final double deltaX = entity.posX - mc.thePlayer.posX;
        final double deltaZ = entity.posZ - mc.thePlayer.posZ;
        final double deltaY = entity.posY - 2.2D + entity.getEyeHeight() - mc.thePlayer.posY;
        final double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        final double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float)pitchToEntity);
    }

    public static float getYawChange(final EntityLivingBase entity){
        final double deltaX = entity.posX - mc.thePlayer.posX;
        final double deltaZ = entity.posZ - mc.thePlayer.posZ;
        double yawToEntity;

        if ((deltaZ < 0.0D) && (deltaX < 0.0D)){
            yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if ((deltaZ < 0.0D) && (deltaX > 0.0D)){
                yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
            } else {
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
            }
        }

        return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float)yawToEntity));
    }

    public static int getBestWeapon(EntityLivingBase target) {
        // TODO: Issue #7 https://gitlab.com/latematt/XIV/issues/7

        final int originalSlot = mc.thePlayer.inventory.currentItem;
        int weaponSlot = -1;
        float weaponDamage = 1.0F;
        for (byte slot = 0; slot < 9; slot = (byte) (slot + 1)) {
            mc.thePlayer.inventory.currentItem = slot;
            final ItemStack itemStack = mc.thePlayer.getHeldItem();
            if (itemStack != null) {
                float damage = getItemDamage(itemStack);
                damage += EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(),
                        target.getCreatureAttribute());
                if (damage > weaponDamage) {
                    weaponDamage = damage;
                    weaponSlot = slot;
                }
            }
        }
        if (weaponSlot != -1)
            return weaponSlot;
        return originalSlot;
    }

    private static float getItemDamage(ItemStack itemStack) {
        final Multimap multimap = itemStack.getAttributeModifiers();
        if (!multimap.isEmpty()) {
            final Iterator iterator = multimap.entries().iterator();
            if (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry) iterator.next();
                final AttributeModifier attributeModifier = (AttributeModifier) entry
                        .getValue();
                double damage;
                if (attributeModifier.getOperation() != 1
                        && attributeModifier.getOperation() != 2) {
                    damage = attributeModifier.getAmount();
                } else {
                    damage = attributeModifier.getAmount() * 100.0D;
                }
                if (attributeModifier.getAmount() > 1.0D)
                    return 1.0F + (float) damage;
                return 1.0F;
            }
        }
        return 1.0F;
    }

    public static void damagePlayer() {
        if(mc != null && mc.thePlayer != null && mc.getNetHandler() != null) {
            for (int i = 0; i < 81; i++) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.05D, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            }
        }
    }
}
