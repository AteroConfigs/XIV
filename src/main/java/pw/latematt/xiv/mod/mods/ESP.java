package pw.latematt.xiv.mod.mods;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.src.Reflector;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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
    public Value<Boolean> players = new Value<>("esp_players", true);
    public Value<Boolean> mobs = new Value<>("esp_mobs", false);
    public Value<Boolean> animals = new Value<>("esp_animals", false);
    public Value<Boolean> items = new Value<>("esp_items", false);
    public Value<Boolean> enderpearls = new Value<>("esp_enderpearls", false);
    public Value<Boolean> boxes = new Value<>("esp_boxes", true);
    public Value<Boolean> spines = new Value<>("esp_spines", false);
    public Value<Boolean> tracerlines = new Value<>("esp_tracerlines", false);
    public Value<Float> lineWidth = new Value<>("esp_lineWidth", 1.0F);

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
        GL11.glPushMatrix();
        mc.entityRenderer.func_175072_h();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(lineWidth.getValue());

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!isValidEntity(entity))
                continue;

            float partialTicks = event.getPartialTicks();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().renderPosZ;

            if (boxes.getValue()) {
            	GL11.glPushMatrix();
                GL11.glTranslated(x, y, z);
                GL11.glRotatef(-entity.rotationYaw, 0.0F, entity.height,
                        0.0F);
                GL11.glTranslated(-x, -y, -z);
                drawBoxes(entity, x, y, z);
                GL11.glPopMatrix();
            }

            if (tracerlines.getValue()) {
            	GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				orientCamera(partialTicks);
                drawTracerLines(entity, x, y, z);
            	GlStateManager.popMatrix();
            }
            if (spines.getValue()) {
                drawSpines(entity, x, y, z);
            }
        }

        GL11.glLineWidth(2.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.entityRenderer.func_180436_i();
        GL11.glPopMatrix();
    }

    private boolean isValidEntity(Entity entity) {
        if (entity == null)
            return false;
        if (entity == mc.thePlayer)
            return false;
        if (!entity.isEntityAlive())
            return false;
        if (entity instanceof EntityLivingBase) {
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
        float[] color;
        if (entity instanceof EntityPlayer && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            color = new float[]{0.3F, 0.7F, 1.0F};
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = new float[]{1.0F, 0.9F, 0.0F};
        } else if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).hurtTime > 0) {
            color = new float[]{1.0F, 0.66F, 0.0F};
        } else if (distance <= 3.9F) {
            color = new float[]{0.9F, 0.0F, 0.0F};
        } else {
            color = new float[]{0.0F, 0.9F, 0.0F};
        }

        GL11.glColor4f(color[0], color[1], color[2], 0.6F);
        RenderUtils.drawLines(box);
        GL11.glColor4f(color[0], color[1], color[2], 0.6F);
        RenderGlobal.drawOutlinedBoundingBox(box, -1);
    }

    public void drawTracerLines(Entity entity, double x, double y, double z) {
        final float distance = mc.thePlayer.getDistanceToEntity(entity);
        float[] color;
        if (entity instanceof EntityPlayer && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            color = new float[]{0.30F, 0.7F, 1.0F};
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = new float[]{1.0F, 0.9F, 0.0F};
        } else if (distance <= 64.0F) {
            color = new float[]{0.9F, distance / 64.0F, 0.0F};
        } else {
            color = new float[]{0.0F, 0.90F, 0.0F};
        }

        GL11.glColor4f(color[0], color[1], color[2], 1.0F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
    }

    private void drawSpines(Entity entity, double x, double y, double z) {
        final float distance = mc.thePlayer.getDistanceToEntity(entity);
        float[] color;
        if (entity instanceof EntityPlayer && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            color = new float[]{0.3F, 0.7F, 1.0F};
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = new float[]{1.0F, 0.9F, 0.0F};
        } else if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).hurtTime > 0) {
            color = new float[]{1.0F, 0.66F, 0.0F};
        } else if (distance <= 3.9F) {
            color = new float[]{0.9F, 0.0F, 0.0F};
        } else {
            color = new float[]{0.0F, 0.9F, 0.0F};
        }

        GL11.glColor4f(color[0], color[1], color[2], 1.0F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];

            switch (action) {
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
                    tracerlines.setValue(!tracerlines.getValue());
                    ChatLogger.print(String.format("ESP will %s display tracer lines.", (tracerlines.getValue() ? "now" : "no longer")));
                    break;
                case "spine":
                case "spines":
                    spines.setValue(!spines.getValue());
                    ChatLogger.print(String.format("ESP will %s display player spines.", (spines.getValue() ? "now" : "no longer")));
                    break;
                case "linewidth":
                case "width":
                    if (arguments.length == 3) {
                        try {
                            float newLineWidth = Float.parseFloat(arguments[2]);
                            lineWidth.setValue(newLineWidth);
                            ChatLogger.print(String.format("ESP Line Width set to %s", lineWidth.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a valid number", arguments[2]));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: esp linewidth <float>");
                    }
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
    
    public void orientCamera(float p_78467_1_)
    {
        Entity var2 = this.mc.func_175606_aa();
        float var3 = var2.getEyeHeight();
        double var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)p_78467_1_;
        double var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)p_78467_1_ + (double)var3;
        double var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)p_78467_1_;

        if (var2 instanceof EntityLivingBase && ((EntityLivingBase)var2).isPlayerSleeping())
        {
            var3 = (float)((double)var3 + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);

            if (!this.mc.gameSettings.debugCamEnable)
            {
                BlockPos var27 = new BlockPos(var2);
                IBlockState var11 = this.mc.theWorld.getBlockState(var27);
                Block var29 = var11.getBlock();

                if (Reflector.ForgeHooksClient_orientBedCamera.exists())
                {
                    Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, new Object[] {this.mc.theWorld, var27, var11, var2});
                }
                else if (var29 == Blocks.bed)
                {
                    int var30 = ((EnumFacing)var11.getValue(BlockBed.AGE)).getHorizontalIndex();
                    GlStateManager.rotate((float)(var30 * 90), 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * p_78467_1_, -1.0F, 0.0F, 0.0F);
            }
        }
        else if (this.mc.gameSettings.thirdPersonView > 0)
        {
            double var28 = (double)(4 + (4 - 4) * p_78467_1_);

            if (this.mc.gameSettings.debugCamEnable)
            {
                GlStateManager.translate(0.0F, 0.0F, (float)(-var28));
            }
            else
            {
                float var12 = var2.rotationYaw;
                float var13 = var2.rotationPitch;

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    var13 += 180.0F;
                }

                double var14 = (double)(-MathHelper.sin(var12 / 180.0F * (float)Math.PI) * MathHelper.cos(var13 / 180.0F * (float)Math.PI)) * var28;
                double var16 = (double)(MathHelper.cos(var12 / 180.0F * (float)Math.PI) * MathHelper.cos(var13 / 180.0F * (float)Math.PI)) * var28;
                double var18 = (double)(-MathHelper.sin(var13 / 180.0F * (float)Math.PI)) * var28;

                for (int var20 = 0; var20 < 8; ++var20)
                {
                    float var21 = (float)((var20 & 1) * 2 - 1);
                    float var22 = (float)((var20 >> 1 & 1) * 2 - 1);
                    float var23 = (float)((var20 >> 2 & 1) * 2 - 1);
                    var21 *= 0.1F;
                    var22 *= 0.1F;
                    var23 *= 0.1F;
                    MovingObjectPosition var24 = this.mc.theWorld.rayTraceBlocks(new Vec3(var4 + (double)var21, var6 + (double)var22, var8 + (double)var23), new Vec3(var4 - var14 + (double)var21 + (double)var23, var6 - var18 + (double)var22, var8 - var16 + (double)var23));

                    if (var24 != null)
                    {
                        double var25 = var24.hitVec.distanceTo(new Vec3(var4, var6, var8));

                        if (var25 < var28)
                        {
                            var28 = var25;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(var2.rotationPitch - var13, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(var2.rotationYaw - var12, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-var28));
                GlStateManager.rotate(var12 - var2.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(var13 - var2.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        }
        else
        {
            GlStateManager.translate(0.0F, 0.0F, -0.1F);
        }

        if (!this.mc.gameSettings.debugCamEnable)
        {
            GlStateManager.rotate(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * p_78467_1_, 1.0F, 0.0F, 0.0F);

            if (var2 instanceof EntityAnimal)
            {
                EntityAnimal var281 = (EntityAnimal)var2;
                GlStateManager.rotate(var281.prevRotationYawHead + (var281.rotationYawHead - var281.prevRotationYawHead) * p_78467_1_ + 180.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                GlStateManager.rotate(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, 1.0F, 0.0F);
            }
        }

        GlStateManager.translate(0.0F, -var3, 0.0F);
        var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * (double)p_78467_1_;
        var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * (double)p_78467_1_ + (double)var3;
        var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * (double)p_78467_1_;
    }
}
