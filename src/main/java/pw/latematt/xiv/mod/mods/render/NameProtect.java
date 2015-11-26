package pw.latematt.xiv.mod.mods.render;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.StringUtils;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MouseClickEvent;
import pw.latematt.xiv.event.events.RenderStringEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.regex.Matcher;

/**
 * @author Matthew
 */
public class NameProtect extends Mod implements Listener<RenderStringEvent>, CommandHandler {
    private final Value<Boolean> nameComplete = new Value<>("nameprotect_name_complete", true);
    private final Value<Boolean> middleClickFriends = new Value<>("nameprotect_middle_click_friends", true);
    private final Listener sendPacketEvent, mouseClickListener;

    public NameProtect() {
        super("NameProtect", ModType.RENDER);
        Command.newCommand().cmd("nameprotect").description("Base command for NameProtect mod.").arguments("<action>").aliases("np").handler(this).build();

        sendPacketEvent = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (!nameComplete.getValue())
                    return;
                if (event.getPacket() instanceof C01PacketChatMessage) {
                    C01PacketChatMessage message = (C01PacketChatMessage) event.getPacket();
                    for (Object o : mc.ingameGUI.getTabList().getPlayerList()) {
                        NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
                        String mcName = StringUtils.stripControlCodes(mc.ingameGUI.getTabList().getPlayerName(playerInfo));
                        if (XIV.getInstance().getFriendManager().isFriend(mcName)) {
                            String alias = XIV.getInstance().getFriendManager().getContents().get(mcName);
                            message.setMessage(message.getMessage().replaceAll("(?i)" + Matcher.quoteReplacement("-" + alias), mcName));
                        }
                    }
                }
            }
        };

        mouseClickListener = new Listener<MouseClickEvent>() {
            @Override
            public void onEventCalled(MouseClickEvent event) {
                if (!middleClickFriends.getValue())
                    return;
                if (event.getButton() == 2 && mc.thePlayer != null) {
                    if (mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
                        if (XIV.getInstance().getFriendManager().isFriend(player.getName()))
                            XIV.getInstance().getFriendManager().remove(player.getName());
                        else
                            XIV.getInstance().getFriendManager().add(player.getName(), player.getName());
                    }
                }
            }
        };
        setEnabled(true);
    }

    @Override
    public void onEventCalled(RenderStringEvent event) {
        switch (event.getState()) {
            case CHAT:
                event.setString(XIV.getInstance().getFriendManager().replace(event.getString(), true));
                break;
            case NAMETAG:
                event.setString(StringUtils.stripControlCodes(protect(event.getString())));
                break;
            case TAB:
            case SCOREBOARD:
                event.setString(protect(event.getString()));
                break;
        }
    }

    public String protect(String string) {
        if (XIV.getInstance().getFriendManager().isFriend(string))
            return String.format("\2473%s\247r", XIV.getInstance().getFriendManager().getContents().get(StringUtils.stripControlCodes(string)));
        return string;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "middleclickfriends":
                case "middlecf":
                case "mcf":
                    if (arguments.length >= 3) {
                        middleClickFriends.setValue(arguments[2].equalsIgnoreCase("-d") || Boolean.parseBoolean(arguments[2]));
                    } else {
                        middleClickFriends.setValue(!middleClickFriends.getValue());
                    }
                    ChatLogger.print(String.format("NameProtect will %s friend players you middle click.", (middleClickFriends.getValue() ? "now" : "no longer")));
                    break;
                case "namecomplete":
                case "dashnames":
                    if (arguments.length >= 3) {
                        nameComplete.setValue(arguments[2].equalsIgnoreCase("-d") || Boolean.parseBoolean(arguments[2]));
                    } else {
                        nameComplete.setValue(!nameComplete.getValue());
                    }
                    ChatLogger.print(String.format("NameProtect will %s complete names in sent chat messages.", (nameComplete.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: middleclickfriends, namecomplete");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: nameprotect <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this, sendPacketEvent, mouseClickListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this, sendPacketEvent, mouseClickListener);
    }
}
