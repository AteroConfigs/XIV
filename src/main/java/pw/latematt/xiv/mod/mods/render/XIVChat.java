package pw.latematt.xiv.mod.mods.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.GuiXIVChat;

import java.util.ArrayList;

public class XIVChat extends Mod {
    public XIVChat() {
        super("XIVChat", ModType.RENDER);
    }

    private GuiXIVChat xivChat;
    private GuiNewChat originalChat;

    @Override
    public void onEnabled() {
        createChats();

        copyLines(originalChat, xivChat);

        mc.ingameGUI.setChatGUI(xivChat);
    }

    @Override
    public void onDisabled() {
        createChats();

        copyLines(xivChat, originalChat);

        mc.ingameGUI.setChatGUI(originalChat);
    }

    private void createChats() {
        if(xivChat == null) {
            this.xivChat = new GuiXIVChat(mc);
        }

        if(originalChat == null) {
            this.originalChat = new GuiNewChat(mc);
        }
    }

    private void copyLines(GuiNewChat oldChat, GuiNewChat newChat) {
        for(Object o: oldChat.chatLines) {
            newChat.chatLines.add(o);
        }

        ArrayList<String> sentMessages = new ArrayList<>();

        for(Object o: oldChat.sentMessages) {
            newChat.sentMessages.add(o);
        }

        for(Object o: oldChat.field_146253_i) {
            newChat.field_146253_i.add(o);
        }

        oldChat.chatLines.clear();
        oldChat.sentMessages.clear();
        oldChat.field_146253_i.clear();
    }
}
