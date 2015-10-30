package pw.latematt.xiv.mod.mods.render;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.ItemRenderEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class NoItems extends Mod implements Listener<ItemRenderEvent> {
    public NoItems() {
        super("NoItems", ModType.RENDER);
    }

    @Override
    public void onEventCalled(ItemRenderEvent event) {
        event.setCancelled(true);
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
