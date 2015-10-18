package pw.latematt.xiv.mod.mods.render;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.IngameHUDRenderEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.render.HUD;
import pw.latematt.xiv.ui.tabgui.GuiTabHandler;
import pw.latematt.xiv.value.Value;

/**
 * @author Jack
 * @author Matthew
 */
public class TabGUI extends Mod implements Listener<IngameHUDRenderEvent> {
    private final GuiTabHandler guiHandler = new GuiTabHandler();
    private final Value<Boolean> watermark = (Value<Boolean>) XIV.getInstance().getValueManager().find("hud_watermark");
    private final Value<Boolean> rudysucks = (Value<Boolean>) XIV.getInstance().getValueManager().find("hud_rudysucks");
    private final Value<Boolean> time = (Value<Boolean>) XIV.getInstance().getValueManager().find("hud_time");
    private final HUD hud = (HUD) XIV.getInstance().getModManager().find("hud");

    public TabGUI() {
        super("TabGUI", ModType.RENDER);
    }

    public void onEventCalled(IngameHUDRenderEvent event) {
        if (mc.gameSettings.showDebugInfo)
            return;

        int tabY = 2;

        if (hud.isEnabled()) {
            if (watermark.getValue() || rudysucks.getValue() || time.getValue())
                tabY += 9;
            if (watermark.getValue() && rudysucks.getValue())
                tabY += 9;
        }

        guiHandler.drawGui(2, tabY);
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
