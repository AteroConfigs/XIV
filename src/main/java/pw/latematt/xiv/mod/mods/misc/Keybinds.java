package pw.latematt.xiv.mod.mods.misc;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.KeyPressEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class Keybinds extends Mod implements Listener<KeyPressEvent> {
    public Keybinds() {
        super("Keybinds", ModType.MISCELLANEOUS);
        setEnabled(true);
    }

    @Override
    public void onEventCalled(KeyPressEvent event) {
        /* mod keybinds */
        XIV.getInstance().getModManager().getContents().stream()
                .filter(mod -> mod.getKeybind() != Keyboard.KEY_NONE)
                .filter(mod -> mod.getKeybind() == event.getKeyCode())
                .forEach(Mod::toggle);

        /* macro keybinds */
        XIV.getInstance().getMacroManager().getContents().stream()
                .filter(macro -> macro.getKeybind() != Keyboard.KEY_NONE)
                .filter(macro -> macro.getKeybind() == event.getKeyCode())
                .forEach(macro -> XIV.getInstance().getCommandManager().parseCommand(XIV.getInstance().getCommandManager().getPrefix() + macro.getCommand()));
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
