package pw.latematt.xiv.mod.mods.movement;

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
import pw.latematt.xiv.utils.RenderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jack
 */

public final class Blink extends Mod {
    private final Listener<SendPacketEvent> packetListener;
    private final Listener<Render3DEvent> renderListener;
    private final List<Packet> packets = new ArrayList<>();
    private double[] start;

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
                RenderUtils.beginGl(); // TODO: Color?
                Tessellator var2 = Tessellator.getInstance();
                WorldRenderer var3 = var2.getWorldRenderer();
                var3.startDrawing(2);
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
        this.start = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this.packetListener);
        XIV.getInstance().getListenerManager().remove(this.renderListener);

        for (final Packet packet : this.packets) {
            mc.thePlayer.sendQueue.getNetworkManager().sendPacket(packet);
        }

        mc.timer.timerSpeed = 1.0F;
        this.packets.clear();
    }
}
