package pw.latematt.xiv.mod.mods;

import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 * @author TehNeon
 */
public class ESP extends Mod implements Listener<Render3DEvent>, CommandHandler {
    public final Value<Boolean> players = new Value<>("esp_players", true);
    public final Value<Boolean> mobs = new Value<>("esp_mobs", false);
    public final Value<Boolean> animals = new Value<>("esp_animals", false);
    public final Value<Boolean> items = new Value<>("esp_items", false);
    public final Value<Boolean> enderpearls = new Value<>("esp_enderpearls", false);
    public final Value<Boolean> boxes = new Value<>("esp_boxes", true);
    public final Value<Boolean> outline = new Value<>("esp_outline", false);
    public final Value<Boolean> spines = new Value<>("esp_spines", false);
    public final Value<Boolean> tracerLines = new Value<>("esp_tracer_lines", false);

    public ESP() {
        super("ESP", ModType.RENDER, Keyboard.KEY_I);

        Command.newCommand()
                .cmd("esp")
                .description("Base command for the ESP mod.")
                .arguments("<action>")
                .handler(this)
                .build();
    }

    public void onEventCalled(Render3DEvent event) {
        RenderUtils.beginGl();
        for (Entity entity : mc.theWorld.playerEntities) {
            if (!isValidEntity(entity))
                continue;

            float partialTicks = event.getPartialTicks();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().renderPosZ;

            if (boxes.getValue()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                GlStateManager.rotate(-entity.rotationYaw, 0.0F, entity.height, 0.0F);
                GlStateManager.translate(-x, -y, -z);
                drawBoxes(entity, x, y, z);
                GlStateManager.popMatrix();
            }

            if (spines.getValue()) {
                drawSpines(entity, x, y, z);
            }

            if (tracerLines.getValue()) {
                GlStateManager.pushMatrix();
                GlStateManager.loadIdentity();
                mc.entityRenderer.orientCamera(partialTicks);
                drawTracerLines(entity, x, y, z);
                GlStateManager.popMatrix();
            }
        }
        RenderUtils.endGl();
    }

    public boolean isValidEntity(Entity entity) {
        if (entity == null)
            return false;
        if (entity == mc.thePlayer)
            return false;
        if (!entity.isEntityAlive())
            return false;
        if (entity instanceof EntityLivingBase) {
            if (entity.ticksExisted < 20)
                return false;
            if (entity instanceof EntityPlayer) {
                return players.getValue();
            } else if (entity instanceof IAnimals && !(entity instanceof IMob)) {
                return animals.getValue();
            } else if (entity instanceof IMob) {
                return mobs.getValue();
            }
        } else if (entity instanceof EntityEnderPearl) {
            return enderpearls.getValue();
        } else if (entity instanceof EntityItem) {
            return items.getValue();
        }
        return false;
    }

    private void drawBoxes(Entity entity, double x, double y, double z) {
        AxisAlignedBB box = AxisAlignedBB.fromBounds(x - entity.width, y, z - entity.width, x + entity.width, y + entity.height + 0.2D, z + entity.width);
        if (entity instanceof EntityLivingBase) {
            box = AxisAlignedBB.fromBounds(x - entity.width + 0.2D, y, z - entity.width + 0.2D, x + entity.width - 0.2D, y + entity.height + (entity.isSneaking() ? 0.02D : 0.2D), z + entity.width - 0.2D);
        }

        final float distance = mc.thePlayer.getDistanceToEntity(entity);
        float[] color = new float[]{0.0F, 0.9F, 0.0F};
        if (entity instanceof EntityPlayer && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            color = new float[]{0.3F, 0.7F, 1.0F};
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = new float[]{1.0F, 0.9F, 0.0F};
        } else if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).hurtTime > 0) {
            color = new float[]{1.0F, 0.66F, 0.0F};
        } else if (distance <= 3.9F) {
            color = new float[]{0.9F, 0.0F, 0.0F};
        }

        GlStateManager.color(color[0], color[1], color[2], 0.6F);
        RenderUtils.drawLines(box);
        GlStateManager.color(color[0], color[1], color[2], 0.6F);
        RenderGlobal.drawOutlinedBoundingBox(box, -1);
    }

    private void drawTracerLines(Entity entity, double x, double y, double z) {
        final float distance = mc.thePlayer.getDistanceToEntity(entity);
        float[] color = new float[]{0.0F, 0.90F, 0.0F};
        if (entity instanceof EntityPlayer && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            color = new float[]{0.30F, 0.7F, 1.0F};
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = new float[]{1.0F, 0.9F, 0.0F};
        } else if (distance <= 64.0F) {
            color = new float[]{0.9F, distance / 64.0F, 0.0F};
        }

        GlStateManager.color(color[0], color[1], color[2], 1.0F);
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        var3.startDrawing(2);
        var3.addVertex(0, mc.thePlayer.getEyeHeight(), 0);
        var3.addVertex(x, y, z);
        var2.draw();
    }

    private void drawSpines(Entity entity, double x, double y, double z) {
        final float distance = mc.thePlayer.getDistanceToEntity(entity);
        float[] color = new float[]{0.0F, 0.90F, 0.0F};
        if (entity instanceof EntityPlayer && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            color = new float[]{0.30F, 0.7F, 1.0F};
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = new float[]{1.0F, 0.9F, 0.0F};
        } else if (distance <= 64.0F) {
            color = new float[]{0.9F, distance / 64.0F, 0.0F};
        }

        GlStateManager.color(color[0], color[1], color[2], 1.0F);
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        var3.startDrawing(2);
        var3.addVertex(x, y, z);
        var3.addVertex(x, y + entity.getEyeHeight(), z);
        var2.draw();
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];

            switch (action.toLowerCase()) {
                case "players":
                case "plyrs":
                    if (arguments.length >= 3) {
                        players.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        players.setValue(!players.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display players.", (players.getValue() ? "now" : "no longer")));
                    break;
                case "mobs":
                    if (arguments.length >= 3) {
                        mobs.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        mobs.setValue(!mobs.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display mobs.", (mobs.getValue() ? "now" : "no longer")));
                    break;
                case "animals":
                    if (arguments.length >= 3) {
                        animals.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        animals.setValue(!animals.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display animals.", (animals.getValue() ? "now" : "no longer")));
                    break;
                case "items":
                    if (arguments.length >= 3) {
                        items.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        items.setValue(!items.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display items.", (items.getValue() ? "now" : "no longer")));
                    break;
                case "enderpearls":
                case "eps":
                    if (arguments.length >= 3) {
                        enderpearls.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        enderpearls.setValue(!enderpearls.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display enderpearls.", (enderpearls.getValue() ? "now" : "no longer")));
                    break;
                case "boxes":
                case "box":
                    if (arguments.length >= 3) {
                        boxes.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        boxes.setValue(!boxes.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display boxes.", (boxes.getValue() ? "now" : "no longer")));
                    break;
                case "outline":
                case "outl":
                    if (arguments.length >= 3) {
                        outline.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        outline.setValue(!outline.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display a glow.", (outline.getValue() ? "now" : "no longer")));
                    break;
                case "tracerlines":
                case "tracers":
                    if (arguments.length >= 3) {
                        tracerLines.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        tracerLines.setValue(!tracerLines.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display tracer lines.", (tracerLines.getValue() ? "now" : "no longer")));
                    break;
                case "spine":
                case "spines":
                    if (arguments.length >= 3) {
                        spines.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        spines.setValue(!spines.getValue());
                    }
                    ChatLogger.print(String.format("ESP will %s display player spines.", (spines.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: players, mobs, animals, items, enderpearls, boxes, tracerlines, spines");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: esp <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }

    public static void renderOne() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(4f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearStencil(0xF);
        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
    }

    public static void renderTwo() {
        GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
        // TODO: Changeable: line underneath
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
    }

    public static void renderThree() {
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
    }

    public static void renderFour() {
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(1, -2000000f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                240f, 240f);
    }

    public static void renderFive() {
        GL11.glPolygonOffset(1, 2000000);
        GL11.glDisable(10754);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }
}
