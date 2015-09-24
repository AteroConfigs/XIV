package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.mods.HUD;
import pw.latematt.xiv.utils.ChatLogger;

import java.util.ArrayList;
import java.util.List;

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


        Command.newCommand()
                .cmd("mods")
                .description("Provides help with commands.")
                .aliases("modules", "hacks", "cheats")
                .handler(message -> {
                    List<Mod> moduleList = XIV.getInstance().getModManager().getContents();
                    StringBuilder mods = new StringBuilder("Modules (" + moduleList.size() + "): ");
                    for (Mod mod : moduleList) {
                        mods.append(mod.isEnabled() ? "\247a" : "\247c").append(mod.getName()).append("\247r, ");
                    }
                    ChatLogger.print(mods.toString().substring(0, mods.length() - 2));
                }).build();
    }
}
