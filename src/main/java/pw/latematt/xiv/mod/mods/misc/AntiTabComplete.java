package pw.latematt.xiv.mod.mods.misc;

import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.ReadPacketEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Matthew
 */
public class AntiTabComplete extends Mod implements Listener<SendPacketEvent>, CommandHandler {

    private final Listener readPacketListener;

    public AntiTabComplete() {
        super("AntiTabComplete", ModType.MISCELLANEOUS);
        setEnabled(true);

        Command.newCommand()
                .cmd("pluginfinder")
                .aliases("pl", "pf")
                .description("Find plugins the server has.")
                .arguments("<action>")
                .handler(this).build();

        readPacketListener = new Listener<ReadPacketEvent>() {
            @Override
            public void onEventCalled(ReadPacketEvent event) {
                if (event.getPacket() instanceof S3APacketTabComplete) {
                    S3APacketTabComplete packet = (S3APacketTabComplete) event.getPacket();
                    event.setCancelled(true);

                    ArrayList<String> plugins = new ArrayList<>();
                    for (String cmd : packet.func_149630_c()) {
                        String[] arguments = cmd.split(":");
                        if (arguments.length > 1 && !plugins.contains(arguments[0].substring(1))) {
                            plugins.add(arguments[0].substring(1));
                        }
                    }

                    String foundPlugins = "";
                    for (String plugin : plugins) {
                        if (!plugin.equalsIgnoreCase("minecraft") && !plugin.equalsIgnoreCase("bukkit") && !plugin.equalsIgnoreCase("spigot")) {
                            foundPlugins = foundPlugins + plugin + ", ";
                        }
                    }

                    if (plugins.size() > 0 && !foundPlugins.equals("")) {
                        ChatLogger.print("Found plugins (" + plugins.size() + "): " + foundPlugins.substring(0, foundPlugins.length() - 2));
                    } else {
                        ChatLogger.print("Unable to find plugins, or the server has none.");
                    }

                    XIV.getInstance().getListenerManager().remove(readPacketListener);
                }
            }
        };
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C14PacketTabComplete) {
            C14PacketTabComplete packet = (C14PacketTabComplete) event.getPacket();

            if (packet.getMessage().startsWith(XIV.getInstance().getCommandManager().getPrefix())) {
                String[] arguments = packet.getMessage().split(" ");
                String[] messages = new String[]{"hey what's up ", "dude ", "hey ", "hi ", "man ", "yo ", "howdy ", "omg "};
                Random random = new Random();

                packet.setMessage(messages[random.nextInt(messages.length)] + arguments[arguments.length - 1]);
            }
        }
    }

    @Override
    public void onCommandRan(String message) {
        mc.getNetHandler().addToSendQueue(new C14PacketTabComplete("/"));

        XIV.getInstance().getListenerManager().add(readPacketListener);
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
