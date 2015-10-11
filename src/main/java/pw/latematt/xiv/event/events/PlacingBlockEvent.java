package pw.latematt.xiv.event.events;

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
