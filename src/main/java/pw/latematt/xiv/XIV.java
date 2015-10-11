package pw.latematt.xiv;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw.latematt.xiv.macro.Macro;
import pw.latematt.xiv.management.managers.*;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.utils.RenderUtils;

/**
 * @author Matthew
 */
public class XIV {
    /* static instance of main class */
    private static final XIV instance = new XIV();

    public static XIV getInstance() {
        return instance;
    }

    /* Management */
    private final FileManager fileManager = new FileManager();
    private final ModManager modManager = new ModManager();
    private final AltManager altManager = new AltManager();
    private final CommandManager commandManager = new CommandManager();
    private final ListenerManager listenerManager = new ListenerManager();
    private final FriendManager friendManager = new FriendManager();
    private final ValueManager valueManager = new ValueManager();
    private final ConfigManager configManager = new ConfigManager();
    private final MacroManager macroManager = new MacroManager();
    private final GuiClick guiClick = new GuiClick();

    public FileManager getFileManager() {
        return fileManager;
    }

    public ModManager getModManager() {
        return modManager;
    }

    public AltManager getAltManager() {
        return altManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public ValueManager getValueManager() {
        return valueManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MacroManager getMacroManager() {
        return macroManager;
    }

    public GuiClick getGuiClick() {
        return guiClick;
    }

    /* logger */
    private final Logger logger = LogManager.getLogger("XIV");

    public Logger getLogger() {
        return logger;
    }

    /* sets up the client base (called in Main#main(p_main_0_) at line 114) */
    public void setup() {
        logger.info("== Begin XIV setup == ");
        /* call setup on all managers */
        /* the order that these are called in is important, do not change! */
        /* setup is not required with all managers */

        RenderUtils.init(); // Prevents crashing with values that aren't initialized.

        fileManager.setup();
        commandManager.setup();
        listenerManager.setup();
        modManager.setup();
        friendManager.setup();
        altManager.setup();
        configManager.setup();

        /* load/save files on startup */
        fileManager.loadAllFiles();
        fileManager.saveAllFiles();

        logger.info("==  End XIV setup  == ");
    }

    public ScaledResolution newScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    }
}
