package pw.latematt.xiv.mod.mods.misc;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.StringUtils;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.util.Random;
import java.util.regex.Matcher;

/**
 * @author Matthew
 */
public class AntiTabComplete extends Mod implements Listener<SendPacketEvent> {
    public AntiTabComplete() {
        super("AntiTabComplete", ModType.MISCELLANEOUS);
        setEnabled(true);
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C14PacketTabComplete) {
            C14PacketTabComplete packet = (C14PacketTabComplete) event.getPacket();

            if(packet.getMessage().startsWith(".")) {
                String[] arguments = packet.getMessage().split(" ");

                String[] messages = new String[] { "hey what's up ", "dude ", "hey ", "hi ", "man ", "yo ", "howdy ", "omg " };

                Random random = new Random();

                packet.setMessage(messages[random.nextInt(messages.length)]);
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
