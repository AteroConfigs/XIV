package pw.latematt.xiv.mod.mods.misc;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.InventoryUtils;
import pw.latematt.xiv.value.ClampedValue;

/**
 * @author Matthew
 */
public class ChestStealer extends Mod implements Listener<MotionUpdateEvent> {
    public final ClampedValue<Long> delay = new ClampedValue<>("cheststealer_delay", 125L, 0L, 1000L);
    private final Timer timer = new Timer();

    public ChestStealer() {
        super("ChestStealer", ModType.MISCELLANEOUS, Keyboard.KEY_NONE, 0xFFE58144);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            if (mc.currentScreen instanceof GuiChest) {
                GuiChest chest = (GuiChest) mc.currentScreen;
                if (isChestEmpty(chest) || isInventoryFull())
                    mc.thePlayer.closeScreen();

                System.out.println(isChestEmpty(chest));
                System.out.println(isInventoryFull());
                for (int index = 0; index < chest.inventorySlots.getInventory().size(); index++) {
                    ItemStack stack = chest.inventorySlots.getSlot(index).getStack();
                    if (stack == null)
                        continue;
                    if (timer.hasReached(delay.getValue())) {
                        mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
                        timer.reset();
                    }
                }
            }
        }
    }

    private boolean isChestEmpty(GuiChest chest) {
        for (int index = 0; index <= chest.getLowerChestInventory().getSizeInventory(); index++) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(index);
            if (stack == null)
                return false;
        }

        return true;
    }

    public boolean isInventoryFull() {
        for (int index = 9; index <= 44; index++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null)
                return false;
        }

        return true;
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
