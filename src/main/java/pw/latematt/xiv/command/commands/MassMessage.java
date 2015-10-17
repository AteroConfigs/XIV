package pw.latematt.xiv.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.Timer;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Rederpz
 */
public class MassMessage implements CommandHandler, Listener<MotionUpdateEvent> {
    private CopyOnWriteArrayList<String> players = new CopyOnWriteArrayList<>();
    private final Timer timer = new Timer();
    private String message;
    private long delay;
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");

        if (arguments.length >= 2) {
            try {
                long delay = Long.parseLong(arguments[1]);
                String newMessage = message.substring(arguments[1].length() + 1).substring(arguments[0].length() + 1);
                this.delay = delay;
                this.message = newMessage;

                if (mc.thePlayer != null) {
                    for (Object o : mc.ingameGUI.getTabList().getPlayerList()) {
                        NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
                        String mcname = StringUtils.stripControlCodes(mc.ingameGUI.getTabList().getPlayerName(playerInfo));

                        players.add(mcname);
                    }
                    timer.reset();

                    XIV.getInstance().getListenerManager().add(this);
                } else {
                    ChatLogger.print("Unable to send mass message.");
                }
            } catch (NumberFormatException e) {
                ChatLogger.print(String.format("\"%s\" is not a number.", arguments[1]));
            } catch (ArrayIndexOutOfBoundsException e) {
                ChatLogger.print("Invalid arguments, valid: massmessage <delay> <message>");
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: massmessage <delay> <message>");
        }
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (!message.equals("")) {
            if (timer.hasReached(delay)) {
                String player = players.get(0);

                if (player != null && !players.isEmpty()) {
                    String sendMessage = message;

                    if (message.contains("@")) {
                        sendMessage = sendMessage.replaceAll("@", player);
                    }
                    ChatLogger.print("Sending message to " + player + ".");

                    mc.thePlayer.sendChatMessage(sendMessage);

                    players.remove(0);
                    timer.reset();

                    if (players.isEmpty()) {
                        ChatLogger.print("Finished mass message.");

                        message = "";
                        delay = -1L;
                        players.clear();

                        XIV.getInstance().getListenerManager().remove(this);
                    }
                }
            }
        }
    }
}
