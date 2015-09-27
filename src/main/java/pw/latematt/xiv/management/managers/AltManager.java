package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.MapManager;

import java.util.HashMap;

/**
 * @author Rederpz
 */
public class AltManager extends MapManager<String, String> {
    public AltManager() {
        super(new HashMap<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + ".");

        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public void add(String username, String password) {
        contents.put(username, password);
    }

    public void remove(String username) {
        contents.remove(username);
    }
}
