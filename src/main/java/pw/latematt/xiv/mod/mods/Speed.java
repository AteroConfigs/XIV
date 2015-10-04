package pw.latematt.xiv.mod.mods;

import net.minecraft.init.Blocks;
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
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Matthew
 */
public class Speed extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private int delay;
    private Value<Mode> currentMode = new Value<>("speed_mode", Mode.BYPASS);

    public Speed() {
        super("Speed", ModType.MOVEMENT, Keyboard.KEY_F, 0xFFDC5B18);

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
                Step step = (Step) XIV.getInstance().getModManager().find("step");
                boolean editingPackets = !Objects.isNull(step) && step.isEditingPackets();
                boolean movingForward = mc.thePlayer.movementInput.moveForward > 0;
                boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                boolean moving = movingForward || strafing;
                if (!mc.thePlayer.onGround || BlockUtils.isInLiquid(mc.thePlayer) || editingPackets || BlockUtils.isOnLiquid(mc.thePlayer) || !moving) {
                    delay = 0;
                }

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
            } else if (currentMode.getValue() == Mode.OLD || currentMode.getValue() == Mode.NORMAL) {
                double speed = currentMode.getValue() == Mode.NORMAL ? 1.3D : 3.05D;
                double slow = 1.425D;
                double offset = currentMode.getValue() == Mode.NORMAL ? 0.55D : 4.9D;
                boolean moving = mc.thePlayer.movementInput.moveForward != 0;
                boolean strafing = mc.thePlayer.movementInput.moveStrafe != 0;
                boolean movingCheck = moving || strafing;

                boolean iceBelow = false;
                boolean liquidBelow = false;

                boolean shouldSpeed = !mc.thePlayer.isSneaking();
                Step step = (Step) XIV.getInstance().getModManager().find("step");
                boolean editingPackets = !Objects.isNull(step) && step.isEditingPackets();
                if (!mc.thePlayer.onGround || editingPackets || !movingCheck) {
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
                    if (!mc.thePlayer.isSprinting()) {
                        offset += 0.8D;
                    }

                    if (mc.thePlayer.moveStrafing != 0.0F) {
                        speed -= 0.1D;
                        offset += 0.5D;
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
                            if (currentMode.getValue() == Mode.NORMAL) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX + mc.thePlayer.motionX / offset, mc.thePlayer.posY, mc.thePlayer.posZ + mc.thePlayer.motionZ / offset);
                            }
                            delay = 0;
                            break;
                    }

                } else if (mc.timer.timerSpeed > 1.0F) {
                    mc.timer.timerSpeed = 1.0F;
                }
            }
        }
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
                            case "bypass":
                                currentMode.setValue(Mode.BYPASS);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().name()));
                                break;
                            case "normal":
                                currentMode.setValue(Mode.NORMAL);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().name()));
                                break;
                            case "old":
                                currentMode.setValue(Mode.OLD);
                                ChatLogger.print(String.format("Speed Mode set to: %s", currentMode.getValue().name()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: bypass, normal, old");
                                break;
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: speed mode <mode>");
                    }
                    break;
                default:
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: speed <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);

        Blocks.ice.slipperiness = 0.6F;
        Blocks.packed_ice.slipperiness = 0.6F;
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        mc.timer.timerSpeed = 1.0F;

        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;
    }

    public enum Mode {
        BYPASS, NORMAL, OLD
    }
}
