package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.init.Blocks;
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
 * @author Jack
 * @author Rederpz
 */
public class Speed extends Mod implements CommandHandler {
    private final Listener motionUpdateListener, moveListener;
    private int delay;
    private boolean shouldBoost;
    private final Value<Mode> currentMode = new Value<>("speed_mode", Mode.NEW);
    private final Value<Boolean> fastLadder = new Value<>("speed_fast_ladder", true);

    public Speed() {
        super("Speed", ModType.MOVEMENT, Keyboard.KEY_F, 0xFFDC5B18);
        setTag(String.format("%s \2477%s", getName(), currentMode.getValue().getName()));

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

                if (currentMode.getValue() == Mode.NEW) {
                    if (shouldBoost) {
                        double speed = 2.035D;

                        if (mc.thePlayer.moveStrafing != 0.0F) {
                            speed -= 0.04D;
                        }

                        if (!mc.thePlayer.isSprinting()) {
                            speed += 0.15D;
                        }

                        if (BlockUtils.isOnIce(mc.thePlayer)) {
                            speed = 4.0D;
                            delay = 0;
                        }

                        if (mc.thePlayer.hurtTime > 0) {
                            speed += 0.01D;
                        }

                        event.setMotionX(event.getMotionX() * speed);
                        event.setMotionZ(event.getMotionZ() * speed);

                        if (delay >= 5) {
                            mc.timer.timerSpeed = 1.3F;
                            if (delay >= 6) {
                                delay = 0;
                            }
                        } else {
                            mc.timer.timerSpeed = 1.0F;
                        }
                    }
                }
            }
        };

        this.motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                Step step = (Step) XIV.getInstance().getModManager().find("step");
                boolean editingPackets = !Objects.isNull(step) && step.isEditingPackets();
                boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
                boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                boolean moving = movingForward && strafing || movingForward;
                if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
                    if (currentMode.getValue() == Mode.OLD) {
                        if (!mc.thePlayer.onGround || BlockUtils.isInLiquid(mc.thePlayer) || editingPackets || !moving) {
                            delay = 0;
                            mc.timer.timerSpeed = 1.0F;
                            shouldBoost = false;
                        } else {
                            double speed = 3.1D;
                            double slow = 1.425D;

                            if (BlockUtils.isOnIce(mc.thePlayer)) {
                                mc.thePlayer.motionX *= 1.51D;
                                mc.thePlayer.motionZ *= 1.51D;

                                return;
                            }

                            if (BlockUtils.isOnLiquid(mc.thePlayer)) {
                                return;
                            }

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
                        }
                    }
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {
                    if (currentMode.getValue() == Mode.NEW) {
                        if (!mc.thePlayer.onGround || BlockUtils.isInLiquid(mc.thePlayer) || editingPackets || !moving) {
                            delay = 0;
                            mc.timer.timerSpeed = 1.0F;
                            shouldBoost = false;
                        } else {
                            if (shouldBoost) {
                                event.setY(event.getY() + 0.001D);
                            }

                            shouldBoost = !shouldBoost;
                            delay++;
                        }
                    }
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
                case "mode":
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
                            default:
                                ChatLogger.print("Invalid mode, valid: new, old");
                                break;
                        }
                        setTag(String.format("%s \2477%s", getName(), currentMode.getValue().getName()));
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

        Blocks.ice.slipperiness = 0.6F;
        Blocks.packed_ice.slipperiness = 0.6F;
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this.moveListener);
        XIV.getInstance().getListenerManager().remove(this.motionUpdateListener);

        mc.timer.timerSpeed = 1.0F;

        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;
    }

    public enum Mode {
        NEW, OLD;

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
