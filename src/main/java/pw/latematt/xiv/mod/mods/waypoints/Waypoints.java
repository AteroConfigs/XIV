package pw.latematt.xiv.mod.mods.waypoints;

import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthew
 */
public class Waypoints extends Mod implements Listener<Render3DEvent>,CommandHandler {
    private final float[] color = new float[]{0.68F, 0.45F, 0.76F};
    private final List<Waypoint> points;
    private Value<Boolean> boxes = new Value<>("waypoints_boxes", true);
    private Value<Boolean> tracerLines = new Value<>("waypoints_tracer_lines", true);
    private Value<Boolean> nametags = new Value<>("waypoints_nametags", true);
    private Value<Float> lineWidth = new Value<>("waypoints_line_width", 1.0F);

    public Waypoints() {
        super("Waypoints", ModType.RENDER);

        points = new ArrayList<>();
        Command.newCommand()
                .cmd("waypoints")
                .description("Base command for Waypoints mod.")
                .arguments("<action>")
                .aliases("points", "wp")
                .handler(this)
                .build();
    }

    @Override
    public void onEventCalled(Render3DEvent event) {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.func_179090_x();
        GL11.glLineWidth(lineWidth.getValue());
        for (Waypoint waypoint : points) {
            double x = waypoint.getX() - mc.getRenderManager().renderPosX;
            double y = waypoint.getY() - mc.getRenderManager().renderPosY;
            double z = waypoint.getZ() - mc.getRenderManager().renderPosZ;

            if (boxes.getValue()) {
                drawBoxes(waypoint, x, y, z);
            }

            if (tracerLines.getValue()) {
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                mc.entityRenderer.orientCamera(event.getPartialTicks());
                drawTracerLines(waypoint, x, y, z);
                GlStateManager.popMatrix();
            }

            if (nametags.getValue()) {
                drawNametags(waypoint, x, y, z);
            }
        }

        GL11.glLineWidth(2.0F);
        GlStateManager.func_179098_w();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void drawBoxes(Waypoint waypoint, double x, double y, double z) {
        AxisAlignedBB box = AxisAlignedBB.fromBounds(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        GlStateManager.color(color[0], color[1], color[2], 0.6F);
        RenderUtils.drawLines(box);
        RenderGlobal.drawOutlinedBoundingBox(box, -1);
        GlStateManager.color(color[0], color[1], color[2], 0.11F);
        RenderUtils.drawFilledBox(box);
    }

    private void drawTracerLines(Waypoint waypoint, double x, double y, double z) {
        GlStateManager.color(color[0], color[1], color[2], 1.0F);
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        var3.startDrawing(2);
        var3.addVertex(0, mc.thePlayer.getEyeHeight(), 0);
        var3.addVertex(x, y, z);
        var2.draw();
    }

    private void drawNametags(Waypoint point, double x, double y, double z) {
        final double dist = mc.thePlayer.getDistance(point.getX(),
                point.getY(), point.getZ()) / 3;
        final String text = point.getName() + " (" + Math.round(dist * 3)
                + "m)";
        double far = this.mc.gameSettings.renderDistanceChunks * 12.8D;
        double dl = Math.sqrt(x * x + z * z + y * y);
        double distance = Math.sqrt(x * x + z * z + y * y);
        double d;
        if (dl > far) {
            d = far / dl;
            x *= d;
            z *= d;
            y *= d;
            dl = far;
        }
    }

    @Override
    public void onCommandRan(String message) {

    }

    public List<Waypoint> getPoints() {
        return points;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
