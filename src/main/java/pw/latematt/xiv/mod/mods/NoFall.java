package pw.latematt.xiv.mod.mods;

import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author TehNeon
 */
public class NoFall extends Mod implements Listener<SendPacketEvent> {
    public NoFall() {
        super("NoFall", ModType.PLAYER, Keyboard.KEY_NONE, 0xFFf5b8cd);
    }

    public void onEventCalled(SendPacketEvent event) {
        if(event.getPacket() instanceof C03PacketPlayer) {
            if(mc.thePlayer.fallDistance >= 3F) {
                C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();
                packet.onGround = true;
                packet.moving = true;

                event.setPacket(packet);
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
