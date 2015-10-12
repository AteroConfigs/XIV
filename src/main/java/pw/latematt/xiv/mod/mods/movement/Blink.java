package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Jack
 */

public final class Blink extends Mod {
    private final Listener<SendPacketEvent> packetListener;
    private final Listener<Render3DEvent> renderListener;
    private final List<Packet> packets = new ArrayList<>();
    private EntityOtherPlayerMP position;

    public Blink() {
        super("Blink", ModType.MOVEMENT, Keyboard.KEY_NONE, 11184895);

        this.packetListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    final boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
                    final boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                    final boolean moving = movingForward && strafing || movingForward;
                    event.setCancelled(true);
                    if (moving) {
                        packets.add(event.getPacket());
                    }
                }

                if (event.getPacket() instanceof C07PacketPlayerDigging || event.getPacket() instanceof C08PacketPlayerBlockPlacement || event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C0APacketAnimation) {
                    event.setCancelled(true);
                    packets.add(event.getPacket());
                }
            }
        };

        this.renderListener = new Listener<Render3DEvent>() {
            @Override
            public void onEventCalled(Render3DEvent event) {
                double[] start = new double[] { position.posX, position.posY, position.posZ };

                RenderUtils.beginGl();
                Tessellator var2 = Tessellator.getInstance();
                WorldRenderer var3 = var2.getWorldRenderer();
                var3.startDrawing(2);
                GlStateManager.color(0.3F, 0.7F, 1.0F, 1.0F);
                var3.addVertex(start[0] - mc.getRenderManager().renderPosX, start[1] - mc.getRenderManager().renderPosY, start[2] - mc.getRenderManager().renderPosZ);
                var3.addVertex(start[0] - mc.getRenderManager().renderPosX, start[1] + mc.thePlayer.height - mc.getRenderManager().renderPosY, start[2] - mc.getRenderManager().renderPosZ);
                var2.draw();
                RenderUtils.endGl();
            }
        };
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this.packetListener);
        XIV.getInstance().getListenerManager().add(this.renderListener);

        mc.timer.timerSpeed = 1.25F;

        if (Objects.nonNull(mc.thePlayer)) {
            this.position = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            this.position.copyLocationAndAnglesFrom(mc.thePlayer);

            EntityUtils.setReference(this.position);
        }
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this.packetListener);
        XIV.getInstance().getListenerManager().remove(this.renderListener);

        if (Objects.nonNull(mc.thePlayer)) {
            EntityUtils.setReference(mc.thePlayer);

            for (final Packet packet : this.packets) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(packet);
            }
        }

        mc.timer.timerSpeed = 1.0F;
        this.packets.clear();
    }
}
