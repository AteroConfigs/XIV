package pw.latematt.xiv.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.ReadPacketEvent;
import pw.latematt.xiv.utils.ChatLogger;

import java.util.ArrayList;

/**
 * @author Matthew
 */
public class PluginFinder implements Listener<ReadPacketEvent>, CommandHandler {
    @Override
    public void onCommandRan(String message) {
        XIV.getInstance().getListenerManager().add(this);
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C14PacketTabComplete("/"));
    }

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

            StringBuilder builder = new StringBuilder();
            plugins.stream()
                    .filter(plugin -> !plugin.equalsIgnoreCase("minecraft"))
                    .filter(plugin -> !plugin.equalsIgnoreCase("bukkit"))
                    .filter(plugin -> !plugin.equalsIgnoreCase("spigot"))
                    .forEach(plugin -> builder.append(plugin).append(", "));

            if (plugins.size() > 0 && !builder.toString().equals("")) {
                ChatLogger.print("Plugins (" + plugins.size() + "): " + builder.toString().substring(0, builder.toString().length() - 2));
            } else {
                ChatLogger.print("Unable to find plugins, or the server has none.");
            }

            XIV.getInstance().getListenerManager().remove(this);
        }
    }
}
