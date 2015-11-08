package pw.latematt.xiv.event.events;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import pw.latematt.xiv.event.Cancellable;
import pw.latematt.xiv.event.Event;

/**
 * @author Matthew
 */
public class BlockReachEvent extends Event {
    private float range;

    public BlockReachEvent(float range) {
        this.range = range;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}
