package pw.latematt.xiv.mod.mods;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.ReadPacketEvent;
import pw.latematt.xiv.event.events.UsingItemSlowdownEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author TehNeon
 */
public class Velocity extends Mod implements Listener<ReadPacketEvent>, CommandHandler {

    public Value<Float> reducedVelocity = new Value<>("velocity_reduction", 0.7F);

    public Velocity() {
        super("Velocity", Keyboard.KEY_NONE, 0x36454f, true);
        setEnabled(true);

        Command.newCommand()
                .cmd("velocity")
                .aliases("vel")
                .description("Base command for Velocity mod.")
                .arguments("<action>")
                .handler(this)
                .build();
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action) {
                case "percent":
                    if (arguments.length >= 3) {
                        String newVelocityString = arguments[2];
                        try {
                            float newPercent = Float.parseFloat(newVelocityString);

                            reducedVelocity.setValue(newPercent);
                            ChatLogger.print(String.format("Velocity Percent set to %s", (reducedVelocity.getValue() * 100F) + "%"));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newVelocityString));
                        }
                    } else {
                        ChatLogger.print("Invalid syntax, valid: velocity percent <number>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: percent");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: velocity <action>");
        }
    }

    @Override
    public void onEventCalled(ReadPacketEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

            if (mc.thePlayer.getEntityId() == packet.getEntityID()) {
                event.setCancelled(true);
                double velX = packet.getVelocityX() * reducedVelocity.getValue() / 8000;
                double velY = packet.getVelocityY() * reducedVelocity.getValue() / 8000;
                double velZ = packet.getVelocityZ() * reducedVelocity.getValue() / 8000;

                mc.thePlayer.motionX += velX;
                mc.thePlayer.motionY += velY;
                mc.thePlayer.motionZ += velZ;
            }
        }
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
