package pw.latematt.xiv.mod.mods;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.IngameHUDRenderEvent;
import pw.latematt.xiv.mod.Mod;

/**
 * @author Matthew
 */
public class HUD extends Mod implements Listener<IngameHUDRenderEvent> {
    public HUD() {
        super("HUD");
        setEnabled(true);
    }

    @Override
    public void onEventCalled(IngameHUDRenderEvent event) {
        mc.fontRendererObj.drawStringWithShadow("XIV", 2, 2, 0xFFFFFFFF);
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
