package pw.latematt.xiv.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class RenderUtils {
    private static final Value<Float> LINE_WIDTH = new Value<>("render_line_width", 1.0F);
    private static final Value<Boolean> ANTIALIASING = new Value<>("render_anti_aliasing", false);
    private static final Value<Boolean> WORLDBOBBING = new Value<>("render_world_bobbing", false);
    private static final Value<Double> NAMETAG_OPACITY = new Value<>("render_nametag_opacity", 0.25D);
    private static final Value<Double> NAMETAG_SIZE = new Value<>("render_nametag_size", 1.0D);
    private static final Value<Boolean> TRACER_ENTITY = new Value<>("render_tracer_entity", true);

    public static void init() {
        ANTIALIASING.getValue();
        WORLDBOBBING.getValue();
        NAMETAG_OPACITY.getValue();
        NAMETAG_SIZE.getValue();
        TRACER_ENTITY.getValue();
    }

    public static void beginGl() {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.func_179090_x();
        if (ANTIALIASING.getValue()) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }
        GL11.glLineWidth(LINE_WIDTH.getValue());
    }

    public static void endGl() {
        GL11.glLineWidth(2.0F);
        if (ANTIALIASING.getValue()) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }
        GlStateManager.func_179098_w();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static void drawLines(AxisAlignedBB bb) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawing(2);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawing(2);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawing(2);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawing(2);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        tessellator.draw();
        worldRenderer.startDrawing(2);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawing(2);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tessellator.draw();
    }

    public static void drawFilledBox(AxisAlignedBB bb) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.maxZ);
        worldRenderer.addVertex(bb.minX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.minX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.minZ);
        worldRenderer.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        worldRenderer.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tessellator.draw();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.func_179090_x();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        var10.startDrawingQuads();
        var10.addVertex(left, bottom, 0.0D);
        var10.addVertex(right, bottom, 0.0D);
        var10.addVertex(right, top, 0.0D);
        var10.addVertex(left, top, 0.0D);
        var9.draw();
        GlStateManager.func_179098_w();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float var7 = (float) (startColor >> 24 & 255) / 255.0F;
        float var8 = (float) (startColor >> 16 & 255) / 255.0F;
        float var9 = (float) (startColor >> 8 & 255) / 255.0F;
        float var10 = (float) (startColor & 255) / 255.0F;
        float var11 = (float) (endColor >> 24 & 255) / 255.0F;
        float var12 = (float) (endColor >> 16 & 255) / 255.0F;
        float var13 = (float) (endColor >> 8 & 255) / 255.0F;
        float var14 = (float) (endColor & 255) / 255.0F;
        GlStateManager.func_179090_x();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator var15 = Tessellator.getInstance();
        WorldRenderer var16 = var15.getWorldRenderer();
        var16.startDrawingQuads();
        var16.func_178960_a(var8, var9, var10, var7);
        var16.addVertex(right, top, 0);
        var16.addVertex(left, top, 0);
        var16.func_178960_a(var12, var13, var14, var11);
        var16.addVertex(left, bottom, 0);
        var16.addVertex(right, bottom, 0);
        var15.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.func_179098_w();
    }

    public static void drawBorderedRect(double left, double top, double right, double bottom, float borderWidth, int borderColor, int color) {
        float alpha = (borderColor >> 24 & 0xFF) / 255.0f;
        float red = (borderColor >> 16 & 0xFF) / 255.0f;
        float green = (borderColor >> 8 & 0xFF) / 255.0f;
        float blue = (borderColor & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        drawRect(left, top, right, bottom, color);
        GlStateManager.enableBlend();
        GlStateManager.func_179090_x();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        if (borderWidth == 1.0F) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }

        GL11.glLineWidth(borderWidth);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawing(1);
        worldRenderer.addVertex(left, top, 0.0F);
        worldRenderer.addVertex(left, bottom, 0.0F);
        worldRenderer.addVertex(right, bottom, 0.0F);
        worldRenderer.addVertex(right, top, 0.0F);
        worldRenderer.addVertex(left, top, 0.0F);
        worldRenderer.addVertex(right, top, 0.0F);
        worldRenderer.addVertex(left, bottom, 0.0F);
        worldRenderer.addVertex(right, bottom, 0.0F);
        tessellator.draw();
        GL11.glLineWidth(2.0F);

        if (borderWidth == 1.0F) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        GlStateManager.func_179098_w();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBorderedRect(double left, double top, double right, double bottom, int borderColor, int color) {
        drawBorderedRect(left, top, right, bottom, 1.0F, borderColor, color);
    }

    public static void drawBorderedGradientRect(double left, double top, double right, double bottom, float borderWidth, int borderColor, int startColor, int endColor) {
        float alpha = (borderColor >> 24 & 0xFF) / 255.0f;
        float red = (borderColor >> 16 & 0xFF) / 255.0f;
        float green = (borderColor >> 8 & 0xFF) / 255.0f;
        float blue = (borderColor & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        drawGradientRect(left, top, right, bottom, startColor, endColor);
        GlStateManager.enableBlend();
        GlStateManager.func_179090_x();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);

        if (borderWidth == 1.0F) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }

        GL11.glLineWidth(borderWidth);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.startDrawing(1);
        worldRenderer.addVertex(left, top, 0.0F);
        worldRenderer.addVertex(left, bottom, 0.0F);
        worldRenderer.addVertex(right, bottom, 0.0F);
        worldRenderer.addVertex(right, top, 0.0F);
        worldRenderer.addVertex(left, top, 0.0F);
        worldRenderer.addVertex(right, top, 0.0F);
        worldRenderer.addVertex(left, bottom, 0.0F);
        worldRenderer.addVertex(right, bottom, 0.0F);
        tessellator.draw();
        GL11.glLineWidth(2.0F);

        if (borderWidth == 1.0F) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        GlStateManager.func_179098_w();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBorderedGradientRect(double left, double top, double right, double bottom, int borderColor, int startColor, int endColor) {
        drawBorderedGradientRect(left, top, right, bottom, 1.0F, borderColor, startColor, endColor);
    }

    public static ScaledResolution newScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }
}
