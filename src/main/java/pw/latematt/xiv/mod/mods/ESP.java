package pw.latematt.xiv.mod.mods;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
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

import java.util.List;
import java.util.stream.Collectors;

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
        List<EntityPlayer> withoutDuplicates = mc.theWorld.playerEntities.stream().distinct().collect(Collectors.toList());
        if (!withoutDuplicates.isEmpty()) {
            RenderUtils.beginGl();
            for (Entity entity : withoutDuplicates) {
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
                    GlStateManager.pushMatrix();
                    drawSpines(entity, x, y, z);
                    GlStateManager.popMatrix();
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
    }

    private boolean isValidEntity(Entity entity) {
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
                    players.setValue(!players.getValue());
                    ChatLogger.print(String.format("ESP will %s display players.", (players.getValue() ? "now" : "no longer")));
                    break;
                case "mobs":
                    mobs.setValue(!mobs.getValue());
                    ChatLogger.print(String.format("ESP will %s display mobs.", (mobs.getValue() ? "now" : "no longer")));
                    break;
                case "animals":
                    animals.setValue(!animals.getValue());
                    ChatLogger.print(String.format("ESP will %s display animals.", (animals.getValue() ? "now" : "no longer")));
                    break;
                case "items":
                    items.setValue(!items.getValue());
                    ChatLogger.print(String.format("ESP will %s display items.", (items.getValue() ? "now" : "no longer")));
                    break;
                case "enderpearls":
                case "eps":
                    enderpearls.setValue(!enderpearls.getValue());
                    ChatLogger.print(String.format("ESP will %s display enderpearls.", (enderpearls.getValue() ? "now" : "no longer")));
                    break;
                case "boxes":
                case "box":
                    boxes.setValue(!boxes.getValue());
                    ChatLogger.print(String.format("ESP will %s display boxes.", (boxes.getValue() ? "now" : "no longer")));
                    break;
                case "tracerlines":
                case "tracers":
                    tracerLines.setValue(!tracerLines.getValue());
                    ChatLogger.print(String.format("ESP will %s display tracer lines.", (tracerLines.getValue() ? "now" : "no longer")));
                    break;
                case "spine":
                case "spines":
                    spines.setValue(!spines.getValue());
                    ChatLogger.print(String.format("ESP will %s display player spines.", (spines.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: players, mobs, animals, items, enderpearls, boxes, tracerlines, linewidth, spines");
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
}
