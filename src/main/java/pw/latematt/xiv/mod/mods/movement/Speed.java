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
import pw.latematt.xiv.event.events.MotionEvent;
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
    private boolean nextTick, hasJumped;
    private int ticks;

    public Speed() {
        super("Speed", ModType.MOVEMENT, Keyboard.KEY_F, 0xFFDC5B18);
        setTag(currentMode.getValue().getName());
        Command.newCommand().cmd("speed").description("Base command for the Speed mod.").arguments("<action>").handler(this).build();

        moveListener = new Listener<MotionEvent>() {
            @Override
            public void onEventCalled(MotionEvent event) {
                if (event.getState() == MotionEvent.State.PRE) {
                    if (BlockUtils.isOnLadder(mc.thePlayer) && mc.thePlayer.isCollidedHorizontally && fastLadder.getValue()) {
                        mc.thePlayer.motionY = 0.1D;
                        event.setMotionY(event.getMotionY() * 2.25D);
                    }

                    if (fastIce.getValue()) {
                        Blocks.ice.slipperiness = 0.6F;
                        Blocks.packed_ice.slipperiness = 0.6F;
                        if (BlockUtils.isOnIce(mc.thePlayer)) {
                            double speed = 2.7D;
                            if (mc.thePlayer.isSprinting())
                                speed -= 0.2D;

                            event.setMotionX(event.getMotionX() * speed);
                            event.setMotionZ(event.getMotionZ() * speed);
                        }
                    } else {
                        Blocks.ice.slipperiness = 0.98F;
                        Blocks.packed_ice.slipperiness = 0.98F;
                    }
                }
            }
        };

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    double speed, slow;
                    double yDifference = mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
                    boolean groundCheck = mc.thePlayer.onGround && yDifference == 0.0D;
                    boolean strafe = mc.thePlayer.moveStrafing != 0.0F;
                    switch (currentMode.getValue()) {
                        case FAST:
                            if (canSpeed(mc.thePlayer.onGround)) {
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

                                mc.getTimer().timerSpeed = 1.11F;

                                hasJumped = true;
                            }

                            if (hasJumped && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder()) {
                                mc.thePlayer.motionY = -0.1F;
                                hasJumped = false;
                            }

                            break;
                        case CAPSAR:
                            speed = 2.433D;
                            slow = 1.5D;
                            if (!mc.thePlayer.isSprinting()) {
                                speed += 0.4D;
                                slow -= 0.005D;
                            }
                            if (strafe) {
                                speed -= 0.04F;
                                slow -= 0.005D;
                            }
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

                            if (canSpeed(groundCheck)) {
                                if (nextTick = !nextTick) {
                                    mc.thePlayer.motionX *= speed;
                                    mc.thePlayer.motionZ *= speed;
                                } else {
                                    mc.thePlayer.motionX /= slow;
                                    mc.thePlayer.motionZ /= slow;
                                }
                            } else if (nextTick) {
                                mc.thePlayer.motionX /= speed;
                                mc.thePlayer.motionZ /= speed;
                                nextTick = false;
                            }
                            break;
                        case OLD:
                            speed = 3.0D;
                            slow = 1.425D;
                            if (!mc.thePlayer.isSprinting()) {
                                speed += 0.4D;
                                slow -= 0.005D;
                            }
                            if (strafe) {
                                speed -= 0.04F;
                                slow -= 0.005D;
                            }

                            if (canSpeed(groundCheck)) {
                                switch (++ticks) {
                                    case 1:
                                        mc.thePlayer.motionX *= speed;
                                        mc.thePlayer.motionZ *= speed;
                                        break;
                                    case 2:
                                        mc.thePlayer.motionX /= slow;
                                        mc.thePlayer.motionZ /= slow;
                                        break;
                                    case 3:
                                        break;
                                    default:
                                        ticks = 0;
                                        break;
                                }
                            } else {
                                mc.thePlayer.motionX *= 0.98D;
                                mc.thePlayer.motionZ *= 0.98D;
                                ticks = 4;
                            }
                            break;
                    }
                }
            }
        };
    }

    private boolean canSpeed(boolean groundCheck) {
        Step step = (Step) XIV.getInstance().getModManager().find("step");
        boolean editingPackets = step != null && step.isEditingPackets();

        boolean moving = mc.thePlayer.movementInput.moveForward != 0;
        boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;

        moving = moving || strafing;

        boolean sneaking = mc.thePlayer.isSneaking();
        boolean collided = mc.thePlayer.isCollidedHorizontally;

        boolean inLiquid = BlockUtils.isInLiquid(mc.thePlayer);
        boolean onLiquid = BlockUtils.isOnLiquid(mc.thePlayer);
        boolean onIce = BlockUtils.isOnIce(mc.thePlayer);

        return moving && !sneaking && !collided && groundCheck && !inLiquid && !onLiquid && !onIce && !editingPackets;
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
                            case "new":
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
                                ChatLogger.print("Invalid mode, valid: fast, capsar, old");
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

