package pw.latematt.xiv.event.events;

import pw.latematt.xiv.event.Cancellable;
import pw.latematt.xiv.event.Event;

/**
 * @author Matthew
 */
public class ItemRenderEvent extends Event implements Cancellable {
    private boolean cancelled;
    private final State state;

    public ItemRenderEvent(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum State {
        PRE,
        POST
    }
}
