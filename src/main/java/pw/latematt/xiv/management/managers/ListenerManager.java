package pw.latematt.xiv.management.managers;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Event;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.KeyPressEvent;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.mod.Mod;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Matthew
 */
public class ListenerManager extends ListManager<Listener> {
    public ListenerManager() {
        super(new CopyOnWriteArrayList<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + "...");
        add(new Listener<KeyPressEvent>() {
            @Override
            public void onEventCalled(KeyPressEvent event) {
                for (Mod mod : XIV.getInstance().getModManager().getContents()) {
                    if (mod.getKeybind() == Keyboard.KEY_NONE) continue;

                    if (mod.getKeybind() == event.getKeyCode()) {
                        mod.toggle();
                    }
                }
            }
        });
        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public void add(Listener listener) {
        if (contents.contains(listener)) {
            XIV.getInstance().getLogger().warn(String.format("%s was attempted to be added when it's already in the list.", listener.getClass().getSimpleName()));
            return;
        }
        contents.add(listener);
    }

    public void remove(Listener listener) {
        if (!contents.contains(listener)) {
            XIV.getInstance().getLogger().warn(String.format("%s was attempted to be removed when it's not in the list.", listener.getClass().getSimpleName()));
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
