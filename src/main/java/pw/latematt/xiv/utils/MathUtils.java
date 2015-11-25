package pw.latematt.xiv.utils;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

/**
 * @author TehNeon
 */
public class MathUtils {
    // mouseX * scaledWidth / displayWidth
    public static int getMouseX() {
        return Mouse.getX() * RenderUtils.newScaledResolution().getScaledWidth() / Minecraft.getMinecraft().displayWidth;
    }

    // scaledHeight - mouseY * scaledHeight / displayHeight
    public static int getMouseY() {
        return RenderUtils.newScaledResolution().getScaledHeight() - Mouse.getY() * RenderUtils.newScaledResolution().getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
    }
}
