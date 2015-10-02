package pw.latematt.xiv.management.managers;

import net.minecraft.util.StringUtils;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.management.MapManager;
import pw.latematt.xiv.utils.ChatLogger;

import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * @author Matthew
 */
public class FriendManager extends MapManager<String, String> {
    public FriendManager() {
        super(new HashMap<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + "...");
        Command.newCommand()
                .cmd("friend")
                .description("Manages a player's friend status so the client doesn't target him.")
                .aliases("fr")
                .arguments("<action>")
                .handler(message -> {
                    String[] arguments = message.split(" ");
                    if (arguments.length >= 2) {
                        String action = arguments[1];
                        switch (action.toLowerCase()) {
                            case "add":
                            case "a":
                                if (arguments.length == 3) {
                                    String mcname = arguments[2];
                                    XIV.getInstance().getFriendManager().add(mcname, mcname);
                                    XIV.getInstance().getFileManager().saveFile("friends");
                                    ChatLogger.print(String.format("Friend \"\2473%s\247r\" added.", mcname));
                                } else if (arguments.length >= 4) {
                                    String mcname = arguments[2];
                                    String alias = arguments[3];
                                    XIV.getInstance().getFriendManager().add(mcname, alias);
                                    XIV.getInstance().getFileManager().saveFile("friends");
                                    ChatLogger.print(String.format("Friend \"\2473%s\247r\" added.", alias));
                                } else {
                                    ChatLogger.print("Invalid arguments, valid: friend add <mcname> [alias]");
                                }
                                break;
                            case "del":
                                if (arguments.length >= 3) {
                                    String mcname = arguments[2];
                                    XIV.getInstance().getFriendManager().remove(mcname);
                                    XIV.getInstance().getFileManager().saveFile("friends");
                                    ChatLogger.print(String.format("Friend \"%s\" removed.", mcname));
                                } else {
                                    ChatLogger.print("Invalid arguments, valid: friend del <mcname>");
                                }
                                break;
                            default:
                                ChatLogger.print("Invalid action, valid: add, del");
                                break;
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: friend <action>");
                    }
                }).build();
        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public void add(String mcname, String alias) {
        contents.put(mcname, alias);
    }

    public void remove(String mcname) {
        contents.remove(mcname);
    }

    public String replace(String string, boolean colored) {
        for (String mcname : contents.keySet()) {
            String alias = contents.get(mcname);
            if (colored) {
                alias = String.format("\2473%s\247r", alias);
            }
            string = string.replaceAll("(?i)" + Matcher.quoteReplacement(mcname), Matcher.quoteReplacement(alias));
        }
        return string;
    }

    public boolean isFriend(String mcname) {
        return contents.containsKey(StringUtils.stripControlCodes(mcname));
    }
}
