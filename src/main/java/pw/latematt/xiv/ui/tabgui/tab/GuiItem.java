package pw.latematt.xiv.ui.tabgui.tab;

import pw.latematt.xiv.mod.Mod;

/**
 * @author Jack
 * @editor Ddong because im the best
 */
public class GuiItem {
    private final Mod mod;

    public GuiItem(final Mod mod) {
        this.mod = mod;
    }

    public Mod getMod() {
        return mod;
    }

    public String getName() {
        return mod.getName();
    }
}
