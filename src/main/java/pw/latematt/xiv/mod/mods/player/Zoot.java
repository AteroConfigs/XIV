package pw.latematt.xiv.mod.mods.player;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
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

public class Zoot extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private final Value<Boolean> fire = new Value<>("zoot_fire", true);
    private final Value<Boolean> potions = new Value<>("zoot_potions", true);

    public Zoot() {
        super("Zoot", ModType.PLAYER, Keyboard.KEY_NONE, 0xFFD298ED);

        Command.newCommand()
                .cmd("zoot")
                .description("Base command for the Zoot mod.")
                .arguments("<action>")
                .handler(this).build();
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            if (this.fire.getValue() && mc.thePlayer.isBurning() && !mc.thePlayer.isInsideOfMaterial(Material.fire) && !mc.thePlayer.isInsideOfMaterial(Material.lava)) {
                for (int x = 0; x < 20; x++) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
                }
            }

            if (!this.potions.getValue()) {
                return;
            }

            if (mc.thePlayer.isPotionActive(Potion.BLINDNESS.getId())) {
                mc.thePlayer.removePotionEffect(Potion.BLINDNESS.getId());
            }

            if (mc.thePlayer.isPotionActive(Potion.NAUSEA.getId())) {
                mc.thePlayer.removePotionEffect(Potion.NAUSEA.getId());
            }

            if (mc.thePlayer.isPotionActive(Potion.MINING_FATIGUE.getId())) {
                mc.thePlayer.removePotionEffect(Potion.MINING_FATIGUE.getId());
            }

            final Potion[] potionTypes;
            for (int length = (potionTypes = Potion.potionTypes).length, i = 0; i < length; i++) {
                final Potion potion = potionTypes[i];
                if (Objects.nonNull(potion) && potion.isBadEffect() && mc.thePlayer.isPotionActive(potion)) {
                    final PotionEffect effect = mc.thePlayer.getActivePotionEffect(potion);
                    for (int x = 0; x < effect.getDuration() / 20; x++) {
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                }
            }
        }
    }

    @Override
    public void onCommandRan(String message) {
        final String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            switch (arguments[1]) {
                case "fire":
                case "antifire":
                case "f":
                case "af":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            fire.setValue(fire.getDefault());
                        } else {
                            fire.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        fire.setValue(!fire.getValue());
                    }
                    ChatLogger.print(String.format("Zoot will %s remove fire.", (fire.getValue() ? "now" : "no longer")));
                    break;

                case "potions":
                case "antipotion":
                case "p":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            fire.setValue(fire.getDefault());
                        } else {
                            potions.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        potions.setValue(!potions.getValue());
                    }
                    ChatLogger.print(String.format("Zoot will %s remove potions.", (potions.getValue() ? "now" : "no longer")));
                    break;

                default:
                    ChatLogger.print("Invalid action, valid: fire, potions");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: step <action>");
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
