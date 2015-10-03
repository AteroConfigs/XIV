package pw.latematt.xiv.mod.mods;

import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.UsingItemSlowdownEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.aura.KillAura;

/**
 * @author Matthew
 */
public class NoSlowdown extends Mod {
    private final Listener itemSlowdownListener;
    private final Listener motionUpdateListener;

    public NoSlowdown() {
        super("NoSlowdown", ModType.MOVEMENT, Keyboard.KEY_NONE);
        itemSlowdownListener = new Listener<UsingItemSlowdownEvent>() {
            @Override
            public void onEventCalled(UsingItemSlowdownEvent event) {
                event.setCancelled(true);
            }
        };

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            public boolean blockingFix;

            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (mc.thePlayer.getCurrentEquippedItem() == null || !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword))
                    return;

                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (mc.thePlayer.isBlocking()) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                    }
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {
                    KillAura aura = (KillAura) XIV.getInstance().getModManager().find("killaura");
                    if (aura.isAttacking()) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                    } else if (mc.thePlayer.isBlocking()) {
                        if (!blockingFix)
                            blockingFix = true;

                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                    } else if (blockingFix) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                        blockingFix = false;
                    }
                }
            }
        };
        setEnabled(true);
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(itemSlowdownListener);
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(itemSlowdownListener);
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        if (mc.thePlayer != null) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
        }
    }
}
