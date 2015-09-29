package pw.latematt.xiv.utils;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.AxisAlignedBB;

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
}
