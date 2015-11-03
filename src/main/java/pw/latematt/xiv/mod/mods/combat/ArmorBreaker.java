package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.network.play.client.C0EPacketClickWindow;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.player.AutoHeal;

/**
 * @author Jack
 */
public class ArmorBreaker extends Mod implements Listener<AttackEntityEvent> {
    public ArmorBreaker() {
        super("ArmorBreaker", ModType.COMBAT, Keyboard.KEY_NONE, 0xFF808080);
    }

    @Override
    public void onEventCalled(AttackEntityEvent event) {
        AutoHeal autoHeal = (AutoHeal) XIV.getInstance().getModManager().find("autoheal");
        if (autoHeal != null && autoHeal.isHealing())
            return;

        if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.inventoryContainer.getSlot(27).getStack() != null) {
            mc.getNetHandler().addToSendQueue(new C0EPacketClickWindow(mc.thePlayer.inventoryContainer.windowId, 27, mc.thePlayer.inventory.currentItem, 2, mc.thePlayer.getCurrentEquippedItem(), mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory)));
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