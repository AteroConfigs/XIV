package pw.latematt.xiv.mod.mods;

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
        super("ArmourBreaker", ModType.COMBAT, Keyboard.KEY_NONE, 0xFF808080);

        Command.newCommand()
                .cmd("armorbreakerpackets")
                .description("Command to set ArmorBreaker packets.")
                .arguments("<number>")
                .aliases("abp")
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
            case 3: {
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
        final String[] arguments = message.split(" ");
        String newPacketsString = arguments[1];
        try {
            int newPackets = Integer.parseInt(newPacketsString);
            packets.setValue(newPackets);
            ChatLogger.print(String.format("Armor Breaker Packets set to %s", packets.getValue()));
        } catch (NumberFormatException e) {
            ChatLogger.print(String.format("\"%s\" is not a number.", newPacketsString));
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
