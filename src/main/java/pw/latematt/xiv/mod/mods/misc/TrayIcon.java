package pw.latematt.xiv.mod.mods.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import org.lwjgl.input.Keyboard;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.XIVTray;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.ClampedValue;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Matthew
 */
public class TrayIcon extends Mod  {

    private XIVTray tray;

    public TrayIcon() {
        super("TrayIcon", ModType.MISCELLANEOUS, Keyboard.KEY_NONE);
    }

    @Override
    public void onEnabled() {
        if(tray == null) {
            this.tray = new XIVTray();
        }

        try {
            tray.load();
        }catch(AWTException e) { }
    }

    @Override
    public void onDisabled() {
        if(tray == null) {
            this.tray = new XIVTray();
        }

        tray.unload();
    }
}
