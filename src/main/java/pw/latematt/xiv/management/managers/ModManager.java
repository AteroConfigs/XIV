package pw.latematt.xiv.management.managers;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.mods.HUD;
import pw.latematt.xiv.mod.mods.Step;
import pw.latematt.xiv.mod.mods.Tracers;
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
        contents.add(new Step());
        contents.add(new Tracers());

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
