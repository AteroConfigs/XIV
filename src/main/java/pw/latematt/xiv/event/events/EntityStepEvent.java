package pw.latematt.xiv.event.events;

import net.minecraft.entity.Entity;
import pw.latematt.xiv.event.Event;

/**
 * @author Matthew
 */
public class EntityStepEvent extends Event {
    private final Entity entity;

    public EntityStepEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
