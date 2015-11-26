package pw.latematt.xiv.mod.mods.render;

import net.minecraft.entity.effect.EntityLightningBolt;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AddWeatherEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * A mod that removes all lightning strikes.
 *
 * @author Tasmanian
 * @since Nov 25, 2015
 */
public class NoLightning extends Mod implements Listener<AddWeatherEvent> {

    public NoLightning() {
        super("NoLightning", ModType.RENDER);
    }

    @Override
    public void onEventCalled(AddWeatherEvent event) {
        if (event.getEntity() instanceof EntityLightningBolt) {
            event.cancel();
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
