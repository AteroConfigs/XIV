package pw.latematt.xiv.event.events;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import pw.latematt.xiv.event.Event;

/**
 * @author Matthew
 */
public class BlockAddBBEvent extends Event {
    private final Block block;
    private final BlockPos pos;
    private AxisAlignedBB axisAlignedBB;

    public BlockAddBBEvent(Block block, BlockPos pos, AxisAlignedBB axisAlignedBB) {
        this.block = block;
        this.pos = pos;
        this.axisAlignedBB = axisAlignedBB;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getPos() {
        return pos;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }
}
