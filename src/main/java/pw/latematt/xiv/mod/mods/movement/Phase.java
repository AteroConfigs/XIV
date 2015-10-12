package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BlockAddBBEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.PushOutOfBlocksEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;

/**
 * @author Rederpz
 */
public class Phase extends Mod implements Listener<MotionUpdateEvent> {
    private final Listener blockAddBBListener;
    private final Listener pushOutOfBlocksListener;
    private boolean collided = false;

    public Phase() {
        super("Phase", ModType.MOVEMENT, Keyboard.KEY_LBRACKET, 0xFF66FF99);

        blockAddBBListener = new Listener<BlockAddBBEvent>() {
            @Override
            public void onEventCalled(BlockAddBBEvent event) {
                if (mc.thePlayer.getEntityBoundingBox().minY - 0.5F < event.getPos().getY() && BlockUtils.isInsideBlock(mc.thePlayer)) {
                    event.setAxisAlignedBB(null);
                }

                if (mc.thePlayer.getEntityBoundingBox().maxY < event.getPos().getY() && !BlockUtils.isInsideBlock(mc.thePlayer)) {
                    event.setAxisAlignedBB(null);
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

    public void onEventCalled(MotionUpdateEvent event) {
        if (!mc.thePlayer.isCollidedHorizontally && collided) {
            collided = false;
        }

        boolean moving = mc.gameSettings.keyBindForward.getIsKeyPressed() || mc.gameSettings.keyBindBack.getIsKeyPressed() || mc.gameSettings.keyBindLeft.getIsKeyPressed() || mc.gameSettings.keyBindRight.getIsKeyPressed();

        if (mc.thePlayer.isCollidedHorizontally && !collided && mc.thePlayer.onGround && !BlockUtils.isInsideBlock(mc.thePlayer) && moving) {
            if (mc.thePlayer.isCollidedHorizontally) {
                collided = true;
            }

            float dir = mc.thePlayer.rotationYaw;

            if (mc.thePlayer.moveForward < 0.0F) {
                dir += 180.0F;
            }

            if (mc.thePlayer.moveStrafing > 0.0F) {
                dir -= 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);
            }

            if (mc.thePlayer.moveStrafing < 0.0F) {
                dir += 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);
            }

            float xD = (float) Math.cos((dir + 90.0F) * Math.PI / 180.0D);
            float zD = (float) Math.sin((dir + 90.0F) * Math.PI / 180.0D);

            float[] offset = new float[]{xD * 0.25F, 1.0F, zD * 0.25F};

            /**
             *  TOO MUCH MEME 6 YOU HAHA XBOYS CODE IN XIV TELL MOM!!!
             */

            double[] topkek = {
                    -0.025000000372529D,
                    -0.02857142899717604D,
                    -0.0333333338300387D,
                    -0.04000000059604645D};

            for (int i = 0; i < topkek.length; i++) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (topkek[i] * offset[1]) + 0.025F, mc.thePlayer.posZ, mc.thePlayer.onGround));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + offset[0] * i, mc.thePlayer.posY, mc.thePlayer.posZ + offset[2] * i, mc.thePlayer.onGround));
            }

            mc.thePlayer.setPosition(mc.thePlayer.posX + (offset[0] * 0.05F), mc.thePlayer.posY, mc.thePlayer.posZ + (offset[2] * 0.05F));
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(blockAddBBListener);
        XIV.getInstance().getListenerManager().add(pushOutOfBlocksListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(blockAddBBListener);
        XIV.getInstance().getListenerManager().remove(pushOutOfBlocksListener);

        this.collided = false;
    }
}