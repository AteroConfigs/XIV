package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.value.Value;

/**
 * @author TehNeon
 */
public class Fly extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {

    private final Value<Boolean> doDamage = new Value<>("fly_damage", true);

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
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "damage":
                    if (arguments.length >= 3) {
                        doDamage.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        doDamage.setValue(!doDamage.getValue());
                    }
                    ChatLogger.print(String.format("Fly will %s take damage on enable.", doDamage.getValue() ? "now" : "no longer"));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: damage");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: fly <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        EntityUtils.damagePlayer();
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
