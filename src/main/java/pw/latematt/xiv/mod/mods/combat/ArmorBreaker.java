package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.movement.Speed;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Jack
 */

public final class ArmorBreaker extends Mod implements Listener<AttackEntityEvent>, CommandHandler {
    private final Value<Boolean> crits = new Value<>("armorbreaker_crits", true);
    private int delay = 0;

    public ArmorBreaker() {
        super("ArmorBreaker", ModType.COMBAT, Keyboard.KEY_NONE, 0xFF808080);

        Command.newCommand()
                .cmd("armorbreaker")
                .description("Base command for ArmorBreaker mod.")
                .arguments("<crits>")
                .aliases("ab")
                .handler(this).build();
    }

    @Override
    public void onEventCalled(AttackEntityEvent event) {
        if (Objects.isNull(mc.thePlayer.getCurrentEquippedItem()) || Objects.isNull(mc.thePlayer.inventoryContainer.getSlot(27).getStack())) {
            return;
        }

        this.delay++;

        if (this.crits.getValue()) { // attempt at lessening flags due to crits
            if (XIV.getInstance().getModManager().find(Speed.class).isEnabled()) {
                mc.timer.timerSpeed = 1.0F;
            }

            switch (this.delay) {
                case 1: { // switch
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 27, mc.thePlayer.inventory.currentItem, 2, mc.thePlayer);
                    break;
                }

                case 2: { // crit
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625101, mc.thePlayer.posZ, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    break;
                }

                case 3: { // switch
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 27, mc.thePlayer.inventory.currentItem, 2, mc.thePlayer);
                    break;
                }

                case 4: { // crit & reset
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625101, mc.thePlayer.posZ, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    this.delay = 0;
                    break;
                }
            }
        } else {
            if (this.delay >= 2) {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 27, mc.thePlayer.inventory.currentItem, 2, mc.thePlayer);
                this.delay = 0;
            }
        }
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "crits":
                case "criticals":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            crits.setValue(crits.getDefault());
                        } else {
                            crits.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        crits.setValue(!crits.getValue());
                    }
                    ChatLogger.print(String.format("ArmorBreaker will %s perform crits.", (crits.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: crits");
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
        this.delay = 0;
    }
}