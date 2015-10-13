package pw.latematt.xiv.management.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.potion.Potion;
import net.minecraft.util.StatCollector;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.event.events.WorldBobbingEvent;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.value.Value;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Matthew
 */
public class CommandManager extends ListManager<Command> {
    private String prefix = ".";
    private final Minecraft mc = Minecraft.getMinecraft();

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
                            ChatLogger.print(String.format("Help for %s:", command.getCmd()));
                            if (command.getDescription() != null) {
                                ChatLogger.print(String.format("Description for %s: %s", command.getCmd(), command.getDescription()));
                            }
                            if (command.getAliases() != null) {
                                ChatLogger.print(String.format("Aliases for %s: %s", command.getCmd(), Arrays.asList(command.getAliases())));
                            }
                            if (command.getArguments() != null) {
                                ChatLogger.print(String.format("Arguments for %s: %s", command.getCmd(), Arrays.asList(command.getArguments())));
                            }
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
                            double newBlockChange = Double.parseDouble(blockChangeString);
                            mc.thePlayer.func_174826_a(mc.thePlayer.getEntityBoundingBox().offset(0, newBlockChange, 0));
                            ChatLogger.print(String.format("You've teleported %s %s block%s", newBlockChange < 0 ? "down" : "up", newBlockChange, (newBlockChange > 1 || newBlockChange < -1) ? "s" : ""));
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
                    EntityUtils.damagePlayer();
                }).build();
        Command.newCommand()
                .cmd("render")
                .description("Manages options for render mods.")
                .arguments("<action>")
                .aliases("rnd")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        String action = arguments[1];
                        switch (action) {
                            case "linewidth":
                            case "lw":
                                if (arguments.length >= 3) {
                                    Value<Float> value = (Value<Float>) XIV.getInstance().getValueManager().find("render_line_width");
                                    if (arguments[2].equalsIgnoreCase("-d")) {
                                        value.setValue(value.getDefault());
                                    } else {
                                        Float lineWidth = Float.parseFloat(arguments[2]);
                                        value.setValue(lineWidth);
                                    }
                                    ChatLogger.print(String.format("Render Line Width set to: %s", value.getValue()));
                                } else {
                                    ChatLogger.print("Invalid arguments, valid: linewidth <float>");
                                }
                                break;
                            case "antialiasing":
                            case "aa":
                                Value<Boolean> antiAliasing = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_anti_aliasing");
                                if (arguments.length >= 3) {
                                    if (arguments[2].equalsIgnoreCase("-d")) {
                                        antiAliasing.setValue(antiAliasing.getDefault());
                                    } else {
                                        antiAliasing.setValue(Boolean.parseBoolean(arguments[2]));
                                    }
                                } else {
                                    antiAliasing.setValue(!antiAliasing.getValue());
                                }
                                ChatLogger.print(String.format("Render mods will %s use antialiasing.", antiAliasing.getValue() ? "now" : "no longer"));
                                break;
                            case "worldbobbing":
                            case "wb":
                                Value<Boolean> worldBobbing = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_world_bobbing");
                                if (arguments.length >= 3) {
                                    if (arguments[2].equalsIgnoreCase("-d")) {
                                        worldBobbing.setValue(worldBobbing.getDefault());
                                    } else {
                                        worldBobbing.setValue(Boolean.parseBoolean(arguments[2]));
                                    }
                                } else {
                                    worldBobbing.setValue(!worldBobbing.getValue());
                                }
                                ChatLogger.print(String.format("Render mods will %s render world bobbing.", worldBobbing.getValue() ? "now" : "no longer"));
                                break;
                            case "tracerentity":
                            case "te":
                                Value<Boolean> tracerEntity = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_tracer_entity");
                                if (arguments.length >= 3) {
                                    if (arguments[2].equalsIgnoreCase("-d")) {
                                        tracerEntity.setValue(tracerEntity.getDefault());
                                    } else {
                                        tracerEntity.setValue(Boolean.parseBoolean(arguments[2]));
                                    }
                                } else {
                                    tracerEntity.setValue(!tracerEntity.getValue());
                                }
                                ChatLogger.print(String.format("Render mods will %s start from tracer entity.", tracerEntity.getValue() ? "no longer" : "now"));
                                break;
                            case "nametagopacity":
                            case "nto":
                                Value<Double> nametagOpacity = (Value<Double>) XIV.getInstance().getValueManager().find("render_nametag_opacity");
                                if (arguments.length >= 3) {
                                    String newNametagOpacityString = arguments[2];
                                    try {
                                        if (arguments[2].equalsIgnoreCase("-d")) {
                                            nametagOpacity.setValue(nametagOpacity.getDefault());
                                        } else {
                                            double newNametagOpacity = Double.parseDouble(newNametagOpacityString);
                                            if (newNametagOpacity > 1.0F) {
                                                newNametagOpacity = 1.0F;
                                            } else if (newNametagOpacity < 0.1F) {
                                                newNametagOpacity = 0.1F;
                                            }
                                            nametagOpacity.setValue(newNametagOpacity);
                                        }
                                        ChatLogger.print(String.format("Render mod nametag opacity set to %s", nametagOpacity.getValue()));
                                    } catch (NumberFormatException e) {
                                        ChatLogger.print(String.format("\"%s\" is not a number.", newNametagOpacityString));
                                    }
                                } else {
                                    ChatLogger.print("Invalid arguments, valid: render nametagopacity <number>");
                                }
                                break;
                            case "nametagsize":
                            case "nts":
                                Value<Double> nametagSize = (Value<Double>) XIV.getInstance().getValueManager().find("render_nametag_size");
                                if (arguments.length >= 3) {
                                    String newNametagSizeString = arguments[2];
                                    try {
                                        if (arguments[2].equalsIgnoreCase("-d")) {
                                            nametagSize.setValue(nametagSize.getDefault());
                                        } else {
                                            double newNametagSize = Double.parseDouble(newNametagSizeString);
                                            if (newNametagSize > 10.0F) {
                                                newNametagSize = 10.0F;
                                            } else if (newNametagSize < 0.1F) {
                                                newNametagSize = 0.1F;
                                            }
                                            nametagSize.setValue(newNametagSize);
                                        }
                                        ChatLogger.print(String.format("Render mod nametag size set to %s", nametagSize.getValue()));
                                    } catch (NumberFormatException e) {
                                        ChatLogger.print(String.format("\"%s\" is not a number.", newNametagSizeString));
                                    }
                                } else {
                                    ChatLogger.print("Invalid arguments, valid: render nametagsize <number>");
                                }
                                break;
                            default:
                                ChatLogger.print("Invalid action, valid: linewidth, antialiasing, worldbobbing, nametagsize, nametagopacity, tracerentity");
                                break;
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: render <action>");
                    }
                }).build();
        Command.newCommand()
                .cmd("say")
                .description("Makes you send a chat message.")
                .arguments("<message>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length > 1) {
                        mc.thePlayer.sendChatMessage(message.substring(arguments[0].length() + 1, message.length()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: say <message>");
                    }
                }).build();
        Command.newCommand()
                .cmd("echo")
                .description("Makes a client message appear.")
                .arguments("<message>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length > 1) {
                        ChatLogger.print(message.substring(arguments[0].length() + 1, message.length()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: echo <message>");
                    }
                }).build();
        Command.newCommand()
                .cmd("clearchat")
                .aliases("cc")
                .description("Clear your chat.")
                .arguments("<action>")
                .handler(message -> {
                    mc.ingameGUI.getChatGUI().clearChatMessages();
                }).build();
        Command.newCommand()
                .cmd("potion")
                .aliases("pot")
                .description("Enhance your potions.")
                .arguments("<effect> <level>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (mc.thePlayer.getHeldItem() == null || mc.thePlayer.getHeldItem().getItem() != Items.potionitem) {
                        ChatLogger.print("You must be holding a potion to enhance potions.");
                    } else {
                        if (!mc.thePlayer.capabilities.isCreativeMode) {
                            ChatLogger.print("You must be in creative mode to enhance potions.");
                        } else {
                            try {
                                if (arguments[1].equalsIgnoreCase("clear")) {
                                    mc.thePlayer.getHeldItem().setTagCompound(new NBTTagCompound());
                                } else {
                                    int level = Integer.parseInt(arguments[2]);
                                    int duration = Integer.parseInt(arguments[3]);

                                    if (mc.thePlayer.getHeldItem().getTagCompound() == null) {
                                        mc.thePlayer.getHeldItem().setTagCompound(new NBTTagCompound());
                                    }

                                    if (!mc.thePlayer.getHeldItem().getTagCompound().hasKey("CustomPotionEffects", 9)) {
                                        mc.thePlayer.getHeldItem().getTagCompound().setTag("CustomPotionEffects", new NBTTagList());
                                    }

                                    if (arguments[1].equalsIgnoreCase("*")) {
                                        for (Potion potion : Potion.potionTypes) {
                                            if (potion != null) {
                                                NBTTagList list = mc.thePlayer.getHeldItem().getTagCompound().getTagList("CustomPotionEffects", 10);
                                                NBTTagCompound tag = new NBTTagCompound();
                                                tag.setByte("Id", (byte) potion.getId());
                                                tag.setByte("Amplifier", (byte) level);
                                                tag.setInteger("Duration", duration);
                                                list.appendTag(tag);
                                            }
                                        }

                                        ChatLogger.print("Enchanted your current item with every enchantment.");
                                    } else {
                                        Potion potion = null;

                                        for (Potion pot : Potion.potionTypes) {
                                            if (pot != null) {
                                                String name = I18n.format(pot.getName(), new Object[0]).replaceAll(" ", "");

                                                System.out.println(name);

                                                if (name.equalsIgnoreCase(arguments[1])) {
                                                    potion = pot;
                                                }
                                            }
                                        }

                                        if (potion != null) {
                                            NBTTagList list = mc.thePlayer.getHeldItem().getTagCompound().getTagList("CustomPotionEffects", 10);
                                            NBTTagCompound tag = new NBTTagCompound();
                                            tag.setByte("Id", (byte) potion.getId());
                                            tag.setByte("Amplifier", (byte) level);
                                            tag.setInteger("Duration", duration);
                                            list.appendTag(tag);

                                            ChatLogger.print(String.format("Enhanced your current potion with %s.", I18n.format(potion.getName())));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ChatLogger.print("Invalid arguments, valid: potion <effect> <level> <duration>");
                            }
                        }
                    }
                }).build();
        Command.newCommand()
                .cmd("rename")
                .aliases("ren")
                .description("Rename your items.")
                .arguments("<name>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (mc.thePlayer.getHeldItem() == null) {
                        ChatLogger.print("You must be holding an item to rename.");
                    } else {
                        if (!mc.thePlayer.capabilities.isCreativeMode) {
                            ChatLogger.print("You must be in creative mode to rename.");
                        } else {
                            if (arguments.length > 1) {
                                String name = message.substring(arguments[0].length() + 1, message.length()).replaceAll("&", "§");

                                mc.thePlayer.getHeldItem().setStackDisplayName(name);

                                if (name.equalsIgnoreCase("clear")) {
                                    mc.thePlayer.getHeldItem().clearCustomName();
                                }

                                ChatLogger.print("Renamed current item to: " + mc.thePlayer.getHeldItem().getDisplayName() + "§r.");
                            } else {
                                ChatLogger.print("Invalid arguments, valid: rename <name>");
                            }
                        }
                    }
                }).build();
        Command.newCommand()
                .cmd("enchant")
                .aliases("enc")
                .description("Enchante your items.")
                .arguments("<enchantment> <level>")
                .handler(message -> {
                    String[] arguments = message.split(" ");

                    if (mc.thePlayer.getHeldItem() == null) {
                        ChatLogger.print("You must be holding an item to enchant.");
                    } else {
                        if (!mc.thePlayer.capabilities.isCreativeMode) {
                            ChatLogger.print("You must be in creative mode to enchant.");
                        } else {
                            try {
                                try {
                                    if (arguments[1].equalsIgnoreCase("clear")) {
                                        mc.thePlayer.getHeldItem().setTagCompound(new NBTTagCompound());
                                    } else {
                                        int level = Integer.parseInt(arguments[2]);

                                        if(level > 127) {
                                            level = 127;
                                        }else if(level < -127) {
                                            level = -127;
                                        }

                                        if (arguments[1].equalsIgnoreCase("*")) {
                                            for (Enchantment enchant : Enchantment.enchantmentsList) {
                                                mc.thePlayer.getHeldItem().addEnchantment(enchant, level);

                                                System.out.println(level);
                                            }

                                            ChatLogger.print("Enchanted your current item with every enchantment.");
                                        } else {
                                            Enchantment enchant = null;

                                            for (Enchantment enc : Enchantment.enchantmentsList) {
                                                String name = StatCollector.translateToLocal(enc.getName()).replaceAll(" ", "");

                                                if (name.equalsIgnoreCase(arguments[1])) {
                                                    enchant = enc;
                                                }
                                            }

                                            if (enchant != null) {
                                                mc.thePlayer.getHeldItem().addEnchantment(enchant, level);

                                                System.out.println(level);

                                                ChatLogger.print(String.format("Enchanted your current item with %s.", StatCollector.translateToLocal(enchant.getName())));
                                            }
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    ChatLogger.print(String.format("\"%s\" is not a number.", arguments[2]));
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                ChatLogger.print("Invalid arguments, valid: enchant <enchantment> <level>");
                            }
                        }
                    }
                }).build();
        Command.newCommand()
                .cmd("copyip")
                .aliases("ci")
                .description("Copy the servers IP.")
                .handler(message -> {
                    if (mc.isSingleplayer()) {
                        ChatLogger.print("You're in singleplayer.");
                    } else {
                        StringSelection contents = new StringSelection(mc.getCurrentServerData().serverIP);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(contents, null);

                        ChatLogger.print("Copied the servers IP to clipboard.");
                    }
                }).build();
        Command.newCommand()
                .cmd("copycoords")
                .aliases("cc")
                .description("Copy your current coordinates.")
                .handler(message -> {
                    if (mc.isSingleplayer()) {
                        ChatLogger.print("You're in singleplayer.");
                    } else {
                        StringSelection contents = new StringSelection((int) mc.thePlayer.posX + " " + (int) mc.thePlayer.posY + " " + (int) mc.thePlayer.posZ);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(contents, null);

                        ChatLogger.print("Copied your current coordinates to clipboard.");
                    }
                }).build();

        XIV.getInstance().getListenerManager().add(new Listener<SendPacketEvent>() {
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C01PacketChatMessage) {
                    C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
                    event.setCancelled(parseCommand(packet.getMessage()));
                }
            }
        });

        XIV.getInstance().getListenerManager().add(new Listener<WorldBobbingEvent>() {
            public void onEventCalled(WorldBobbingEvent event) {
                Value<Boolean> worldBobbing = (Value<Boolean>) XIV.getInstance().getValueManager().find("render_world_bobbing");
                if (!Objects.isNull(worldBobbing)) {
                    event.setCancelled(!worldBobbing.getValue());
                }
            }
        });
        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public boolean parseCommand(String message) {
        String[] spaceSplit = message.split(" ");
        if (spaceSplit[0].startsWith(prefix)) {
            for (Command command : contents) {
                if (spaceSplit[0].equalsIgnoreCase(prefix + command.getCmd())) {
                    command.getHandler().onCommandRan(message);
                    return true;
                }

                if (!Objects.isNull(command.getAliases())) {
                    for (String alias : command.getAliases()) {
                        if (spaceSplit[0].equalsIgnoreCase(prefix + alias)) {
                            command.getHandler().onCommandRan(message);
                            return true;
                        }
                    }
                }
            }
            ChatLogger.print(String.format("Invalid command \"%s\"", spaceSplit[0]));
            return true;
        }
        return false;
    }

    public Command find(Class clazz) {
        for (Command cmd : getContents()) {
            if (cmd.getHandler().getClass().equals(clazz)) {
                return cmd;
            }
        }

        return null;
    }

    public Command find(String name) {
        for (Command cmd : getContents()) {
            if (cmd.getCmd().equals(name)) {
                return cmd;
            }
        }

        return null;
    }
}
