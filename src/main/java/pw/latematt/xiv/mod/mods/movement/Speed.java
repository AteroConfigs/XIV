package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.MoveEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Matthew
 * @author Jack
 */
public class Speed extends Mod implements CommandHandler {
    private final Listener motionUpdateListener;
    private final Listener moveListener;
    private int delay;
    private boolean shouldBoost;
    // private final Value<Mode> currentMode = new Value<>("speed_mode", Mode.NEW);
    private final Value<Boolean> bypass = new Value<>("speed_bypass", true);
    private final Value<Boolean> fastLadder = new Value<>("speed_fast_ladder", true);
    private final Value<Double> speed = new Value<>("speed_multiplier", 2.0D);

    public Speed() {
        super("Speed", ModType.MOVEMENT, Keyboard.KEY_F, 0xFFDC5B18);
        // setTag(String.format("%s \2477%s", getName(), currentMode.getValue().getName()));

        Command.newCommand()
                .cmd("speed")
                .description("Base command for the Speed mod.")
                .arguments("<action>")
                .handler(this)
                .build();

        this.moveListener = new Listener<MoveEvent>() {
            @Override
            public void onEventCalled(MoveEvent event) {
                if (BlockUtils.isOnLadder(mc.thePlayer) && mc.thePlayer.isCollidedHorizontally && fastLadder.getValue()) {
                    mc.thePlayer.motionY = 0.1D;
                    event.setMotionY(event.getMotionY() * 2.25D);
                }

                if (bypass.getValue()) {
                    if (shouldBoost) {
                        event.setMotionX(event.getMotionX() * 2.0D);
                        event.setMotionZ(event.getMotionZ() * 2.0D);

                        if (delay >= 5) {
                            mc.timer.timerSpeed = 1.2F;
                            delay = 0;
                        } else {
                            mc.timer.timerSpeed = 1.0F;
                        }
                    }
                } else {
                    event.setMotionX(event.getMotionX() * speed.getValue());
                    event.setMotionZ(event.getMotionZ() * speed.getValue());
                }
            }
        };

        this.motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (bypass.getValue()) {
                        Step step = (Step) XIV.getInstance().getModManager().find("step");
                        boolean editingPackets = !Objects.isNull(step) && step.isEditingPackets();
                        boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
                        boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                        boolean moving = movingForward && strafing || movingForward;
                        if (!mc.thePlayer.onGround || BlockUtils.isInLiquid(mc.thePlayer) || editingPackets || BlockUtils.isOnLiquid(mc.thePlayer) || !moving) {
                            delay = 0;
                            shouldBoost = false;
                            return;
                        }

                        if (shouldBoost) {
                            event.setY(event.getY() + 0.001D);
                        }

                        shouldBoost = !shouldBoost;
                        delay++;
                    }

                    /*if (currentMode.getValue() == Mode.NEW) {
                        // thanks anodise
                        Step step = (Step) XIV.getInstance().getModManager().find("step");
                        boolean editingPackets = !Objects.isNull(step) && step.isEditingPackets();
                        boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
                        boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                        boolean moving = movingForward && strafing || movingForward;
                        if (!mc.thePlayer.onGround || BlockUtils.isInLiquid(mc.thePlayer) || editingPackets || BlockUtils.isOnLiquid(mc.thePlayer) || !moving) {
                            delay = 0;
                        }

                        switch (delay) {
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

                                    if (potion.getId() == Potion.SPEED.getId()) {
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
                                delay = 0;
                                break;
                            }
                            default: {
                                mc.timer.timerSpeed = 1.0F;
                                mc.thePlayer.motionX *= 0.98D;
                                mc.thePlayer.motionZ *= 0.98D;
                                break;
                            }
                        }
                        delay++;
                    } else if (currentMode.getValue() == Mode.OLD) {
                        double speed = 3.05D;
                        double slow = 1.425D;
                        boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
                        boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                        boolean moving = movingForward && strafing || movingForward;

                        boolean iceBelow = false;
                        boolean liquidBelow = false;

                        boolean shouldSpeed = !mc.thePlayer.isSneaking();
                        Step step = (Step) XIV.getInstance().getModManager().find("step");
                        boolean editingPackets = !Objects.isNull(step) && step.isEditingPackets();
                        if (!mc.thePlayer.onGround || editingPackets || !moving) {
                            mc.timer.timerSpeed = 1.0F;
                            mc.thePlayer.motionX *= 0.98D;
                            mc.thePlayer.motionZ *= 0.98D;
                            shouldSpeed = false;
                        }

                        if (BlockUtils.isOnIce(mc.thePlayer)) {
                            iceBelow = true;
                            shouldSpeed = false;
                        }

                        if (BlockUtils.isOnLiquid(mc.thePlayer)) {
                            liquidBelow = true;
                            shouldSpeed = false;
                        }

                        if (BlockUtils.isInLiquid(mc.thePlayer)) {
                            shouldSpeed = false;
                        }

                        if (iceBelow) {
                            mc.thePlayer.motionX *= 1.51D;
                            mc.thePlayer.motionZ *= 1.51D;
                        }

                        if (liquidBelow) {
                            if (delay > 2) {
                                delay = 0;
                            }

                            ++delay;
                            switch (delay) {
                                case 1:
                                    mc.thePlayer.motionX *= 1.4D;
                                    mc.thePlayer.motionZ *= 1.4D;
                                    break;
                                case 2:
                                    mc.thePlayer.motionX /= 1.375D;
                                    mc.thePlayer.motionZ /= 1.375D;
                                    delay = 0;
                                    break;
                            }
                        }

                        if (shouldSpeed) {
                            if (mc.thePlayer.moveStrafing != 0.0F) {
                                speed -= 0.1D;
                            }

                            if (mc.thePlayer.isInWater()) {
                                speed -= 0.1D;
                            }

                            ++delay;
                            switch (delay) {
                                case 1:
                                    mc.timer.timerSpeed = 1.325F;
                                    mc.thePlayer.motionX *= speed;
                                    mc.thePlayer.motionZ *= speed;
                                    break;
                                case 2:
                                    mc.timer.timerSpeed = 1.0F;
                                    mc.thePlayer.motionX /= slow;
                                    mc.thePlayer.motionZ /= slow;
                                    break;
                                case 3:
                                    mc.timer.timerSpeed = 1.05F;
                                    break;
                                case 4:
                                    mc.timer.timerSpeed = 1.0F;
                                    delay = 0;
                                    break;
                            }

                        } else if (mc.timer.timerSpeed > 1.0F) {
                            mc.timer.timerSpeed = 1.0F;
                        }
                    }*/
                }
            }
        };
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                /*case "mode":
                    if (arguments.length >= 3) {
                        String mode = arguments[2];
                        switch (mode.toLowerCase()) {
                            case "new":
                                currentMode.setValue(Mode.NEW);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "old":
                                currentMode.setValue(Mode.OLD);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "-d":
                                currentMode.setValue(currentMode.getDefault());
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "anodise":
                                currentMode.setValue(Mode.ANODISE);
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
                    break;*/
                case "fastladder":
                case "fl":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            fastLadder.setValue(fastLadder.getDefault());
                        } else {
                            fastLadder.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        fastLadder.setValue(!fastLadder.getValue());
                    }
                    ChatLogger.print(String.format("Speed will %s go fast on ladders.", (fastLadder.getValue() ? "now" : "no longer")));
                    break;
                case "bypass":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            bypass.setValue(bypass.getDefault());
                        } else {
                            bypass.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        bypass.setValue(!bypass.getValue());
                    }
                    ChatLogger.print(String.format("Speed will %s bypass nocheat.", (bypass.getValue() ? "now" : "no longer")));
                    break;
                case "speed":
                    if (arguments.length >= 3) {
                        String newSpeedString = arguments[2];
                        try {
                            double newSpeed = arguments[2].equalsIgnoreCase("-d") ? speed.getDefault() : Double.parseDouble(newSpeedString);
                            speed.setValue(newSpeed);
                            ChatLogger.print(String.format("Speed Multiplier set to %s", speed.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newSpeedString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: speed speed <number>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: bypass, fastladder, speed");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: speed <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this.moveListener);
        XIV.getInstance().getListenerManager().add(this.motionUpdateListener);

        //if (currentMode.getValue() == Mode.OLD) {
            Blocks.ice.slipperiness = 0.6F;
            Blocks.packed_ice.slipperiness = 0.6F;
        //}
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this.moveListener);
        XIV.getInstance().getListenerManager().remove(this.motionUpdateListener);

        mc.timer.timerSpeed = 1.0F;

        //if (currentMode.getValue() == Mode.OLD) {
            Blocks.ice.slipperiness = 0.98F;
            Blocks.packed_ice.slipperiness = 0.98F;
        //}
    }

    public enum Mode {
        NEW, OLD, ANODISE;

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
