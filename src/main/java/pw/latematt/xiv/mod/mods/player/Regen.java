package pw.latematt.xiv.mod.mods.player;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.Timer;
import tv.twitch.chat.Chat;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Rederpz
 */
public class Regen extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {

    public Regen() {
        super("Regen", ModType.PLAYER, Keyboard.KEY_P, 0xFF9681D6);

        Command.newCommand()
                .cmd("massmessage")
                .description("Send a message to every player in the tab list.")
                .arguments("<delay> <message>")
                .aliases("masscommand", "mc", "masschat", "mm")
                .handler(this)
                .build();

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if(!message.equals("")) {
                    if(timer.hasReached(delay)) {
                        String player = players.get(0);

                        if(player != null && !players.isEmpty()) {
                            String sendMessage = message;

                            if(message.contains("@")) {
                                sendMessage = sendMessage.replaceAll("@", player);
                            }else{
                                sendMessage = sendMessage = player;
                            }
                            ChatLogger.print("Sending message to " + player + ".");

                            mc.thePlayer.sendChatMessage(sendMessage);

                            players.remove(0);
                            timer.reset();

                            if(players.isEmpty()) {
                                ChatLogger.print("Finished mass message.");

                                message = "";
                                delay = -1L;
                                players.clear();

                                XIV.getInstance().getListenerManager().remove(motionUpdateListener);
                            }
                        }
                    }
                }
            }
        };
    }

    public void onEventCalled(MotionUpdateEvent event) {
        if (mc.thePlayer.getActivePotionEffect(Potion.REGENERATION) != null) {
            if (mc.thePlayer.onGround || BlockUtils.isOnLadder(mc.thePlayer) || BlockUtils.isInLiquid(mc.thePlayer) || BlockUtils.isOnLiquid(mc.thePlayer)) {
                if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
                    for (int i = 0; i < mc.thePlayer.getMaxHealth() - mc.thePlayer.getHealth(); i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                }
            }
        }
    }

    private final Listener motionUpdateListener;

    private CopyOnWriteArrayList<String> players = new CopyOnWriteArrayList<>();
    private final Timer timer = new Timer();
    private String message;
    private long delay;

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");

        if (arguments.length >= 2) {
            long delay = Long.parseLong(arguments[1]);
            String newMessage = message.substring(arguments[1].length() + 1).substring(arguments[0].length() + 1);

            try {
                this.delay = delay;
                this.message = newMessage;

                if(mc.thePlayer != null) {
                    for (Object o : mc.ingameGUI.getTabList().getPlayerList()) {
                        NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
                        String mcname = StringUtils.stripControlCodes(mc.ingameGUI.getTabList().getPlayerName(playerInfo));

                        players.add(mcname);
                    }
                    timer.reset();

                    XIV.getInstance().getListenerManager().add(motionUpdateListener);
                }else{
                    ChatLogger.print("Unable to send mass message.");
                }
            }catch(NumberFormatException e) {
                ChatLogger.print(String.format("\"%s\" is not a number.", arguments[1]));
            }catch(ArrayIndexOutOfBoundsException e) {
                ChatLogger.print("Invalid arguments, valid: massmessage <delay> <message>");
            }
        }else{
            ChatLogger.print("Invalid arguments, valid: massmessage <delay> <message>");
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
