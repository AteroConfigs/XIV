package pw.latematt.xiv.mod.mods.render;

import com.sun.javafx.geom.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.player.FastUse;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rederpz
 * @author WhoEverDidTheMathOriginally
 *
 * @implNote pls glstatemanager thx
 */
public class Trajectories extends Mod implements Listener<Render3DEvent> {

    public Trajectories() {
        super("Trajectories", ModType.RENDER);
    }

    @Override
    public void onEventCalled(Render3DEvent event) {
        double renderPosX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.getPartialTicks();
        double renderPosY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.getPartialTicks();
        double renderPosZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.getPartialTicks();

        ItemStack stack = null;
        Item item = null;

        if(mc.thePlayer.getHeldItem() != null && mc.gameSettings.thirdPersonView == 0) {
            if(!isValidPotion(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getItem()) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSnowball) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl) && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEgg)) {
                return;
            }
            stack = mc.thePlayer.getHeldItem();
            item = mc.thePlayer.getHeldItem().getItem();
        }else{
            return;
        }
        
        RenderUtils.beginGl();

        double posX = renderPosX - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        double posY = renderPosY + mc.thePlayer.getEyeHeight() - 0.1000000014901161D;
        double posZ = renderPosZ - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;

        double motionX = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI) * (item instanceof ItemBow ? 1.0D : 0.4D);
        double motionY = -MathHelper.sin(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI) * (item instanceof ItemBow ? 1.0D : 0.4D);
        double motionZ = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI) * (item instanceof ItemBow ? 1.0D : 0.4D);

        int var6 = 72000 - mc.thePlayer.getItemInUseCount();
        float power = var6 / 20.0F;
        power = (power * power + power * 2.0F) / 3.0F;

        if(XIV.getInstance().getModManager().find(FastUse.class) != null && XIV.getInstance().getModManager().find(FastUse.class).isEnabled()) {
            if(power > 0.3F) {
                power = 1.0F;
            }
        }

        if(power < 0.1D) {
            return;
        }

        if(power > 1.0F) {
            power = 1.0F;
        }

        float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);

        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;

        float pow = (item instanceof ItemBow ? power * 2.0F : isValidPotion(stack, item) ? 0.325F : item instanceof ItemFishingRod ? 1.25F : 1.0F);

        motionX *= pow * (item instanceof ItemFishingRod ? 0.75F : 1.5F);
        motionY *= pow * (item instanceof ItemFishingRod ? 0.75F : 1.5F);
        motionZ *= pow * (item instanceof ItemFishingRod ? 0.75F : 1.5F);

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 0.0F);

        GL11.glDisable(2896);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 0.3F, 0.0F, 1.0F);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0F);
        GL11.glBegin(3);

        float size = (float)(item instanceof ItemBow ? 0.3D : 0.25D);
        boolean hasLanded = false;
        boolean isEntity = false;
        Entity hitEntity = null;
        MovingObjectPosition landingPosition = null;
        while(!hasLanded && posY > 0.0D) {
            Vec3 present = new Vec3(posX, posY, posZ);
            Vec3 future = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);

            MovingObjectPosition possibleLandingStrip = mc.theWorld.rayTraceBlocks(present, future, false, true, false);
            if(possibleLandingStrip != null && possibleLandingStrip.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
                landingPosition = possibleLandingStrip;
                hasLanded = true;
            }

            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
            List entities = getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            for(int motionAdjustment = 0; motionAdjustment < entities.size(); motionAdjustment++) {
                Entity boundingBox = (Entity)entities.get(motionAdjustment);
                if((boundingBox.canBeCollidedWith()) && (boundingBox != mc.thePlayer)) {
                    float var11 = 0.3F;
                    AxisAlignedBB var12 = boundingBox.getEntityBoundingBox().expand(var11, var11, var11);
                    MovingObjectPosition possibleEntityLanding = var12.calculateIntercept(present, future);
                    if(possibleEntityLanding != null) {
                        hasLanded = true;
                        isEntity = true;
                        landingPosition = possibleEntityLanding;
                    }
                }
            }

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float motionAdjustment = 0.99F;
            motionX *= motionAdjustment;
            motionY *= motionAdjustment;
            motionZ *= motionAdjustment;
            motionY -= (item instanceof ItemBow ? 0.05D : 0.03D);
            GL11.glVertex3d(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
        }

        GL11.glEnd();

        GL11.glPushMatrix();
        GL11.glTranslated(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);

        if(landingPosition != null && landingPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int side = landingPosition.field_178784_b.getIndex();

            if(side == 2) {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }else if(side == 3) {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }else if(side == 4) {
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            }else if(side == 5) {
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if(isEntity) {
                GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
            }else{
                GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            }

            Cylinder c = new Cylinder();
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            c.setDrawStyle(GLU.GLU_SILHOUETTE);
            c.draw(0.6F, 0.3F, 0.0F, 3, 1);
        }

        GL11.glPopMatrix();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glEnable(3553);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glPopMatrix();

        RenderUtils.endGl();
    }

    private boolean isValidPotion(ItemStack stack, Item item) {
        if(item != null && item instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) item;

            if(potion.getEffects(stack) != null) {
                for(Object o: potion.getEffects(stack)) {
                    PotionEffect effect = (PotionEffect) o;
                    Potion pot = Potion.potionTypes[effect.getPotionID()];

                    return true;
                }
            }
        }
        return false;
    }

    private List getEntitiesWithinAABB(AxisAlignedBB bb) {
        ArrayList list = new ArrayList();
        int chunkMinX = MathHelper.floor_double((bb.minX - 2.0D) / 16.0D);
        int chunkMaxX = MathHelper.floor_double((bb.maxX + 2.0D) / 16.0D);
        int chunkMinZ = MathHelper.floor_double((bb.minZ - 2.0D) / 16.0D);
        int chunkMaxZ = MathHelper.floor_double((bb.maxZ + 2.0D) / 16.0D);
        for (int x = chunkMinX; x <= chunkMaxX; x++) {
            for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                if (mc.theWorld.getChunkProvider().chunkExists(x, z)) {
                    mc.theWorld.getChunkFromChunkCoords(x, z).func_177414_a(mc.thePlayer, bb, list, null);
                }
            }
        }
        return list;
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
