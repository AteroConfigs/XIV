package pw.latematt.xiv.mod.mods;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.MoveEvent;
import pw.latematt.xiv.event.events.PushOutOfBlocksEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.Timer;

import java.util.Objects;

/**
 * @author Jack
 * @author Matthew
 */
public class Freecam extends Mod {
    private Timer timer = new Timer();
    private final Listener packetListener;
    private final Listener motionListener;
    private final Listener moveListener;
    private final Listener pushOutOfBlocksListener;
    private EntityOtherPlayerMP entity;

    private boolean sneaking = false;

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

                /**
                 *  Update entity state
                 */

                clonePlayer(entity);
            }
        };

        this.packetListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C0BPacketEntityAction) {
                    event.setCancelled(true);
                }

                if (event.getPacket() instanceof C03PacketPlayer && entity != null) {
                    C03PacketPlayer packetPlayer = (C03PacketPlayer) event.getPacket();
                    packetPlayer.setX(entity.posX);
                    packetPlayer.setY(entity.posY);
                    packetPlayer.setZ(entity.posZ);
                    packetPlayer.setYaw(entity.rotationYaw);
                    packetPlayer.setPitch(entity.rotationPitch);
                }
            }
        };

        this.pushOutOfBlocksListener = new Listener<PushOutOfBlocksEvent>() {
            @Override
            public void onEventCalled(PushOutOfBlocksEvent event) {
                event.setCancelled(true);
            }
        };
    }

    @Override
    public void onEnabled() {
        if (Objects.nonNull(mc.thePlayer)) {
            entity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            entity.copyLocationAndAnglesFrom(mc.thePlayer);
            entity.rotationYawHead = mc.thePlayer.rotationYawHead;

            clonePlayer(entity);

            mc.theWorld.addEntityToWorld(-1, entity);
            mc.renderGlobal.loadRenderers();

            sneaking = mc.thePlayer.isSneaking() || XIV.getInstance().getModManager().find(Sneak.class).isEnabled();
        }

        XIV.getInstance().getListenerManager().add(this.packetListener);
        XIV.getInstance().getListenerManager().add(this.motionListener);
        XIV.getInstance().getListenerManager().add(this.moveListener);
        XIV.getInstance().getListenerManager().add(this.pushOutOfBlocksListener);


        // If you don't want the tracers to go to the freecam body, you can just not do this. Maybe a mode in the future?
        EntityUtils.setReference(entity);
    }

    @Override
    public void onDisabled() {
        EntityUtils.setReference(mc.thePlayer);

        XIV.getInstance().getListenerManager().remove(this.packetListener);
        XIV.getInstance().getListenerManager().remove(this.motionListener);
        XIV.getInstance().getListenerManager().remove(this.moveListener);
        XIV.getInstance().getListenerManager().remove(this.pushOutOfBlocksListener);

        if (Objects.nonNull(mc.thePlayer) && Objects.nonNull(entity)) {
            mc.thePlayer.noClip = false;
            mc.thePlayer.copyLocationAndAnglesFrom(entity);
            mc.theWorld.removeEntityFromWorld(-1);
            entity = null;
            mc.renderGlobal.loadRenderers();

            if (!mc.thePlayer.isSneaking()) {
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
        }

        this.sneaking = false;
    }

    public void clonePlayer(EntityOtherPlayerMP entity) {
        if(entity != null) {
            entity.setSneaking(sneaking);
            entity.swingProgress = mc.thePlayer.swingProgress;
            entity.swingProgressInt = mc.thePlayer.swingProgressInt;
            entity.isSwingInProgress = mc.thePlayer.isSwingInProgress;
            entity.setEating(mc.thePlayer.isEating());
            entity.setInvisible(mc.thePlayer.isInvisible());
            entity.setHealth(mc.thePlayer.getHealth());
            entity.setAbsorptionAmount(mc.thePlayer.getAbsorptionAmount());
            entity.clonePlayer(mc.thePlayer, true);
        }
    }
}
