package pw.latematt.xiv.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import pw.latematt.xiv.XIV;

/**
 * @author Matthew
 */
public class ChatLogger {
    private static final String PREFIX = "\247g[XIV]:\247r ";
    private static boolean enabled = true;

    public static void print(String message) {
        if(enabled) {
            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(PREFIX + message));
            } else {
                XIV.getInstance().getLogger().info(message);
            }
        }
    }

    public static void setEnabled(boolean enable) {
        enabled = enable;
    }
}
