package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.mod.Mod;

/**
 * @author Matthew
 */
public class Test extends Mod {
    public Test() {
        super("Test", Keyboard.KEY_F, 0xFF696969);
    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisabled() {

    }
}
