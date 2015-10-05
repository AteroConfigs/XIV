package pw.latematt.xiv.mod.mods;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BlockAddBBEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;

/**
 * @author Matthew
 */
public class Jesus extends Mod {
    private final Listener blockAddBBListener;
    private final Listener motionUpdatesListener;
    private final Listener sendPacketListener;
    private boolean nextTick;

    public Jesus() {
        super("Jesus", ModType.MOVEMENT, Keyboard.KEY_J, 0xFF56BFE3);

        blockAddBBListener = new Listener<BlockAddBBEvent>() {
            @Override
            public void onEventCalled(BlockAddBBEvent event) {
                if (event.getBlock() instanceof BlockLiquid) {
                    if (mc.thePlayer == null)
                        return;
                    BlockPos pos = event.getPos();
                    if (event.getBlock() instanceof BlockLiquid && !BlockUtils.isInLiquid(mc.thePlayer) && mc.thePlayer.fallDistance < 3.0F && !mc.thePlayer.isSneaking() && event.getEntity() == mc.thePlayer) {
                        event.setAxisAlignedBB(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
                    }
                }
            }
        };

        motionUpdatesListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (BlockUtils.isInLiquid(mc.thePlayer) && mc.thePlayer.isInsideOfMaterial(Material.air) && !mc.thePlayer.isSneaking()) {
                    mc.thePlayer.motionY = 0.08;
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (BlockUtils.isOnLiquid(mc.thePlayer)) {
                        nextTick = !nextTick;
                        if (nextTick) {
                            player.y -= 0.01;
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(blockAddBBListener);
        XIV.getInstance().getListenerManager().add(motionUpdatesListener);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(blockAddBBListener);
        XIV.getInstance().getListenerManager().remove(motionUpdatesListener);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
    }
}
