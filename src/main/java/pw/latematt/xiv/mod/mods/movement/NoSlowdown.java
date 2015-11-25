package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SoulSandSlowdownEvent;
import pw.latematt.xiv.event.events.UsingItemSlowdownEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class NoSlowdown extends Mod {
    private final Listener itemSlowdownListener, soulSandSlowdownListener, motionUpdateListener;

    public NoSlowdown() {
        super("NoSlowdown", ModType.MOVEMENT, Keyboard.KEY_NONE);

        itemSlowdownListener = new Listener<UsingItemSlowdownEvent>() {
            @Override
            public void onEventCalled(UsingItemSlowdownEvent event) {
                event.setCancelled(true);
            }
        };

        soulSandSlowdownListener = new Listener<SoulSandSlowdownEvent>() {
            @Override
            public void onEventCalled(SoulSandSlowdownEvent event) {
                event.setCancelled(true);
            }
        };

        /* fix for blocking with a sword, TODO: remove it when updating to 1.9 */
        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
                if (currentItem == null)
                    return;
                if (currentItem.getItem().getItemUseAction(currentItem) != EnumAction.BLOCK)
                    return;

                if (mc.thePlayer.isBlocking()) {
                    boolean moving = mc.thePlayer.movementInput.moveForward != 0;
                    boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                    moving = moving || strafing;
                    block();
                    if (event.getCurrentState() == MotionUpdateEvent.State.PRE && moving)
                        unblock();
                } else
                    unblock();
            }
        };

        setEnabled(true);
    }

    private void block() {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
    }

    private void unblock() {
        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(itemSlowdownListener, motionUpdateListener, soulSandSlowdownListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(itemSlowdownListener, motionUpdateListener, soulSandSlowdownListener);
        if (mc.thePlayer != null)
            unblock();
    }
}
