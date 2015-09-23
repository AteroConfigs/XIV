package pw.latematt.xiv.management.managers;

import org.reflections.Reflections;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.mod.Mod;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author Matthew
 */
public class ModManager extends ListManager<Mod> {
    public ModManager() {
        super(new ArrayList<Mod>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + "...");
        Reflections reflections = new Reflections("pw.latematt.xiv.mod.mods");
        Set<Class<? extends Mod>> mods = reflections.getSubTypesOf(Mod.class);
        for (Class clazz : mods) {
            try {
                Mod mod = (Mod) clazz.newInstance();
                contents.add(mod);
                XIV.getInstance().getLogger().info("Mod \"" + mod.getName() + "\" successfully loaded.");
            } catch (InstantiationException e) {
                XIV.getInstance().getLogger().warn("Mod \"" + clazz.getSimpleName() + "\" failed to load, a stacktrace has been printed below: ");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                XIV.getInstance().getLogger().warn("Mod \"" + clazz.getSimpleName() + "\" failed to load, a stacktrace has been printed below: ");
                e.printStackTrace();
            }
        }
        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }
}
