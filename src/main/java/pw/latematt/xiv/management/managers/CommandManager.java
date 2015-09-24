package pw.latematt.xiv.management.managers;

import net.minecraft.network.play.client.C01PacketChatMessage;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.utils.ChatLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthew
 */
public class CommandManager extends ListManager<Command> {
    private String prefix = ".";

    public CommandManager() {
        super(new ArrayList<>());
    }

    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void setup() {
        // setup is not required in command management, commands will add themselves as they are built
        // however, we do add certain commands here
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + "...");
        Command.newCommand()
                .cmd("help")
                .description("Provides help with commands.")
                .aliases("cmds", "?")
                .arguments("[command]")
                .handler(message -> {
                    List<Command> commandList = XIV.getInstance().getCommandManager().getContents();
                    StringBuilder commands = new StringBuilder("Commands (" + commandList.size() + "): ");
                    for (Command command : commandList) {
                        commands.append(prefix).append(command.getCmd()).append(", ");
                    }
                    ChatLogger.print(commands.toString().substring(0, commands.length() - 2));
                }).build();

        XIV.getInstance().getListenerManager().add(new Listener<SendPacketEvent>() {
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C01PacketChatMessage) {
                    C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
                    String[] spaceSplit = packet.getMessage().split(" ");
                    if (spaceSplit[0].startsWith(prefix)) {
                        for (Command command : contents) {
                            if (spaceSplit[0].equalsIgnoreCase(prefix + command.getCmd())) {
                                command.getHandler().onCommandRan(packet.getMessage());
                                event.setCancelled(true);
                                return;
                            }
                        }
                        ChatLogger.print("Invalid command \"" + spaceSplit[0] + "\"");
                        event.setCancelled(true);
                    }
                }
            }
        });
        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }
}
