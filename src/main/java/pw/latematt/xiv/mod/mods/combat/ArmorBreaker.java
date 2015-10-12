package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Jack
 */

public final class ArmorBreaker extends Mod implements Listener<AttackEntityEvent>, CommandHandler {
    private final Value<Integer> packets = new Value<>("armorbreaker_packets", 50);
    private int itemSwitchTicks = 0;

    public ArmorBreaker() {
        super("ArmorBreaker", ModType.COMBAT, Keyboard.KEY_NONE, 0xFF808080);

        Command.newCommand()
                .cmd("armorbreaker")
                .description("Base command for ArmorBreaker mod.")
                .arguments("<number>")
                .aliases("ab")
                .handler(this).build();
    }

    @Override
    public void onEventCalled(AttackEntityEvent event) {
        if (this.packets.getValue() != 0) {
            for (int i = 0; i < this.packets.getValue(); i++) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
            }
        }

        switch (++this.itemSwitchTicks) {
            case 2: {
                if (!mc.thePlayer.inventoryContainer.getSlot(27).getHasStack()) {
                    break;
                }

                final ItemStack item = mc.thePlayer.inventoryContainer.getSlot(27).getStack();

                if (Objects.nonNull(item)) {
                    mc.playerController.windowClick(0, 27, 0, 2, mc.thePlayer);
                    break;
                }

                break;
            }

            case 4: {
                if (mc.thePlayer.inventoryContainer.getSlot(27).getHasStack()) {
                    final ItemStack item = mc.thePlayer.inventoryContainer.getSlot(27).getStack();
                    if (Objects.nonNull(item)) {
                        mc.playerController.windowClick(0, 27, 0, 2, mc.thePlayer);
                    }
                }

                this.itemSwitchTicks = 0;
                break;
            }
        }
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "packets":
                    if (arguments.length >= 3) {
                        String newPacketsString = arguments[2];
                        try {
                            if(arguments[2].equalsIgnoreCase("-d")) {
                                packets.setValue(packets.getDefault());
                            }else{
                                int newPackets = Integer.parseInt(newPacketsString);
                                if (newPackets < 0) {
                                    newPackets = 0;
                                }
                                packets.setValue(newPackets);
                            }
                            ChatLogger.print(String.format("ArmorBreaker Packet Amount set to %s", packets.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newPacketsString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: armorbreaker packets <number>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: packets");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: armorbreaker <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        this.itemSwitchTicks = 0;
    }
}