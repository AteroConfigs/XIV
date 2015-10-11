package pw.latematt.xiv.event.events;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import pw.latematt.xiv.event.Cancellable;
import pw.latematt.xiv.event.Event;

/**
 * @author Matthew
 */
public class PlacingBlockEvent extends Event {
    private int placeDelay;

    public PlacingBlockEvent(int placeDelay) {
        this.placeDelay = placeDelay;
    }

    public int getPlaceDelay() {
        return placeDelay;
    }

    public void setPlaceDelay(int placeDelay) {
        this.placeDelay = placeDelay;
    }
}
