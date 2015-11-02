package pw.latematt.xiv.mod.mods.player;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.InventoryUtils;
import pw.latematt.xiv.value.ClampedValue;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

import static pw.latematt.xiv.utils.InventoryUtils.*;

/**
 * @author Matthew
 */
public class AutoHeal extends Mod implements CommandHandler {
    private final ClampedValue<Long> delay = new ClampedValue<>("autoheal_delay", 350L, 0L, 1000L);
    private final ClampedValue<Float> health = new ClampedValue<>("autoheal_health", 13.0F, 1.0F, 20.0F);
    private final Value<Boolean> soup = new Value<>("autoheal_soup", false);
    private final Value<Boolean> potion = new Value<>("autoheal_potion", true);
    private final Listener sendPacketListener, motionUpdateListener;
    private final Timer timer = new Timer();
    private boolean healing;

    public AutoHeal() {
        super("AutoHeal", ModType.PLAYER, Keyboard.KEY_NONE, 0xFF85E0E0);
        Command.newCommand().cmd("autoheal").aliases("aheal", "autoh", "ah").description("Base command for the AutoHeal mod.").arguments("<action>").handler(this).build();

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
                    updateTag();
                    if (mc.thePlayer.getHealth() <= health.getValue() && timer.hasReached(delay.getValue())) {
                        if (soup.getValue()) {
                            dropFirst(Items.bowl);
                            if (!hotbarHas(Items.mushroom_stew))
                                getFromInventory(Items.mushroom_stew);

                            useFirst(Items.mushroom_stew);

                            if (mc.thePlayer.getHealth() <= health.getValue() && potion.getValue() && countPotion(Potion.INSTANT_HEALTH, true) > 0) {
                                if (!hotbarHasPotion(Potion.INSTANT_HEALTH, true))
                                    getPotionFromInventory(Potion.INSTANT_HEALTH, true);

                                if (hotbarHasPotion(Potion.INSTANT_HEALTH, true)) {
                                    healing = true;
                                    event.setYaw(-event.getYaw());
                                    event.setPitch(85);
                                }
                            } else {
                                timer.reset();
                            }
                        } else if (potion.getValue() && countPotion(Potion.INSTANT_HEALTH, true) > 0) {
                            if (!hotbarHasPotion(Potion.INSTANT_HEALTH, true))
                                getPotionFromInventory(Potion.INSTANT_HEALTH, true);

                            if (hotbarHasPotion(Potion.INSTANT_HEALTH, true)) {
                                healing = true;
                                event.setYaw(-event.getYaw());
                                event.setPitch(85);
                            }
                        }
                    }
                } else if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.POST)) {
                    if (healing) {
                        useFirstPotion(Potion.INSTANT_HEALTH, true);
                        healing = false;
                        timer.reset();
                    }
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (healing) {
                        player.setYaw(-player.getYaw());
                        player.setPitch(85);
                    }
                }
            }
        };
    }

    private void updateTag() {
        String tag = "";
        if (potion.getValue() && InventoryUtils.countPotion(Potion.INSTANT_HEALTH, true) > 0) {
            tag += "\247c" + InventoryUtils.countPotion(Potion.INSTANT_HEALTH, true);
            if (soup.getValue() && InventoryUtils.countItem(Items.mushroom_stew) > 0) {
                tag += " \2476" + InventoryUtils.countItem(Items.mushroom_stew);
            }
        }

        if (soup.getValue() && InventoryUtils.countItem(Items.mushroom_stew) > 0) {
            tag += "\2476" + InventoryUtils.countItem(Items.mushroom_stew);
        }

        setTag(tag);
    }

    public boolean isHealing() {
        return healing;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "delay":
                case "d":
                    if (arguments.length >= 3) {
                        String newDelayString = arguments[2];
                        try {
                            if (arguments[2].equalsIgnoreCase("-d")) {
                                delay.setValue(delay.getDefault());
                            } else {
                                long newDelay = Long.parseLong(newDelayString);
                                delay.setValue(newDelay);
                                if (delay.getValue() > delay.getMax())
                                    delay.setValue(delay.getMax());
                                else if (delay.getValue() < delay.getMin())
                                    delay.setValue(delay.getMin());
                            }
                            ChatLogger.print(String.format("AutoHeal Delay set to %sms", delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: autoheal delay <number>");
                    }
                    break;
                case "health":
                    if (arguments.length >= 3) {
                        String newHealthString = arguments[2];
                        try {
                            if (arguments[2].equalsIgnoreCase("-d")) {
                                health.setValue(health.getDefault());
                            } else {
                                Float newHealth = Float.parseFloat(newHealthString);
                                health.setValue(newHealth);
                                if (health.getValue() > health.getMax())
                                    health.setValue(health.getMax());
                                else if (health.getValue() < health.getMin())
                                    health.setValue(health.getMin());
                            }

                            ChatLogger.print(String.format("AutoHeal Health set to %s", health.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newHealthString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: autoheal health <number>");
                    }
                    break;
                case "potion":
                case "pot":
                    useFirstPotion(Potion.INSTANT_HEALTH, true);
                    break;
                case "soup":
                    dropFirst(Items.bowl);
                    useFirst(Items.mushroom_stew);
                    break;
                case "usepotions":
                case "usepots":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            potion.setValue(potion.getDefault());
                        } else {
                            potion.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        potion.setValue(!potion.getValue());
                    }
                    ChatLogger.print(String.format("AutoHeal will %s use potions.", (potion.getValue() ? "now" : "no longer")));
                    break;
                case "usesoups":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            soup.setValue(soup.getDefault());
                        } else {
                            soup.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        soup.setValue(!soup.getValue());
                    }
                    ChatLogger.print(String.format("AutoHeal will %s use soups.", (soup.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: delay, health, potion, soup, usepotions, usesoups");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: autoheal <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(sendPacketListener);
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
    }
}
