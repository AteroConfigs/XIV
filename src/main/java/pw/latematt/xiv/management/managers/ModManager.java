package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.mods.HUD;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class ModManager extends ListManager<Mod> {
    public ModManager() {
        super(new ArrayList<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + "...");
        contents.add(new HUD());
        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }
}
