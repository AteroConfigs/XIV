package pw.latematt.xiv.management.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
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
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        String commandName = arguments[1];
                        Command command = XIV.getInstance().getCommandManager().find(commandName);
                        if (command != null) {
                            ChatLogger.print(String.format("Help for %s.", command.getCmd()));
                            ChatLogger.print(String.format("Description for %s: %s.", command.getCmd(), command.getDescription() == null ? "No description" : command.getDescription()));
                            ChatLogger.print(String.format("Aliases for %s: %s.", command.getCmd(), command.getAliases() == null ? "No aliases" : Arrays.asList(command.getAliases())));
                            ChatLogger.print(String.format("Arguments for %s: %s.", command.getCmd(), command.getArguments() == null ? "No arguments" : Arrays.asList(command.getArguments())));
                        } else {
                            ChatLogger.print(String.format("Invalid command \"%s\"", commandName));
                        }
                    } else {
                        List<Command> commandList = XIV.getInstance().getCommandManager().getContents();
                        StringBuilder commands = new StringBuilder("Commands (" + commandList.size() + "): ");
                        for (Command command : commandList) {
                            commands.append(prefix).append(command.getCmd()).append(", ");
                        }
                        ChatLogger.print(commands.toString().substring(0, commands.length() - 2));
                    }
                }).build();
        Command.newCommand()
                .cmd("vclip")
                .description("Allows you to teleport up and down.")
                .aliases("vc", "up", "down")
                .arguments("<blocks>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        String blockChangeString = arguments[1];
                        try {
                            double newHeight = Double.parseDouble(blockChangeString);

                            Minecraft.getMinecraft().thePlayer.func_174826_a(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0, newHeight, 0));

                            // Minecraft.getMinecraft().thePlayer.posY += newHeight;

                            ChatLogger.print(String.format("You've teleported %s block%s", newHeight, (newHeight > 1 || newHeight < -1) ? "s" : ""));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", blockChangeString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: vclip <blocks>");
                    }
                }).build();
        Command.newCommand()
                .cmd("damage")
                .description("Force damage.")
                .aliases("dmg")
                .handler(message -> {
                    for (int i = 0; i < 81; i++) {
                        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + 0.05D, Minecraft.getMinecraft().thePlayer.posZ, false));
                        Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, false));
                    }
                }).build();
        Command.newCommand()
                .cmd("linewidth")
                .description("Manages the line width of rendering mods.")
                .aliases("lwidth", "linew", "lw")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        Float lineWidth = Float.parseFloat(arguments[1]);
                        Value<Float> value = (Value<Float>) XIV.getInstance().getValueManager().find("render_line_width");
                        value.setValue(lineWidth);
                        ChatLogger.print(String.format("Render Line Width set to: %s", value.getValue()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: linewidth <float>");
                    }
                }).build();
        Command.newCommand()
                .cmd("antialiasing")
                .description("Manages the anti aliasing of rendering mods.")
                .aliases("aaliasing", "antia", "aa")
                .handler(message -> {
                    Value<Boolean> value = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_anti_aliasing");
                    value.setValue(!value.getValue());
                    ChatLogger.print(String.format("Render mods will %s use antialiasing.", value.getValue() ? "now" : "no longer"));
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

                            if (command.getAliases() != null) {
                                for (String alias : command.getAliases()) {
                                    if (spaceSplit[0].equalsIgnoreCase(prefix + alias)) {
                                        command.getHandler().onCommandRan(packet.getMessage());
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
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

    public Command find(Class clazz) {
        for (Command cmd : getContents()) {
            if (cmd.getClass().equals(clazz)) {
                return cmd;
            }
        }

        return null;
    }

    public Command find(String name) {
        for (Command cmd : getContents()) {
            if (cmd
                    .getCmd()
                    .equals(name)) {
                return cmd;
            }
        }

        return null;
    }
}
