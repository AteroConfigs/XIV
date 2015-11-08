package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.EntityStepEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.ClampedValue;
import pw.latematt.xiv.value.Value;

import java.util.Iterator;
import java.util.List;

/**
 * @author Matthew
 */
public class Step extends Mod implements Listener<EntityStepEvent>, CommandHandler {
    private final Value<Mode> mode = new Value<>("step_mode", Mode.OLD);
    private final ClampedValue<Float> height = new ClampedValue<>("step_height", 1.0646F, 0.5F, 10.0F);
    private final Listener sendPacketListener, motionUpdateListener;
    private boolean editPackets;

    private int delay = 0;

    public Step() {
        super("Step", ModType.MOVEMENT);
        Command.newCommand().cmd("step").description("Base command for Step mod.").arguments("<action>").handler(this).build();

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (delay > 0)
                        delay--;
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (editPackets && mode.getValue() == Mode.OLD) {
                        if (mc.thePlayer.posY - mc.thePlayer.lastTickPosY >= 0.75D)
                            player.setY(player.getY() + 0.0646D);
                        editPackets = false;
                    } else if (mode.getValue() == Mode.JUMP) {
                        if (mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.37F;
                            mc.thePlayer.isAirBorne = true;
                        }
                        editPackets = false;
                    } else if (mode.getValue() == Mode.BLINK) {
                        if (editPackets)
                            event.setCancelled(true);

                        if (mc.thePlayer.onGround)
                            editPackets = false;
                    }
                }
            }
        };
        setTag(this.mode.getValue().getName());
    }

    @Override
    public void onEventCalled(EntityStepEvent event) {
        if (mc.thePlayer == null || !event.canStep())
            return;

        if (event.getEntity() != mc.thePlayer)
            return;

        switch (mode.getValue()) {
            case OLD:
                mc.thePlayer.stepHeight = height.getValue();

                editPackets = !BlockUtils.isInLiquid(mc.thePlayer);
                event.setCancelled(!editPackets);
                break;
            case BLINK:
                mc.thePlayer.stepHeight = delay == 0 ? height.getValue() : 0.5F;

                double yDifference = mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
                boolean yDiffCheck = yDifference == 0.0D;

                if (delay == 0 && yDiffCheck && event.canStep()) {
                    mc.thePlayer.onGround = false;
                    mc.thePlayer.isAirBorne = true;
                    editPackets = true;

                    double y = 0.0F, offset = 0.4015F;

                    while (y < 1.0F) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + y, mc.thePlayer.posZ, false));

                        y += offset;
                        offset -= (0.165F * offset);

                        if (offset < 0.3)
                            offset = 0.3F;
                    }

                    delay = 1;
                }else{
                    mc.thePlayer.stepHeight = 0.5F;
                }
                break;
            default:
                mc.thePlayer.stepHeight = 0.5F;

                editPackets = !BlockUtils.isInLiquid(mc.thePlayer);
                break;
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
                            case "new":
                            case "blink":
                            case "offset":
                                this.mode.setValue(Mode.BLINK);
                                ChatLogger.print(String.format("Step Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            case "jump":
                                this.mode.setValue(Mode.JUMP);
                                ChatLogger.print(String.format("Step Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            case "old":
                                this.mode.setValue(Mode.OLD);
                                ChatLogger.print(String.format("Step Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            case "-d":
                                this.mode.setValue(this.mode.getDefault());
                                ChatLogger.print(String.format("Step Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: blink, jump, old");
                                break;
                        }
                        setTag(this.mode.getValue().getName());
                    } else {
                        ChatLogger.print("Invalid arguments, valid: step mode <mode>");
                    }
                    break;
                case "height":
                    if (arguments.length >= 3) {
                        String newHeightString = arguments[2];
                        try {
                            float newHeight = arguments[2].equalsIgnoreCase("-d") ? height.getDefault() : Float.parseFloat(newHeightString);
                            height.setValue(newHeight);
                            if (height.getValue() > height.getMax())
                                height.setValue(height.getMax());
                            else if (height.getValue() < height.getMin())
                                height.setValue(height.getMin());

                            mc.thePlayer.stepHeight = height.getValue();
                            ChatLogger.print(String.format("Step Height set to %s", height.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newHeightString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: step height <number>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: height, mode");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: step <action>");
        }
    }

    public boolean isEditingPackets() {
        return editPackets;
    }

    public boolean isNewStep() {
        return this.mode.getValue() == Mode.BLINK;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        if (mc.thePlayer != null)
            mc.thePlayer.stepHeight = height.getValue();
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        editPackets = false;
        if (mc.thePlayer != null)
            mc.thePlayer.stepHeight = 0.5F;
    }

    private enum Mode {
        OLD, JUMP, BLINK;

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
