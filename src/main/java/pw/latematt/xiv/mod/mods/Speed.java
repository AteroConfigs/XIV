package pw.latematt.xiv.mod.mods;

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

/**
 * @author Matthew
 */
public class Speed extends Mod implements Listener<MotionUpdateEvent>,CommandHandler {
    private int delay;
    private Value<Mode> currentMode = new Value<>("speed_mode", Mode.BYPASS);

    public Speed() {
        super("Speed", ModType.MOVEMENT, Keyboard.KEY_M, 0xFFDC5B18);

        Command.newCommand()
                .cmd("speed")
                .description("Base command for the Speed mod.")
                .arguments("<action>")
                .handler(this)
                .build();
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            /* thanks anodise */
            if (currentMode.getValue() == Mode.BYPASS) {
                switch (this.delay) {
                    case 1: {
                        mc.timer.timerSpeed = 1.0F;
                        mc.thePlayer.motionX /= 3.6D;
                        mc.thePlayer.motionZ /= 3.6D;
                        break;
                    }
                    case 2: {
                        double speed = 5.0D;

                        for (Object o : mc.thePlayer.getActivePotionEffects()) {
                            final PotionEffect effect = (PotionEffect) o;
                            Potion potion = Potion.potionTypes[effect.getPotionID()];

                            if (potion.getId() == Potion.moveSpeed.getId()) {
                                if (effect.getAmplifier() == 0) {
                                    speed = 4.7D;
                                } else if (effect.getAmplifier() == 1) {
                                    speed = 3.7D;
                                } else if (effect.getAmplifier() == 2) {
                                    speed = 2.7D;
                                } else if (effect.getAmplifier() >= 3) {
                                    speed = 1.7D;
                                }
                            }
                        }

                        if (mc.thePlayer.movementInput.moveStrafe != 0) {
                            speed -= 0.1D;
                        }

                        mc.timer.timerSpeed = (mc.thePlayer.getHealth() == mc.thePlayer.getMaxHealth() ? 1.30F : 1.0F);
                        mc.thePlayer.motionX *= speed;
                        mc.thePlayer.motionZ *= speed;
                        break;
                    }
                    case 3: {
                        mc.timer.timerSpeed = 1.0F;
                        mc.thePlayer.motionX /= 1.3D;
                        mc.thePlayer.motionZ /= 1.3D;
                        break;
                    }
                    case 4: {
                        this.delay = 0;
                        break;
                    }
                    default: {
                        mc.timer.timerSpeed = 1.0F;
                        mc.thePlayer.motionX *= 0.98D;
                        mc.thePlayer.motionZ *= 0.98D;
                        break;
                    }
                }
                this.delay++;
            } else if (currentMode.getValue() == Mode.OLD) {

            }
        }
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            // TODO: Make this actually function!
            ChatLogger.print("Command not functional yet. :(");

            switch (action.toLowerCase()) {
                case "bypass":
                case "anodise":
                    break;
                case "old":
                case "rederpz":
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: bypass, old");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: speed <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        mc.timer.timerSpeed = 1.0F;
    }

    public enum Mode {
        BYPASS, OLD
    }
}
