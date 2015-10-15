package pw.latematt.xiv.mod.mods.none;

import net.minecraft.client.gui.GuiNewChat;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.GuiXIVChat;

public class XIVChat extends Mod {
    public XIVChat() {
        super("XIVChat", ModType.RENDER);
    }

    @Override
    public void onEnabled() {
        mc.ingameGUI.setChatGUI(new GuiXIVChat(mc));
    }

    @Override
    public void onDisabled() {
        mc.ingameGUI.setChatGUI(new GuiNewChat(mc));
    }
}
