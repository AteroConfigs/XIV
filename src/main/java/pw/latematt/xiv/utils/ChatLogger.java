package pw.latematt.xiv.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

/**
 * @author Matthew
 */
public class ChatLogger {
    private static final String PREFIX = "\2473[XIV]:\247r ";
    public static void print(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(PREFIX + message));
    }
}
