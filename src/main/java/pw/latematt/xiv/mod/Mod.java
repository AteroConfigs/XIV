package pw.latematt.xiv.mod;

import net.minecraft.client.Minecraft;

/**
 * @author Matthew
 */
public abstract class Mod {
    protected final Minecraft mc = Minecraft.getMinecraft();
    private String name;
    private int keybind, color;
    private boolean enabled, visible;

    public Mod(String name) {
        this(name, 0, -1, false);
    }
    public Mod(String name, int keybind, int color) {
        this(name, keybind, color, true);
    }
    public Mod(String name, int keybind) {
        this(name, keybind, -1, false);
    }
    public Mod(String name, int keybind, int color, boolean visible) {
        this.name = name;
        this.keybind = keybind;
        this.color = color;
        this.visible = visible;
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
