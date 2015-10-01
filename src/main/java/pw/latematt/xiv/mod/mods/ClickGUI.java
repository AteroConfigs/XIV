package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.GuiScreenEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.clickgui.GuiClick;

public class ClickGUI extends Mod implements Listener<GuiScreenEvent> {
    public GuiClick screen;

    public ClickGUI() {
        super("ClickGUI", ModType.NONE, Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEventCalled(GuiScreenEvent event) {
        if (!(event.getScreen() instanceof GuiClick)) {
            this.setEnabled(false);
        }
    }

    @Override
    public void onEnabled() {
        if (!mc.inGameHasFocus) {
            this.setEnabled(false);
            return;
        }

        if (screen == null) {
            screen = new GuiClick();
        }

        XIV.getInstance().getListenerManager().add(this);
        this.mc.displayGuiScreen(screen);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
