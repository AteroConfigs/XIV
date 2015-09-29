package pw.latematt.xiv.management.managers;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.mods.*;
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
        contents.add(new ClickGUI());
        contents.add(new ESP());
        contents.add(new Fly());
        contents.add(new HUD());
        contents.add(new InventoryWalk());
        contents.add(new KillAura());
        contents.add(new Nametags());
        contents.add(new NoFall());
        contents.add(new NoSlowdown());
        contents.add(new Regen());
        contents.add(new Speed());
        contents.add(new Sprint());
        contents.add(new Step());
        contents.add(new Velocity());

        /* ALWAYS LOAD TABGUI LAST!!! */
        contents.add(new TabGUI());

        Command.newCommand()
                .cmd("mods")
                .description("Provides a list of all modules.")
                .aliases("modules", "hacks", "cheats", "lm")
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
                .description("Toggle modules on or off.")
                .aliases("t", "tog")
                .arguments("<module>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        String modName = arguments[1];
                        Mod mod = XIV.getInstance().getModManager().find(modName);

                        if (mod != null) {
                            mod.toggle();
                            ChatLogger.print(String.format("%s has been toggled %s.", mod.getName(), mod.isEnabled() ? "on" : "off"));
                        } else {
                            ChatLogger.print(String.format("Invalid module \"%s\"", modName));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: toggle <module>");
                    }
                }).build();

        Command.newCommand()
                .cmd("visible")
                .description("Toggle visibility of modules on or off in the arraylist.")
                .aliases("vis")
                .arguments("<module>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        String modName = arguments[1];
                        Mod mod = XIV.getInstance().getModManager().find(modName);

                        if (mod != null) {
                            mod.setVisible(!mod.isVisible());
                            ChatLogger.print(String.format("%s will %s be shown in the arraylist.", mod.getName(), mod.isVisible() ? "now" : "no longer"));
                        } else {
                            ChatLogger.print(String.format("Invalid module \"%s\"", modName));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: visible <module>");
                    }
                }).build();

        Command.newCommand()
                .cmd("bind")
                .description("Rebinds a module.")
                .arguments("<module> <key>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 3) {
                        String modName = arguments[1];
                        Mod mod = XIV.getInstance().getModManager().find(modName);

                        if (mod != null) {
                            String newBindName = arguments[2].toUpperCase();
                            int newBind = Keyboard.getKeyIndex(newBindName);
                            mod.setKeybind(newBind);
                            ChatLogger.print(String.format("%s is now bound to %s", mod.getName(), Keyboard.getKeyName(newBind)));
                        } else {
                            ChatLogger.print(String.format("Invalid module \"%s\"", modName));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: bind <module> <key>");
                    }
                }).build();

        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public Mod find(Class clazz) {
        for (Mod mod : getContents()) {
            if (mod.getClass().equals(clazz)) {
                return mod;
            }
        }

        return null;
    }

    public Mod find(String name) {
        for (Mod mod : getContents()) {
            if (mod.getName().toLowerCase().replaceAll(" ", "").startsWith(name.toLowerCase().replaceAll(" ", ""))) {
                return mod;
            }
        }
        return null;
    }
}
