package pw.latematt.xiv.mod.mods;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.value.Value;

import java.util.List;

/**
 * Created by TehNeon on 9/24/2015.
 */
public class Tracers extends Mod implements Listener<Render3DEvent> {

    public Value<Boolean> spines = new Value<Boolean>("tracers_spines", false);
    public Value<Float> tracerWidth = new Value<Float>("tracers_width", 1F);
    public Value<Float> spineWidth = new Value<Float>("tracers_spine_width", 1F);

    public Tracers() {
        super("Tracers", Keyboard.KEY_I, 0xFFA718AD, new String[] {}, new String[] {});
    }

    public void onEventCalled(Render3DEvent event) {
        for (EntityPlayer player : (List<EntityPlayer>) mc.theWorld.playerEntities) {
            if(player == mc.thePlayer)
                continue;

            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            float partialTicks = event.getPartialTicks();
            mc.entityRenderer.orientCamera(partialTicks);
            double eposX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double eposY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double eposZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            double x = eposX - mc.getRenderManager().renderPosX;
            double y = eposY - mc.getRenderManager().renderPosY;
            double z = eposZ - mc.getRenderManager().renderPosZ;
            drawLines(player, x, y, z);
            GL11.glPopMatrix();
        }
    }

    public void drawLines(EntityLivingBase entity, double x, double y, double z) {
        GL11.glPushMatrix();

        RenderHelper.disableStandardItemLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        GL11.glLineWidth(tracerWidth.getValue());

        GL11.glColor3f(1, 1, 1);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(0, mc.thePlayer.getEyeHeight());
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();

        if(spines.getValue()) {
            GL11.glLineWidth(spineWidth.getValue());
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y + entity.height, z);
            GL11.glEnd();
        }

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void onCommandRan(String message) {

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
