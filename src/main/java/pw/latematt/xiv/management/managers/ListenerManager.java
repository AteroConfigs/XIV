package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.event.Event;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.management.ListManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Matthew
 */
public class ListenerManager extends ListManager<Listener> {
    private boolean enabled = true;

    public ListenerManager() {
        super(new CopyOnWriteArrayList<>());
    }

    public void add(Listener listener) {
        if (contents.contains(listener)) {
            return;
        }
        contents.add(listener);
    }

    public void remove(Listener listener) {
        if (!contents.contains(listener)) {
            return;
        }
        contents.remove(listener);
    }

    @SuppressWarnings("unchecked") // rudy sucks really bad
    public void call(Event event) {
        if(isEnabled()) {
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
