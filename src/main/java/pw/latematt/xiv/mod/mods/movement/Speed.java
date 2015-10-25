package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.init.Blocks;
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
import pw.latematt.xiv.mod.mods.combat.Criticals;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Jack
 * @author Rederpz
 */
public class Speed extends Mod implements CommandHandler {
    private final Value<Mode> currentMode = new Value<>("speed_mode", Mode.NEW);
    private final Value<Boolean> fastLadder = new Value<>("speed_fast_ladder", true);
    private final Listener motionUpdateListener, moveListener;
    private int delay;
    private boolean nextTick;

    public Speed() {
        super("Speed", ModType.MOVEMENT, Keyboard.KEY_F, 0xFFDC5B18);
        setTag(String.format("%s \2477%s", getName(), currentMode.getValue().getName()));

        Command.newCommand()
                .cmd("speed")
                .description("Base command for the Speed mod.")
                .arguments("<action>")
                .handler(this)
                .build();

        moveListener = new Listener<MoveEvent>() {
            @Override
            public void onEventCalled(MoveEvent event) {
                if (BlockUtils.isOnLadder(mc.thePlayer) && mc.thePlayer.isCollidedHorizontally && fastLadder.getValue()) {
                    mc.thePlayer.motionY = 0.1D;
                    event.setMotionY(event.getMotionY() * 2.25D);
                }
            }
        };

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (Objects.equals(currentMode.getValue(), Mode.OLD)) {
                        if (isValid()) {
                            double speed = 3.0D;
                            double slow = 1.425D;
                            if (mc.thePlayer.moveStrafing == 0.0F) {
                                speed += 0.05D;
                            }

                            switch (++delay) {
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
                                    delay = 0;
                                    break;
                            }
                        } else {
                            delay = 4;
                            mc.timer.timerSpeed = 1.0F;
                            mc.thePlayer.motionX *= 0.98D;
                            mc.thePlayer.motionZ *= 0.98D;
                        }
                    }

                    if (Objects.equals(currentMode.getValue(), Mode.NEW)) {
                        double speed = 2.3D;

                        for (final Object o : mc.thePlayer.getActivePotionEffects()) {
                            final PotionEffect effect = (PotionEffect) o;
                            switch (effect.getAmplifier()) {
                                case 0: {
                                    speed -= 0.3D;
                                    break;
                                }
                            }
                        }

                        final boolean strafe = mc.thePlayer.moveStrafing != 0.0F;
                        speed = speed + (mc.thePlayer.isSprinting() ? 0.02D : 0.40D);

                        if (!strafe) {
                            speed += 0.04F;
                        }

                        if (isValid()) {
                            nextTick = !nextTick;
                            if (nextTick) {
                                mc.thePlayer.motionX *= speed;
                                mc.thePlayer.motionZ *= speed;
                                mc.timer.timerSpeed = 1.15F;
                                Criticals crits = (Criticals) XIV.getInstance().getModManager().find("criticals");
                                event.setY(event.getY() + 0.0001D);
                                if (crits.isEnabled() && crits.getFallDistance() > 0.0F) {
                                    crits.setFallDistance(crits.getFallDistance() + 0.0001F);
                                }
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
                    }
                }
            }
        };
    }

    private boolean isValid() {
        Step step = (Step) XIV.getInstance().getModManager().find("step");
        boolean editingPackets = step != null && step.isEditingPackets();
        boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
        boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
        boolean moving = movingForward && strafing || movingForward;

        return mc.thePlayer.onGround &&
                !BlockUtils.isOnLiquid(mc.thePlayer) &&
                !BlockUtils.isInLiquid(mc.thePlayer) &&
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
                    ChatLogger.print("Invalid action, valid: mode, fastladder");
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
        Blocks.ice.slipperiness = 0.6F;
        Blocks.packed_ice.slipperiness = 0.6F;
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(moveListener);
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

