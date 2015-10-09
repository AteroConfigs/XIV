package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.macro.Macro;
import pw.latematt.xiv.management.ListManager;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class MacroManager extends ListManager<Macro> {
    public MacroManager() {
        super(new ArrayList<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + ".");

        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }
}
