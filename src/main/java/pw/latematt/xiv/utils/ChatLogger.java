package pw.latematt.xiv.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import pw.latematt.xiv.XIV;

/**
 * @author Matthew
 */
public class ChatLogger {
    private static final String PREFIX = "\2473[XIV]:\247r ";

    public static void print(String message) {
        if (Minecraft.getMinecraft().inGameHasFocus) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(PREFIX + message));
        } else {
            XIV.getInstance().getLogger().info(message);
        }
    }
}
