package pw.latematt.xiv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw.latematt.xiv.management.managers.*;

/**
 * @author Matthew
 */
public class XIV {
    /* static instance of main class */
    private static XIV instance = new XIV();

    public static XIV getInstance() {
        return instance;
    }

    /* Management */
    private ModManager modManager = new ModManager();
    private CommandManager commandManager = new CommandManager();
    private ListenerManager listenerManager = new ListenerManager();
    private FileManager fileManager = new FileManager();
    private FriendManager friendManager = new FriendManager();
    private ValueManager valueManager = new ValueManager();

    public ModManager getModManager() {
        return modManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public ValueManager getValueManager() {
        return valueManager;
    }

    /* logger */
    private Logger logger = LogManager.getLogger("XIV");

    public Logger getLogger() {
        return logger;
    }

    /* sets up the client base (called in Main#main(p_main_0_) at line 114) */
    public void setup() {
        logger.info("== Begin XIV setup == ");
        /* call setup on all managers */
        /* the order that these are called in is important, do not change! */
        /* setup is not required with all managers */
        commandManager.setup();
        listenerManager.setup();
        modManager.setup();
        friendManager.setup();
        fileManager.setup();

        logger.info("==  End XIV setup  == ");
    }

    public ScaledResolution newScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }
}
