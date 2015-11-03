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

/**
 * @author Jack
 * @author Rederpz
 */
public class Speed extends Mod implements CommandHandler {
    private final Value<Boolean> fastLadder = new Value<>("speed_fast_ladder", true);
    private final Value<Boolean> fastIce = new Value<>("speed_fast_ice", true);
    private final Value<Mode> currentMode = new Value<>("speed_mode", Mode.CAPSAR);
    private final Listener motionUpdateListener, moveListener;
    private boolean nextTick;
    private int ticks;

    public Speed() {
        super("Speed", ModType.MOVEMENT, Keyboard.KEY_F, 0xFFDC5B18);
        setTag(currentMode.getValue().getName());
        Command.newCommand().cmd("speed").description("Base command for the Speed mod.").arguments("<action>").handler(this).build();

        moveListener = new Listener<MoveEvent>() {
            @Override
            public void onEventCalled(MoveEvent event) {
                if (BlockUtils.isOnLadder(mc.thePlayer) && mc.thePlayer.isCollidedHorizontally && fastLadder.getValue()) {
                    mc.thePlayer.motionY = 0.1D;
                    event.setMotionY(event.getMotionY() * 2.25D);
                }

                if (fastIce.getValue() && BlockUtils.isOnIce(mc.thePlayer)) {
                    Blocks.ice.slipperiness = 0.6F;
                    Blocks.packed_ice.slipperiness = 0.6F;
                    double speed = 2.7D;
                    if (mc.thePlayer.isSprinting())
                        speed -= 0.2D;

                    event.setMotionX(event.getMotionX() * speed);
                    event.setMotionZ(event.getMotionZ() * speed);
                } else {
                    Blocks.ice.slipperiness = 0.98F;
                    Blocks.packed_ice.slipperiness = 0.98F;
                }
            }
        };

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    double speed;
                    switch (currentMode.getValue()) {
                        case FAST:
                            if (!mc.gameSettings.keyBindJump.getIsKeyPressed() && !mc.thePlayer.isCollidedHorizontally && isValid()) {
                                double offset = (mc.thePlayer.rotationYaw + 90 + (mc.thePlayer.moveForward > 0 ? (mc.thePlayer.moveStrafing > 0 ? -45 : mc.thePlayer.moveStrafing < 0 ? 45 : 0) : mc.thePlayer.moveForward < 0 ? 180 + (mc.thePlayer.moveStrafing > 0 ? 45 : mc.thePlayer.moveStrafing < 0 ? -45 : 0) : (mc.thePlayer.moveStrafing > 0 ? -90 : mc.thePlayer.moveStrafing < 0 ? 90 : 0))) * Math.PI / 180;

                                double x = Math.cos(offset) * 0.25F;
                                double z = Math.sin(offset) * 0.25F;

                                mc.thePlayer.motionX += x;
                                mc.thePlayer.motionY = 0.0175F;
                                mc.thePlayer.motionZ += z;

                                if (mc.thePlayer.movementInput.moveStrafe != 0) {
                                    mc.thePlayer.motionX *= 0.975F;
                                    mc.thePlayer.motionZ *= 0.975F;
                                }

                                mc.timer.timerSpeed = 1.11F;

                                nextTick = true;
                            } else {
                                mc.timer.timerSpeed = 1.0F;
                            }

                            if (nextTick && !mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.getIsKeyPressed() && !mc.thePlayer.isOnLadder()) {
                                mc.thePlayer.motionY = -0.1F;
                                nextTick = false;
                            }
                            break;
                        case CAPSAR:
                            speed = 2.37D;
                            if (isValid()) {
                                if (mc.thePlayer.isPotionActive(Potion.SPEED)) {
                                    PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.SPEED);
                                    switch (effect.getAmplifier()) {
                                        case 0:
                                            speed -= 0.2975D;
                                            break;
                                        case 1:
                                            speed -= 0.5575D;
                                            break;
                                        case 2:
                                            speed -= 0.7858D;
                                            break;
                                        case 3:
                                            speed -= 0.9075D;
                                            break;
                                    }
                                }

                                boolean strafe = mc.thePlayer.moveStrafing != 0.0F;
                                speed = speed + (mc.thePlayer.isSprinting() ? 0.02D : 0.40D);

                                if (!strafe)
                                    speed += 0.04F;

                                if (nextTick = !nextTick) {
                                    mc.thePlayer.motionX *= speed;
                                    mc.thePlayer.motionZ *= speed;
                                    mc.timer.timerSpeed = 1.15F;
                                } else {
                                    mc.thePlayer.motionX /= 1.50D;
                                    mc.thePlayer.motionZ /= 1.50D;
                                    mc.timer.timerSpeed = 1.0F;
                                }
                            } else if (nextTick) {
                                mc.thePlayer.motionX /= speed;
                                mc.thePlayer.motionZ /= speed;
                                mc.timer.timerSpeed = 1.0F;
                                nextTick = false;
                            }
                            break;
                        case OLD:
                            if (isValid()) {
                                speed = 3.0D;
                                double slow = 1.425D;
                                if (mc.thePlayer.moveStrafing == 0.0F)
                                    speed += 0.05D;

                                switch (++ticks) {
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
                                    default:
                                        mc.timer.timerSpeed = 1.0F;
                                        ticks = 0;
                                        break;
                                }
                            } else {
                                ticks = 4;
                                mc.timer.timerSpeed = 1.0F;
                                mc.thePlayer.motionX *= 0.98D;
                                mc.thePlayer.motionZ *= 0.98D;
                            }
                            break;
                    }
                }
            }
        };
    }

    private boolean isValid() {
        Step step = (Step) XIV.getInstance().getModManager().find("step");
        boolean editingPackets = step != null && step.isEditingPackets();
        boolean moving = mc.thePlayer.movementInput.moveForward != 0;
        boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
        moving = moving || strafing;

        return mc.thePlayer.onGround &&
                !BlockUtils.isOnLiquid(mc.thePlayer) &&
                !BlockUtils.isInLiquid(mc.thePlayer) &&
                !BlockUtils.isOnIce(mc.thePlayer) &&
                !editingPackets && moving;
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
                            case "fast":
                                currentMode.setValue(Mode.FAST);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "capsar":
                            case "capser":
                                currentMode.setValue(Mode.CAPSAR);
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
                            default:
                                ChatLogger.print("Invalid mode, valid: fast, casper, old");
                                break;
                        }
                        setTag(currentMode.getValue().getName());
                    } else {
                        ChatLogger.print("Invalid arguments, valid: speed mode <mode>");
                    }
                    break;
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
                case "fastice":
                case "fi":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            fastIce.setValue(fastIce.getDefault());
                        } else {
                            fastIce.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        fastIce.setValue(!fastIce.getValue());
                    }
                    ChatLogger.print(String.format("Speed will %s go fast on ice.", (fastIce.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: mode, fastladder, fastice");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: speed <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        XIV.getInstance().getListenerManager().add(moveListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(moveListener);
        mc.timer.timerSpeed = 1.0F;
        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;
    }

    private enum Mode {
        FAST, CAPSAR, OLD;

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

