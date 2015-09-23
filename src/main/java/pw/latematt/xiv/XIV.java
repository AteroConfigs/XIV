package pw.latematt.xiv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw.latematt.xiv.management.managers.CommandManager;
import pw.latematt.xiv.management.managers.FileManager;
import pw.latematt.xiv.management.managers.ListenerManager;
import pw.latematt.xiv.management.managers.ModManager;

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

    /* logger */
    private Logger logger = LogManager.getLogger("XIV");

    public Logger getLogger() {
        return logger;
    }

    /* sets up the client base (called in XIVMain) */
    public void setup() {
        logger.info("== Begin XIV setup == ");
        /* call setup on all managers */
        /* the order that these are called in is important, do not change! */
        /* setup is not required with all managers */
        commandManager.setup();
        modManager.setup();

        /* file loading */
        fileManager.setup();
        fileManager.loadAllFiles();

        /* save files on shutdown */
        Runtime.getRuntime().addShutdownHook(new Thread("XIV Shutdown Thread") {
            public void run() {
                fileManager.saveAllFiles();
            }
        });

        logger.info("==  End XIV setup  == ");
    }
}
