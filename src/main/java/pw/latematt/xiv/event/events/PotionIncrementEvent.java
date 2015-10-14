package pw.latematt.xiv.event.events;

import net.minecraft.entity.EntityLivingBase;
import pw.latematt.xiv.event.Cancellable;
import pw.latematt.xiv.event.Event;

/**
 * @author Matthew
 */
public class PotionIncrementEvent extends Event implements Cancellable {
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
