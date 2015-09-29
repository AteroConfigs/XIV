package pw.latematt.xiv.mod.mods;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.NametagRenderEvent;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class Nametags extends Mod {
    private final Listener nametagRenderListener;
    private final Listener render3DListener;
    private Value<Boolean> health = new Value<>("nametags_health", true);

    public Nametags() {
        super("Nametags", ModType.RENDER);

        nametagRenderListener = new Listener<NametagRenderEvent>() {
            @Override
            public void onEventCalled(NametagRenderEvent event) {
                if (event.getEntity() instanceof EntityPlayer) {
                    event.setCancelled(true);
                }
            }
        };

        render3DListener = new Listener<Render3DEvent>() {
            @Override
            public void onEventCalled(Render3DEvent event) {
                for (EntityPlayer player : mc.theWorld.playerEntities) {
                    if (!isValidEntity(player))
                        continue;

                    float partialTicks = event.getPartialTicks();
                    double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - mc.getRenderManager().renderPosX;
                    double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - mc.getRenderManager().renderPosY;
                    double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - mc.getRenderManager().renderPosZ;

                    drawNametags(player, x, y, z);
                }
            }
        };
    }

    private boolean isValidEntity(Entity entity) {
        return entity != null && entity != mc.thePlayer && entity.isEntityAlive();
    }

    public void drawNametags(EntityLivingBase entity, double x, double y, double z) {
        String entityName = entity.getDisplayName().getFormattedText();
        if (XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            entityName = StringUtils.stripControlCodes(entityName);
            entityName = XIV.getInstance().getFriendManager().replace(entityName, false);
        }
        int health = MathHelper.floor_double(entity.getHealth() / 2);
        String healthColor;
        if (health > 6) {
            healthColor = "2";
        } else if (health > 3) {
            healthColor = "6";
        } else {
            healthColor = "4";
        }

        if (this.health.getValue()) {
            entityName = String.format("%s \247%s%s", entityName, healthColor, health);
        }

        float distance = mc.thePlayer.getDistanceToEntity(entity);
        float var13 = distance / 4 <= 2 ? 2.0F : distance / 4;
        float var14 = 0.016666668F * var13;
        GlStateManager.pushMatrix();
        mc.entityRenderer.func_175072_h();
        GlStateManager.translate((float) x + 0.0F, (float) y + entity.height + 0.5F, (float) z);
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
        Tessellator var15 = Tessellator.getInstance();
        WorldRenderer var16 = var15.getWorldRenderer();
        byte var17 = 0;
        if (entity.isSneaking()) {
            var17 += 4;
        }

        GlStateManager.func_179090_x();
        var16.startDrawingQuads();
        int var18 = mc.fontRendererObj.getStringWidth(entityName) / 2;
        var16.func_178960_a(0.0F, 0.0F, 0.0F, 0.25F);
        var16.addVertex((double)(-var18 - 2), (double)(-2 + var17), 0.0D);
        var16.addVertex((double)(-var18 - 2), (double)(9 + var17), 0.0D);
        var16.addVertex((double)(var18 + 2), (double)(9 + var17), 0.0D);
        var16.addVertex((double)(var18 + 2), (double)(-2 + var17), 0.0D);
        var15.draw();
        GlStateManager.func_179098_w();
        mc.fontRendererObj.drawStringWithShadow(entityName, -var18, var17, getNametagColor(entity));
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.entityRenderer.func_180436_i();
        GlStateManager.popMatrix();
    }

    public int getNametagColor(EntityLivingBase entity) {
        int color = 0xFFFFFFFF;
        if (entity instanceof EntityPlayer && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName())) {
            color = 0xFF4DB3FF;
        } else if (entity.isInvisibleToPlayer(mc.thePlayer)) {
            color = 0xFFFFE600;
        } else if (mc.thePlayer.getDistanceToEntity(entity) >= 64 || (mc.thePlayer.isSneaking() && !entity.canEntityBeSeen(mc.thePlayer))) {
            color = 0xFF00FF00;
        } else if (entity.isSneaking()) {
            color = 0xFFFF0000;
        }
        return color;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(nametagRenderListener);
        XIV.getInstance().getListenerManager().add(render3DListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(nametagRenderListener);
        XIV.getInstance().getListenerManager().remove(render3DListener);
    }
}
