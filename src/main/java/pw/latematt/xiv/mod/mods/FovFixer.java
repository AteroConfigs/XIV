package pw.latematt.xiv.mod.mods;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Items;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.FovModifierEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class FovFixer extends Mod implements Listener<FovModifierEvent> {
    public FovFixer() {
        super("FovFixer", ModType.RENDER);
    }

    @Override
    public void onEventCalled(FovModifierEvent event) {
        float var1 = 1.0F;

        if (mc.thePlayer.capabilities.isFlying) {
            var1 *= 1.1F;
        }

        IAttributeInstance var2 = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        float walkSpeed = (float) var2.getAttributeValue();
        if (walkSpeed > 0.12999481F) {
            walkSpeed = 0.12999481F;
        }
        var1 = (var1 * ((walkSpeed / mc.thePlayer.capabilities.getWalkSpeed() + 1.0F) / 2.0F));

        if (mc.thePlayer.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(var1) || Float.isInfinite(var1)) {
            var1 = 1.0F;
        }

        if (mc.thePlayer.isUsingItem() && mc.thePlayer.getItemInUse().getItem() == Items.bow) {
            int var3 = mc.thePlayer.getItemInUseDuration();
            float var4 = (float) var3 / 20.0F;

            if (var4 > 1.0F) {
                var4 = 1.0F;
            } else {
                var4 *= var4;
            }

            var1 *= 1.0F - var4 * 0.15F;
        }

        event.setFov(var1);
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
