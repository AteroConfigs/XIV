package pw.latematt.xiv.mod;

import net.minecraft.client.Minecraft;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;

/**
 * @author Matthew
 */
public abstract class Mod implements CommandHandler {
    protected final Minecraft mc = Minecraft.getMinecraft();
    private String name;
    private int keybind, color;
    private boolean enabled, visible;

    public Mod(String name, String[] commandArguments, String[] commandAliases) {
        this(name, 0, -1, false, commandArguments, commandAliases);
    }
    public Mod(String name, int keybind, int color, String[] commandArguments, String[] commandAliases) {
        this(name, keybind, color, true, commandArguments, commandAliases);
    }
    public Mod(String name, int keybind, String[] commandArguments, String[] commandAliases) {
        this(name, keybind, -1, false, commandArguments, commandAliases);
    }
    public Mod(String name, int keybind, int color, boolean visible, String[] commandArguments, String[] commandAliases) {
        this.name = name;
        this.keybind = keybind;
        this.color = color;
        this.visible = visible;

        Command.newCommand()
                .cmd(name.toLowerCase().replaceAll(" ", ""))
                .description("Base command for the " + name + " mod.")
                .arguments(commandArguments)
                .aliases(commandAliases)
                .handler(this)
                .build();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnabled();
        } else {
            onDisabled();
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            onEnabled();
        } else {
            onDisabled();
        }
    }

    public abstract void onEnabled();

    public abstract void onDisabled();
}
