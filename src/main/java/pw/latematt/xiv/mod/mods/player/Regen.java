package pw.latematt.xiv.mod.mods.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Rederpz / Trevor
 */
public class Regen extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private final Value<Mode> currentMode = new Value<>("regen_mode", Mode.POTION);

    public Regen() {
        super("Regen", ModType.PLAYER, Keyboard.KEY_P, 0xFF9681D6);

        Command.newCommand()
                .cmd("regen")
                .description("Base command for the Regen mod.")
                .arguments("<action>")
                .handler(this)
                .build();
    }

    public void onEventCalled(MotionUpdateEvent event) {
        if(currentMode.getValue() == Mode.POTION) {
            if (mc.thePlayer.getActivePotionEffect(Potion.REGENERATION) != null) {
                if (mc.thePlayer.onGround || BlockUtils.isOnLadder(mc.thePlayer) || BlockUtils.isInLiquid(mc.thePlayer) || BlockUtils.isOnLiquid(mc.thePlayer)) {
                    if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
                        for (int i = 0; i < mc.thePlayer.getMaxHealth() - mc.thePlayer.getHealth(); i++) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                        }
                    }
                }
            }
        }else if(currentMode.getValue() == Mode.BYPASS && event.getCurrentState() == MotionUpdateEvent.State.POST){
            if (mc.thePlayer.onGround || BlockUtils.isOnLadder(mc.thePlayer) || BlockUtils.isInLiquid(mc.thePlayer) || BlockUtils.isOnLiquid(mc.thePlayer)) {
                if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
                    for(int i = 0; i < 1; i++) {
                        mc.thePlayer.setDead();
                    }
                }
            }
        }
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "mode":
                    if (arguments.length >= 3) {
                        String mode = arguments[2];
                        switch (mode.toLowerCase()) {
                            case "old":
                            case "potion":
                                currentMode.setValue(Mode.POTION);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "new":
                            case "bypass":
                                currentMode.setValue(Mode.BYPASS);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "-d":
                                currentMode.setValue(currentMode.getDefault());
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: new, old");
                                break;
                        }
                        setTag(String.format("%s \2477%s", getName(), currentMode.getValue().getName()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: speed mode <mode>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: mode");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: regen <action>");
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

    public enum Mode {
        POTION, BYPASS;

        public String getName() {
            String prettyName = "";
            String[] actualNameSplit = name().split("_");
            if (actualNameSplit.length > 0) {
                for (String arg : actualNameSplit) {
                    arg = arg.substring(0, 1).toUpperCase() + arg.substring(1, arg.length()).toLowerCase();
                    prettyName += arg + " ";
                }
            } else {
                prettyName = actualNameSplit[0].substring(0, 1).toUpperCase() + actualNameSplit[0].substring(1, actualNameSplit[0].length()).toLowerCase();
            }
            return prettyName.trim();
        }
    }
}
