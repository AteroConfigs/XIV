package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Jack
 */

public class SmoothAimbot extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private final Value<Float> fov = new Value<>("smoothaimbot_fov", 80F);
    private final Value<Float> speed = new Value<>("smoothaimbot_speed", 8F);
    private final Value<Double> range = new Value<>("smoothaimbot_range", 3.8D);
    private final Value<Boolean> pitch = new Value<>("smoothaimbot_pitch", true);

    public SmoothAimbot() {
        super("SmoothAimbot", ModType.COMBAT, Keyboard.KEY_NONE);

        Command.newCommand()
                .cmd("smoothaimbot")
                .description("Base command for the SmoothAimbot mod.")
                .arguments("<action>")
                .aliases("sa")
                .handler(this).build();
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        final EntityPlayer target = this.getClosestPlayerToCursor(this.fov.getValue());
        if (Objects.nonNull(target)) {
            if (pitch.getValue())
                mc.thePlayer.rotationPitch = mc.thePlayer.rotationPitch + (EntityUtils.getPitchChange(target) / speed.getValue());
            mc.thePlayer.rotationYaw = mc.thePlayer.rotationYaw + (EntityUtils.getYawChange(target) / speed.getValue());
        }
    }

    private EntityPlayer getClosestPlayerToCursor(final float angle) {
        float distance = angle;
        EntityPlayer tempPlayer = null;
        for (final Object o : mc.theWorld.playerEntities) {
            final EntityPlayer player = (EntityPlayer) o;

            if (this.isValidEntity(player)) {
                final float yaw = EntityUtils.getYawChange(player);
                final float pitch = EntityUtils.getPitchChange(player);

                if (yaw > angle || pitch > angle) {
                    continue;
                }

                final float currentDistance = (yaw + pitch) / 2F;

                if (currentDistance <= distance) {
                    distance = currentDistance;
                    tempPlayer = player;
                }
            }
        }

        return tempPlayer;
    }

    @Override
    public void onCommandRan(String message) {
        final String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            switch (arguments[1].toLowerCase()) {
                case "fov":
                case "angle":
                    if (arguments.length >= 3) {
                        String newFovString = arguments[2];
                        try {
                            float newFov = arguments[2].equalsIgnoreCase("-d") ? fov.getDefault() : Float.parseFloat(newFovString);
                            fov.setValue(newFov);
                            ChatLogger.print(String.format("SmoothAimbot FOV set to %s", newFov));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newFovString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: smoothaimbot fov <number>");
                    }
                    break;

                case "speed":
                    if (arguments.length >= 3) {
                        String newSpeedString = arguments[2];
                        try {
                            float newSpeed = arguments[2].equalsIgnoreCase("-d") ? speed.getDefault() : Float.parseFloat(newSpeedString);
                            speed.setValue(newSpeed);
                            ChatLogger.print(String.format("SmoothAimbot Speed set to %s", newSpeed));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newSpeedString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: smoothaimbot speed <number>");
                    }
                    break;

                case "range":
                case "r":
                    if (arguments.length >= 3) {
                        String newRangeString = arguments[2];
                        try {
                            double newRange = arguments[2].equalsIgnoreCase("-d") ? range.getDefault() : Double.parseDouble(newRangeString);
                            range.setValue(newRange);
                            ChatLogger.print(String.format("SmoothAimbot Range set to %s", range.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newRangeString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: smoothaimbot range <number>");
                    }
                    break;

                case "pitch":
                    if (arguments.length >= 3) {
                        if(arguments[2].equalsIgnoreCase("-d")) {
                            pitch.setValue(pitch.getDefault());
                        }else {
                            pitch.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        pitch.setValue(!pitch.getValue());
                    }
                    ChatLogger.print(String.format("SmoothAimbot will %s aim in pitch.", (pitch.getValue() ? "now" : "no longer")));
                    break;

                default:
                    ChatLogger.print("Invalid action, valid: fov, speed, range, pitch");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: smoothaimbot <action>");
        }
    }

    private boolean isValidEntity(final EntityPlayer player) {
        return Objects.nonNull(player) && player.isEntityAlive() && player.getDistanceToEntity(mc.thePlayer) <= this.range.getValue() &&
                player.ticksExisted > 20 && !player.isInvisibleToPlayer(mc.thePlayer) && !XIV.getInstance().getFriendManager().isFriend(player.getName());
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
