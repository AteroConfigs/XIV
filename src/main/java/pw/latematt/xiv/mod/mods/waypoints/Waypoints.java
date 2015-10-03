package pw.latematt.xiv.mod.mods.waypoints;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.management.file.XIVFile;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author Matthew
 */
public class Waypoints extends Mod implements Listener<Render3DEvent>, CommandHandler {
    private final float[] color = new float[]{0.68F, 0.45F, 0.76F};
    private final List<Waypoint> points;
    private final Value<Boolean> boxes = new Value<>("waypoints_boxes", true);
    private final Value<Boolean> tracerLines = new Value<>("waypoints_tracer_lines", true);
    private final Value<Boolean> nametags = new Value<>("waypoints_nametags", true);
    private final Value<Float> lineWidth = new Value<>("waypoints_line_width", 1.0F);
    private final XIVFile waypointFile;

    public Waypoints() {
        super("Waypoints", ModType.RENDER);

        points = new CopyOnWriteArrayList<>();
        waypointFile = new XIVFile("waypoints", "json") {
            @Override
            public void load() throws IOException {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                List<Waypoint> pointsFromFile = gson.fromJson(reader, new TypeToken<List<Waypoint>>() {
                }.getType());
                points.addAll(pointsFromFile.stream().collect(Collectors.toList()));
            }

            @Override
            public void save() throws IOException {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Files.write(gson.toJson(points).getBytes("UTF-8"), file);
            }
        };
        Command.newCommand()
                .cmd("waypoints")
                .description("Base command for Waypoints mod.")
                .arguments("<action>")
                .aliases("points", "wp")
                .handler(this)
                .build();
        try {
            waypointFile.load();
        } catch (IOException e) {
            XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not load, a stack trace has been printed.", waypointFile.getName(), waypointFile.getExtension()));
            e.printStackTrace();
        }
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
            String server;
            if (mc.getCurrentServerData() == null) {
                server = "singleplayer";
            } else {
                server = mc.getCurrentServerData().serverIP;
            }
            if (!waypoint.getServer().equals(server))
                continue;

            double x = waypoint.getX() + 0.5F - mc.getRenderManager().renderPosX;
            double y = waypoint.getY() - mc.getRenderManager().renderPosY;
            double z = waypoint.getZ() + 0.5F - mc.getRenderManager().renderPosZ;

            if (boxes.getValue()) {
                drawBoxes(x, y, z);
            }

            if (tracerLines.getValue()) {
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                mc.entityRenderer.orientCamera(event.getPartialTicks());
                drawTracerLines(x, y, z);
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

    private void drawBoxes(double x, double y, double z) {
        AxisAlignedBB box = AxisAlignedBB.fromBounds(x - 0.5D, y, z - 0.5D, x + 0.5D, y + 1.0D, z + 0.5D);
        GlStateManager.color(color[0], color[1], color[2], 0.6F);
        RenderUtils.drawLines(box);
        RenderGlobal.drawOutlinedBoundingBox(box, -1);
        GlStateManager.color(color[0], color[1], color[2], 0.11F);
        RenderUtils.drawFilledBox(box);
    }

    private void drawTracerLines(double x, double y, double z) {
        GlStateManager.color(color[0], color[1], color[2], 1.0F);
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        var3.startDrawing(2);
        var3.addVertex(0, mc.thePlayer.getEyeHeight(), 0);
        var3.addVertex(x, y, z);
        var2.draw();
    }

    private void drawNametags(Waypoint point, double x, double y, double z) {
        final double dist = mc.thePlayer.getDistance(point.getX(), point.getY(), point.getZ());
        final String text = point.getName() + " \2477" + Math.round(dist) + "m\247r";
        double far = this.mc.gameSettings.renderDistanceChunks * 12.8D;
        double dl = Math.sqrt(x * x + z * z + y * y);
        double d;
        if (dl > far) {
            d = far / dl;
            x *= d;
            z *= d;
            y *= d;
            dl = far;
        }

        float var13 = (float) dist / 5 <= 2 ? 2.0F : (float) dist / 5;
        float var14 = 0.016666668F * var13;
        if (var14 > 0.4F) {
            var14 = 0.4F;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(x, y + 1.5F, z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        if (mc.gameSettings.thirdPersonView == 2) {
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
        } else {
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        }
        GlStateManager.scale(-var14, -var14, var14);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.func_179090_x();
        worldRenderer.startDrawingQuads();
        int var18 = mc.fontRendererObj.getStringWidth(text) / 2;
        worldRenderer.func_178960_a(0.0F, 0.0F, 0.0F, 0.25F);
        worldRenderer.addVertex(-var18 - 2, -2, 0.0D);
        worldRenderer.addVertex(-var18 - 2, 9, 0.0D);
        worldRenderer.addVertex(var18 + 2, 9, 0.0D);
        worldRenderer.addVertex(var18 + 2, -2, 0.0D);
        tessellator.draw();
        GlStateManager.func_179098_w();
        mc.fontRendererObj.drawStringWithShadow(text, -var18, 0, 0xFFFFFFFF);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "add":
                case "a":
                    if (arguments.length >= 6) {
                        if (isInteger(arguments[2])) {
                            final int x = Integer.parseInt(arguments[2]);
                            final int y = Integer.parseInt(arguments[3]);
                            final int z = Integer.parseInt(arguments[4]);
                            final String name = message.substring((arguments[0] + " " + arguments[1] + " " + arguments[2] + " " + arguments[3] + " " + arguments[4] + " ").length());
                            String server;
                            if (mc.getCurrentServerData() == null) {
                                server = "singleplayer";
                            } else {
                                server = mc.getCurrentServerData().serverIP;
                            }
                            points.add(new Waypoint(name, server, x, y, z, true));
                            try {
                                waypointFile.save();
                            } catch (IOException e) {
                                XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not save, a stack trace has been printed.", waypointFile.getName(), waypointFile.getExtension()));
                                e.printStackTrace();
                            }
                            ChatLogger.print("Waypoint \"" + name + "\" added at " + x + ", " + y + ", " + z);
                        } else {
                            ChatLogger.print("You did not enter an Integer for x, y, or z.");
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: waypoints add <x> <y> <z> <name>");
                    }
                    break;
                case "del":
                case "d":
                    final String name = message.substring((arguments[0] + " " + arguments[1] + " ").length());
                    boolean found = false;
                    for (final Waypoint waypoint : points) {
                        if (waypoint.getName().toLowerCase().startsWith(name.toLowerCase())) {
                            points.remove(waypoint);
                            try {
                                waypointFile.save();
                            } catch (IOException e) {
                                XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not save, a stack trace has been printed.", waypointFile.getName(), waypointFile.getExtension()));
                                e.printStackTrace();
                            }
                            ChatLogger.print("Waypoint \"" + waypoint.getName() + "\" deleted.");
                            found = true;
                        }
                    }

                    if (!found) {
                        ChatLogger.print("Waypoint \"" + name + "\" not found.");
                    }
                    break;
                case "tracerlines":
                case "tracers":
                    tracerLines.setValue(!tracerLines.getValue());
                    ChatLogger.print(String.format("Waypoints will %s draw tracer lines.", tracerLines.getValue() ? "now" : "no longer"));
                    break;
                case "boxes":
                    boxes.setValue(!boxes.getValue());
                    ChatLogger.print(String.format("Waypoints will %s draw boxes.", boxes.getValue() ? "now" : "no longer"));
                    break;
                case "nametags":
                case "tags":
                    nametags.setValue(!nametags.getValue());
                    ChatLogger.print(String.format("Waypoints will %s draw nametags.", nametags.getValue() ? "now" : "no longer"));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: add, del, tracerlines, boxes, nametags");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: waypoints <action>");
        }
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
        } catch (final Exception e) {
            return false;
        }
        return true;
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
