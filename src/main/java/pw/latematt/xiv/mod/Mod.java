package pw.latematt.xiv.mod;

import net.minecraft.client.Minecraft;
import pw.latematt.xiv.XIV;

/**
 * @author Matthew
 */
public abstract class Mod {
    protected final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private String tag;
    private int keybind, color;
    private boolean enabled, visible;
    private ModType type;

    public Mod(String name, ModType type) {
        this(name, type, 0, -1, false);
    }

    public Mod(String name, ModType type, int keybind, int color) {
        this(name, type, keybind, color, true);
    }

    public Mod(String name, ModType type, int keybind) {
        this(name, type, keybind, -1, false);
    }

    public Mod(String name, ModType type, int keybind, int color, boolean visible) {
        this.name = name;
        this.keybind = keybind;
        this.color = color;
        this.visible = visible;
        this.type = type;
        tag = "";
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        if (XIV.getInstance().getValueManager().find("render_show_tags") != null && ((Boolean) XIV.getInstance().getValueManager().find("render_show_tags").getValue()) || XIV.getInstance().getValueManager().find("render_show_tags") == null) {
            return tag;
        }
        return getName();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
        XIV.getInstance().getFileManager().saveFile("modconfig");
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        XIV.getInstance().getFileManager().saveFile("modconfig");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ModType getModType() {
        return type;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnabled();
        } else {
            onDisabled();
        }

        XIV.getInstance().getFileManager().saveFile("modconfig");
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        XIV.getInstance().getFileManager().saveFile("modconfig");
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            onEnabled();
        } else {
            onDisabled();
        }

        XIV.getInstance().getFileManager().saveFile("modconfig");
    }

    public abstract void onEnabled();

    public abstract void onDisabled();
}
