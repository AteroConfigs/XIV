package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author TehNeon
 */
public class Fly extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    public Fly() {
        super("Fly", ModType.PLAYER, Keyboard.KEY_M, 0xFF4B97F6);

        Command.newCommand()
                .cmd("fly")
                .aliases("flight")
                .description("Base command for the Fly mod.")
                .arguments("<action>")
                .handler(this).build();
    }

    public void onEventCalled(MotionUpdateEvent event) {
        double motionY = 0;

        if (mc.gameSettings.keyBindJump.getIsKeyPressed()) {
            motionY = 0.5;
        }

        if (mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
            motionY = -0.5;
        }

        mc.thePlayer.motionY = motionY;
    }

    @Override
    public void onCommandRan(String message) {

    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
