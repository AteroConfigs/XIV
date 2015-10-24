package pw.latematt.xiv.mod.mods.disabled;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.input.Keyboard;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Jack
 */
// TODO: fix
public class AutoHead extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private final Value<Long> delay = new Value<>("autohead_delay", 5000L);
    private final Value<Float> health = new Value<>("autohead_health", 20.0F);
    private final Timer timer = new Timer();

    public AutoHead() {
        super("AutoHead", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFFFAA00);

        Command.newCommand()
                .cmd("autohead")
                .description("Base command for the AutoHead mod.")
                .aliases("ah")
                .arguments("<action>")
                .handler(this)
                .build();
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
            if ((mc.thePlayer.getHealth() <= this.health.getValue() || this.doesFriendNeedHealth()) && this.timer.hasReached(this.delay.getValue())) {
                if (this.doesHotbarHaveHeads()) {
                    for (int i = 36; i < 45; i++) {
                        final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if (Objects.nonNull(stack) && stack.getItem() instanceof ItemSkull) {
                            final int oldSlot = mc.thePlayer.inventory.currentItem;
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(i - 36));
                            mc.playerController.updateController();
                            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(oldSlot));
                            break;
                        }
                    }
                } else {
                    this.getHeadsFromInventory();
                }

                this.timer.reset();
            }
        }
    }

    private boolean doesHotbarHaveHeads() {
        for (int i = 36; i < 45; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (Objects.nonNull(stack) && stack.getItem() instanceof ItemSkull) {
                return true;
            }
        }

        return false;
    }

    private void getHeadsFromInventory() {
        if (mc.currentScreen instanceof GuiChest) {
            return;
        }

        for (int i = 9; i < 36; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (Objects.nonNull(stack) && stack.getItem() instanceof ItemSkull) {
                mc.playerController.windowClick(0, i, 0, 1, mc.thePlayer);
                break;
            }
        }
    }

    private boolean doesFriendNeedHealth() {
        for (final Object o : mc.theWorld.getLoadedEntityList()) {
            final Entity entity = (Entity) o;
            if (o instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) entity;
                if (XIV.getInstance().getFriendManager().isFriend(player.getName())) {
                    if (player.getHealth() <= this.health.getValue()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onCommandRan(String message) {
        final String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            switch (arguments[1].toLowerCase()) {
                case "delay":
                case "d": {
                    if (arguments.length >= 3) {
                        final String newDelayString = arguments[2];
                        try {
                            final long newDelay = Long.parseLong(newDelayString);
                            this.delay.setValue(newDelay);

                            if (this.delay.getValue() > 10000L) {
                                this.delay.setValue(10000L);
                            } else if (this.delay.getValue() < 500L) {
                                this.delay.setValue(500L);
                            }

                            ChatLogger.print(String.format("AutoHead Delay set to %s", this.delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: autohead delay <number>");
                    }

                    break;
                }

                case "health":
                case "h": {
                    if (arguments.length >= 3) {
                        final String newHealthString = arguments[2];
                        try {
                            final float newHealth = Float.parseFloat(newHealthString);
                            this.health.setValue(newHealth);

                            if (this.health.getValue() > 39.0F) {
                                this.health.setValue(39.0F);
                            } else if (this.health.getValue() < 1.0F) {
                                this.health.setValue(1.0F);
                            }

                            ChatLogger.print(String.format("AutoHead Health set to %s", this.health.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newHealthString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: autohead health <number>");
                    }
                    break;
                }

                default: {
                    ChatLogger.print("Invalid action, valid: delay, health");
                }
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: autohead <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        if (mc.thePlayer.getMaxHealth() != 40.0F) {
            ChatLogger.print("You must be in UHC to use this mod.");
            this.toggle();
        }
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
    }
}
