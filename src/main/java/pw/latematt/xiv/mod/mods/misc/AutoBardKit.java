package pw.latematt.xiv.mod.mods.misc;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.util.Objects;

/**
 * @author Matthew
 */
public class AutoBardKit extends Mod implements Listener<MotionUpdateEvent> {
    private final Item helmet = Items.golden_helmet;
    private final Item chestplate = Items.golden_chestplate;
    private final Item leggings = Items.golden_leggings;
    private final Item boots = Items.golden_boots;

    public AutoBardKit() {
        super("AutoBardKit", ModType.MISCELLANEOUS, Keyboard.KEY_NONE, 0xFFE3CC4D);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE) && mc.currentScreen instanceof GuiRepair) {
            if (Objects.nonNull(mc.thePlayer.getCurrentEquippedItem())) {
                Item currentItem = mc.thePlayer.getCurrentEquippedItem().getItem();
                if (Objects.equals(currentItem, helmet) || Objects.equals(currentItem, chestplate) || Objects.equals(currentItem, leggings) || Objects.equals(currentItem, boots)) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                }
            }
        }
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
