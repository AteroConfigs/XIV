package pw.latematt.xiv.mod.mods;

import net.minecraft.client.multiplayer.WorldClient;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.LoadWorldEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class Fullbright extends Mod implements Listener<LoadWorldEvent>,CommandHandler {
    private final Value<Float> brightness = new Value<>("fullbright_brightness", 0.4F);

    public Fullbright() {
        super("Fullbright", ModType.RENDER, Keyboard.KEY_C, 0xFFFCFDCD);

        Command.newCommand()
                .cmd("fullbright")
                .description("Base command for the Fullbright mod.")
                .arguments("<action>")
                .aliases("bright", "b")
                .handler(this)
                .build();
    }

    public void editTable(WorldClient world, float value) {
        if (world == null)
            return;
        final float[] light = world.provider.lightBrightnessTable;
        for (int index = 0; index < light.length; index++) {
            if (light[index] > value) {
                continue;
            }
            light[index] = value;
        }
    }

    @Override
    public void onEventCalled(LoadWorldEvent event) {
        editTable(event.getWorld(), brightness.getValue());
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "brightness":
                    if (arguments.length >= 3) {
                        String newBrightnessString = arguments[2];
                        try {
                            float newBrightness = Float.parseFloat(newBrightnessString);
                            if (newBrightness > 1.0F) {
                                newBrightness = 1.0F;
                            } else if (newBrightness < 0.1F) {
                                newBrightness = 0.1F;
                            }
                            brightness.setValue(newBrightness);
                            editTable(mc.theWorld, brightness.getValue());
                            ChatLogger.print(String.format("Fullbright Brightness set to %s", brightness.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newBrightnessString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: fullbright brightness <number>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: brightness");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: fullbright <action>");
        }
    }

    @Override
    public void onEnabled() {
        if (mc.theWorld != null) {
            editTable(mc.theWorld, brightness.getValue());
        }
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        if (mc.theWorld != null) {
            for (int var2 = 0; var2 <= 15; ++var2) {
                float var3 = 1.0F - (float) var2 / 15.0F;
                mc.theWorld.provider.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F);
            }
        }
        XIV.getInstance().getListenerManager().remove(this);
    }
}
