package pw.latematt.xiv.mod.mods;

import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.EntityStepEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class Step extends Mod implements Listener<EntityStepEvent>,CommandHandler {
    private final Listener sendPacketListener;
    private boolean editPackets;
    private Value<Float> height = new Value<>("step_height", 1.5F);

    public Step() {
        super("Step");

        Command.newCommand()
                .cmd("step")
                .description("Base command for Step mod.")
                .arguments("<action>")
                .handler(this)
                .build();

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    // wip (find bypass)
                }
            }
        };
    }

    @Override
    public void onEventCalled(EntityStepEvent event) {
        final boolean shouldStep = event.getEntity() == mc.thePlayer
                && mc.thePlayer.onGround && !mc.thePlayer.isInWater()
                && !mc.thePlayer.isCollidedHorizontally;
        mc.thePlayer.stepHeight = mc.thePlayer.isInWater() ? 0.50F : height.getValue();
        editPackets = shouldStep;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action) {
                case "height":
                    String newHeightString = arguments[2];
                    try {
                        float newHeight = Float.parseFloat(newHeightString);
                        if (newHeight > 10.0F) {
                            newHeight = 10.0F;
                        } else if (newHeight < 0.5F) {
                            newHeight = 0.5F;
                        }
                        height.setValue(newHeight);
                        ChatLogger.print(String.format("Step Height set to %s", height.getValue()));
                    } catch (NumberFormatException e) {
                        ChatLogger.print(String.format("\"%s\" is not a number.", newHeightString));
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: height");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: step <action>");
        }
    }

    public boolean isEditingPackets() {
        return editPackets;
    }

    public void setEditPackets(boolean editPackets) {
        this.editPackets = editPackets;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        if (mc.thePlayer != null) {
            mc.thePlayer.stepHeight = 0.5F;
        }
    }
}
