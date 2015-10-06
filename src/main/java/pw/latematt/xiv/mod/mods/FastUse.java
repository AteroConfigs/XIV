package pw.latematt.xiv.mod.mods;

import net.minecraft.item.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.SliderValue;
import pw.latematt.xiv.value.Value;

import java.text.DecimalFormat;

/**
 * @author Matthew
 */
public class FastUse extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private final Value<Boolean> bow = new Value<>("fastuse_bow", false);
    private final Value<Boolean> food = new Value<>("fastuse_food", true);
    private final Value<Boolean> milk = new Value<>("fastuse_milk", true);
    private final Value<Boolean> potions = new Value<>("fastuse_potions", true);
    private final SliderValue<Integer> ticksToWait = new SliderValue<>("fastuse_ticks_to_wait", 16, 0, 31, new DecimalFormat("0"));

    public FastUse() {
        super("FastUse", ModType.PLAYER, Keyboard.KEY_NONE, 0xFFEF60A9);

        Command.newCommand()
                .cmd("fastuse")
                .description("Base command for FastUse mod.")
                .arguments("<action>")
                .aliases("fuse", "fu")
                .handler(this)
                .build();
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            if (isUsable(mc.thePlayer.getCurrentEquippedItem()) && mc.thePlayer.getItemInUseDuration() >= ticksToWait.getValue()) {
                for (int x = 0; x <= (32 - ticksToWait.getValue()); x++) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                }
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                mc.thePlayer.stopUsingItem();
            }
        }
    }

    private boolean isUsable(ItemStack stack) {
        if (stack == null)
            return false;
        if (!mc.thePlayer.isUsingItem())
            return false;
        if (stack.getItem() instanceof ItemBow)
            return bow.getValue();
        else if (stack.getItem() instanceof ItemFood)
            return food.getValue();
        else if (stack.getItem() instanceof ItemPotion)
            return potions.getValue();
        else if (stack.getItem() instanceof ItemBucketMilk)
            return milk.getValue();
        return false;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "bow":
                    if (arguments.length >= 3) {
                        bow.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        bow.setValue(!bow.getValue());
                    }
                    ChatLogger.print(String.format("FastUse will %s use bow quickly.", (bow.getValue() ? "now" : "no longer")));
                    break;
                case "food":
                    if (arguments.length >= 3) {
                        food.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        food.setValue(!food.getValue());
                    }
                    ChatLogger.print(String.format("FastUse will %s use food quickly.", (food.getValue() ? "now" : "no longer")));
                    break;
                case "milk":
                    if (arguments.length >= 3) {
                        milk.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        milk.setValue(!milk.getValue());
                    }
                    ChatLogger.print(String.format("FastUse will %s use milk quickly.", (milk.getValue() ? "now" : "no longer")));
                    break;
                case "potions":
                    if (arguments.length >= 3) {
                        potions.setValue(Boolean.parseBoolean(arguments[2]));
                    } else {
                        potions.setValue(!potions.getValue());
                    }
                    ChatLogger.print(String.format("FastUse will %s use potions quickly.", (potions.getValue() ? "now" : "no longer")));
                    break;
                case "tickstowait":
                case "ticks":
                    if (arguments.length >= 3) {
                        String newTicksString = arguments[2];
                        try {
                            int newTicks = Integer.parseInt(newTicksString);
                            ticksToWait.setValue(newTicks);
                            ChatLogger.print(String.format("FastUse Ticks to Wait set to %s", ticksToWait.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newTicksString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: fastuse tickstowait <number>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: bow, food, milk, potions, tickstowait");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: fastuse <action>");
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
