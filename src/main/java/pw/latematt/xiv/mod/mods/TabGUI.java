package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.IngameHUDRenderEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.tabgui.GuiTabHandler;

/**
 * @author Jack
 */

public class TabGUI extends Mod implements Listener<IngameHUDRenderEvent> {
    private GuiTabHandler guiHandler = new GuiTabHandler();

    public TabGUI() {
        super("TabGUI", ModType.RENDER, Keyboard.KEY_NONE);
    }

    public void onEventCalled(IngameHUDRenderEvent event) {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        this.guiHandler.drawGui();
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
