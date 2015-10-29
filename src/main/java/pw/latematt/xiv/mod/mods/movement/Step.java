package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.EntityStepEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.combat.Criticals;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.ClampedValue;

/**
 * @author Matthew
 */
public class Step extends Mod implements Listener<EntityStepEvent>, CommandHandler {
    private final ClampedValue<Float> height = new ClampedValue<>("step_height", 1.0646F, 0.5F, 10.0F);
    private final Listener sendPacketListener;
    private boolean editPackets;

    public Step() {
        super("Step", ModType.MOVEMENT);

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
                    if (editPackets) {
                        if (mc.thePlayer.posY - mc.thePlayer.lastTickPosY >= 0.80D) {
                            player.setY(player.getY() + 0.0646D);

                            Criticals criticals = (Criticals) XIV.getInstance().getModManager().find("criticals");
                            if (criticals != null && criticals.isEnabled())
                                criticals.setFallDistance(criticals.getFallDistance() + 0.0646F);
                        }
                        editPackets = false;
                    }
                }
            }
        };
    }

    @Override
    public void onEventCalled(EntityStepEvent event) {
        if (mc.thePlayer == null || !event.canStep())
            return;

        if (event.getEntity() != mc.thePlayer)
            return;

        if (mc.thePlayer.stepHeight != height.getValue())
            mc.thePlayer.stepHeight = height.getValue();

        editPackets = !BlockUtils.isInLiquid(mc.thePlayer);
        event.setCancelled(!editPackets);
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
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

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
        if (mc.thePlayer != null)
            mc.thePlayer.stepHeight = height.getValue();
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        editPackets = false;
        if (mc.thePlayer != null)
            mc.thePlayer.stepHeight = 0.5F;
    }
}
