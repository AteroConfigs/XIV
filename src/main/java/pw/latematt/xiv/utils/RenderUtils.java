package pw.latematt.xiv.utils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

/**
 * @author Matthew
 */
public class RenderUtils {
    public static void drawLines(AxisAlignedBB bb) {
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        var3.startDrawing(2);
        var3.addVertex(bb.minX, bb.minY, bb.minZ);
        var3.addVertex(bb.minX, bb.maxY, bb.maxZ);
        var2.draw();
        var3.startDrawing(2);
        var3.addVertex(bb.maxX, bb.minY, bb.minZ);
        var3.addVertex(bb.minX, bb.maxY, bb.maxZ);
        var2.draw();
        var3.startDrawing(2);
        var3.addVertex(bb.maxX, bb.minY, bb.maxZ);
        var3.addVertex(bb.minX, bb.maxY, bb.maxZ);
        var2.draw();
        var3.startDrawing(2);
        var3.addVertex(bb.maxX, bb.minY, bb.minZ);
        var3.addVertex(bb.minX, bb.maxY, bb.minZ);
        var2.draw();
        var3.startDrawing(2);
        var3.addVertex(bb.maxX, bb.minY, bb.minZ);
        var3.addVertex(bb.minX, bb.minY, bb.maxZ);
        var2.draw();
        var3.startDrawing(2);
        var3.addVertex(bb.maxX, bb.maxY, bb.minZ);
        var3.addVertex(bb.minX, bb.maxY, bb.maxZ);
        var2.draw();
    }

    public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, col2);
        float f = (col1 >> 24 & 0xFF) / 255.0f;
        float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        float f4 = (col1 & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d((double) x, (double) y);
        GL11.glVertex2d((double) x, (double) y2);
        GL11.glVertex2d((double) x2, (double) y2);
        GL11.glVertex2d((double) x2, (double) y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }
}
