package pw.latematt.xiv.mod.mods;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.StringUtils;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.util.regex.Matcher;

/**
 * @author Matthew
 */
public class NameComplete extends Mod implements Listener<SendPacketEvent> {
    public NameComplete() {
        super("NameComplete", ModType.PLAYER);
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C01PacketChatMessage) {
            C01PacketChatMessage message = (C01PacketChatMessage) event.getPacket();
            for (Object o : mc.ingameGUI.getTabList().getPlayerList()) {
                NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
                String mcname = StringUtils.stripControlCodes(mc.ingameGUI.getTabList().getPlayerName(playerInfo));
                if (XIV.getInstance().getFriendManager().isFriend(mcname)) {
                    String alias = XIV.getInstance().getFriendManager().getContents().get(mcname);
                    message.setMessage(message.getMessage().replaceAll("(?i)" + Matcher.quoteReplacement("-" + alias), mcname));
                }
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
