package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Event;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.management.ListManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Matthew
 *         <p>
 *         rudy sucks
 */
public class ListenerManager extends ListManager<Listener> {
    public ListenerManager() {
        super(new ArrayList<Listener>());
    }

    public void add(Listener listener) {
        if (contents.contains(listener)) {
            XIV.getInstance().getLogger().warn("Duplicate listener entries were attempted to be added");
            return;
        }
        contents.add(listener);
    }

    public void remove(Listener listener) {
        if (!contents.contains(listener)) {
            XIV.getInstance().getLogger().warn("A listener that didn't exist was attempted to be removed");
            return;
        }
        contents.remove(listener);
    }

    @SuppressWarnings("unchecked") // rudy sucks really bad
    public void call(Event event) {
        for (Listener listener : contents) {
            /* thanks rudy for this method */
            Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                    for (Type genericType : genericTypes) {
                        if (genericType == event.getClass()) {
                            listener.onEventCalled(event);
                        }
                    }
                }
            }
        }
    }
}
