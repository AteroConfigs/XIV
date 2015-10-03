package pw.latematt.xiv.mod.mods;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.MoveEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.Timer;

import java.util.Objects;

/**
 * @author Jack
 */

public class Freecam extends Mod {
    private Timer timer = new Timer();
    private double x, y, z;
    private float yaw, pitch;
    private Listener packetListener;
    private Listener motionListener;
    private Listener moveListener;

    public Freecam() {
        super("Freecam", ModType.RENDER, Keyboard.KEY_V);

        this.motionListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                mc.thePlayer.renderArmPitch += 400.0F;

                if (timer.hasReached(7000L)) {
                    mc.renderGlobal.loadRenderers();
                    timer.reset();
                }
            }
        };

        this.moveListener = new Listener<MoveEvent>() {
            @Override
            public void onEventCalled(MoveEvent event) {
                mc.thePlayer.noClip = true;
                double speed = 4.0D;

                if (mc.gameSettings.ofKeyBindZoom.getIsKeyPressed()) {
                    speed /= 2.0D;
                }

                event.setMotionY(0.0D);
                mc.thePlayer.motionY = 0.0D;

                if (!mc.inGameHasFocus) {
                    return;
                }

                event.setMotionX(event.getMotionX() * speed);
                event.setMotionZ(event.getMotionZ() * speed);

                if (mc.thePlayer.movementInput.jump) {
                    event.setMotionY(speed / 4.0D);
                } else if (mc.thePlayer.movementInput.sneak) {
                    event.setMotionY(-(speed / 4.0D));
                }
            }
        };

        this.packetListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C0BPacketEntityAction) {
                    event.setCancelled(true);
                }

                if (event.getPacket() instanceof C03PacketPlayer) {
                    final C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
                    packetPlayer.x = x;
                    packetPlayer.y = y;
                    packetPlayer.z = z;
                    packetPlayer.yaw = yaw;
                    packetPlayer.pitch = pitch;
                    event.setCancelled(true);
                }
            }
        };
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this.packetListener);
        XIV.getInstance().getListenerManager().add(this.motionListener);
        XIV.getInstance().getListenerManager().add(this.moveListener);

        if (Objects.nonNull(mc.thePlayer)) {
            this.x = mc.thePlayer.posX;
            this.y = mc.thePlayer.posY;
            this.z = mc.thePlayer.posZ;
            this.yaw = mc.thePlayer.rotationYaw;
            this.pitch = mc.thePlayer.rotationPitch;
            final EntityOtherPlayerMP entity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            entity.setPositionAndRotation(this.x, this.y, this.z, this.yaw, this.pitch);
            entity.inventory = mc.thePlayer.inventory;
            entity.inventoryContainer = mc.thePlayer.inventoryContainer;
            entity.rotationYawHead = mc.thePlayer.rotationYawHead;
            mc.theWorld.addEntityToWorld(-1, entity);
            mc.renderGlobal.loadRenderers();
        }
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this.packetListener);
        XIV.getInstance().getListenerManager().remove(this.motionListener);
        XIV.getInstance().getListenerManager().remove(this.moveListener);

        if (Objects.nonNull(mc.thePlayer)) {
            mc.thePlayer.noClip = false;
            mc.theWorld.removeEntityFromWorld(-1);
            mc.thePlayer.setPositionAndRotation(this.x, this.y, this.z, this.yaw, this.pitch);
            mc.renderGlobal.loadRenderers();
        }
    }
}
