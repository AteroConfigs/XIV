package pw.latematt.xiv.mod.mods.world;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BreakingBlockEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.ClampedValue;

/**
 * @author Jack
 * @author Matthew
 */
public class Speedmine extends Mod implements Listener<BreakingBlockEvent>, CommandHandler {
    private final ClampedValue<Double> multiplier = new ClampedValue<>("speedmine_multiplier", 1.25D, 0.75D, 5.0D);
    private final ClampedValue<Integer> hitDelay = new ClampedValue<>("speedmine_hit_delay", 0, 0, 5);

    public Speedmine() {
        super("Speedmine", ModType.WORLD, Keyboard.KEY_G, 0xFF77A24E);

        Command.newCommand()
                .cmd("speedmine")
                .description("Base command for the Speedmine mod.")
                .arguments("<action>")
                .aliases("smine")
                .handler(this)
                .build();
    }

    @Override
    public void onEventCalled(BreakingBlockEvent event) {
        event.setMultiplier(multiplier.getValue());
        event.setHitDelay(hitDelay.getValue());
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "hitdelay":
                    if (arguments.length >= 3) {
                        String newHitDelayString = arguments[2];
                        try {
                            int newHitDelay = arguments[2].equalsIgnoreCase("-d") ? hitDelay.getDefault() : Integer.parseInt(newHitDelayString);
                            if (newHitDelay > 4) {
                                newHitDelay = 4;
                            } else if (newHitDelay < 0) {
                                newHitDelay = 0;
                            }
                            hitDelay.setValue(newHitDelay);
                            ChatLogger.print(String.format("Speedmine Hit Delay set to %s", hitDelay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newHitDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: speedmine hitdelay <number>");
                    }
                    break;
                case "multiplier":
                    if (arguments.length >= 3) {
                        String newMultiplierString = arguments[2];
                        try {
                            double newMultiplier = arguments[2].equalsIgnoreCase("-d") ? multiplier.getDefault() : Double.parseDouble(newMultiplierString);
                            if (newMultiplier > 10.0D) {
                                newMultiplier = 10.0D;
                            } else if (newMultiplier < 1.0D) {
                                newMultiplier = 1.0D;
                            }
                            multiplier.setValue(newMultiplier);
                            ChatLogger.print(String.format("Speedmine Multiplier set to %s", multiplier.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newMultiplierString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: speedmine multiplier <number>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: hitdelay, multiplier");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: speedmine <action>");
        }
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
