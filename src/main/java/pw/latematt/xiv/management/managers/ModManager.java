package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.mods.HUD;
import pw.latematt.xiv.utils.ChatLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Pack200;

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

        Command.newCommand()
                .cmd("mods")
                .description("Provides a list of all modules.")
                .aliases("modules", "hacks", "cheats")
                .handler(message -> {
                    List<Mod> moduleList = XIV.getInstance().getModManager().getContents();
                    StringBuilder mods = new StringBuilder("Modules (" + moduleList.size() + "): ");
                    for (Mod mod : moduleList) {
                        mods.append(mod.isEnabled() ? "\247a" : "\247c").append(mod.getName()).append("\247r, ");
                    }
                    ChatLogger.print(mods.toString().substring(0, mods.length() - 2));
                }).build();

        Command.newCommand()
                .cmd("toggle")
                .description("Allows you to toggle modules.")
                .aliases("t", "tog")
                .arguments("<module>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        String modName = arguments[1];
                        Mod mod = XIV.getInstance().getModManager().find(modName);

                        if(mod != null) {
                            mod.toggle();
                            ChatLogger.print(String.format("%s is now %s!", mod.getName(), mod.isEnabled() ? "enabled" : "disabled"));
                        } else {
                            ChatLogger.print("Module \"" + modName + "\" does not exist!");
                        }

                        return;
                    }

                    ChatLogger.print("Invalid arguments, valid: hud <module>");
                }).build();

        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public Mod find(Class clazz) {
        for(Mod mod: getContents()) {
            if(mod.getClass().equals(clazz)) {
                return mod;
            }
        }

        return null;
    }

    public Mod find(String name) {
        for(Mod mod: getContents()) {
            if(mod.getName().equalsIgnoreCase(name)) {
                return mod;
            }
        }
        return null;
    }
}
