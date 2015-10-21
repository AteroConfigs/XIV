package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
    private final Listener blockAddBBListener, motionUpdatesListener, sendPacketListener;
    private boolean nextTick, shouldJump;

    public Jesus() {
        super("Jesus", ModType.MOVEMENT, Keyboard.KEY_J, 0xFF56BFE3);

        blockAddBBListener = new Listener<BlockAddBBEvent>() {
            @Override
            public void onEventCalled(BlockAddBBEvent event) {
                if (event.getBlock() instanceof BlockLiquid && event.getBlock() != null && mc.theWorld != null && mc.thePlayer != null) {
                    IBlockState state = mc.theWorld.getBlockState(event.getPos());

                    if (state != null) {
                        float blockHeight = BlockLiquid.getLiquidHeightPercent(event.getBlock().getMetaFromState(state));

                        shouldJump = blockHeight < 0.55F;
                    } else {
                        shouldJump = false;
                    }

                    if (shouldJump && event.getEntity() == mc.thePlayer && !BlockUtils.isInLiquid(mc.thePlayer) && mc.thePlayer.fallDistance <= 3.0F && !mc.thePlayer.isSneaking()) {
                        BlockPos pos = event.getPos();
                        event.setAxisAlignedBB(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
                    }
                }
            }
        };

        motionUpdatesListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (shouldJump && BlockUtils.isInLiquid(mc.thePlayer) && mc.thePlayer.isInsideOfMaterial(Material.air) && !mc.thePlayer.isSneaking()) {
                    mc.thePlayer.motionY = 0.08;
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (BlockUtils.isOnLiquid(mc.thePlayer) && shouldJump) {
                        nextTick = !nextTick;
                        if (nextTick) {
                            player.setY(player.getY() - 0.01);
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
        nextTick = false;
    }
}
