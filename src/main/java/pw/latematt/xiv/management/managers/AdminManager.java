package pw.latematt.xiv.management.managers;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.StringUtils;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.file.XIVFile;
import pw.latematt.xiv.management.MapManager;
import pw.latematt.xiv.utils.ChatLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;

/**
 * @author Rederpz
 */
public class AdminManager extends MapManager<String, String> {
    public AdminManager() {
        super(new HashMap<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info(String.format("Starting to setup %s.", getClass().getSimpleName()));

        new XIVFile("admins", "json") {
            @Override
            public void load() throws IOException {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                HashMap<String, String> admins = gson.fromJson(reader, new TypeToken<HashMap<String, String>>() {
                }.getType());
                for (String mcname : admins.keySet()) {
                    String alias = admins.get(mcname);
                    XIV.getInstance().getAdminManager().add(mcname, alias);
                }
            }

            @Override
            public void save() throws IOException {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Files.write(gson.toJson(XIV.getInstance().getAdminManager().getContents()).getBytes("UTF-8"), file);
            }
        };

        Command.newCommand()
                .cmd("admin")
                .description("Manages a player's friend status so the client doesn't target him.")
                .aliases("enemy")
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
                                    XIV.getInstance().getAdminManager().add(mcname, mcname);
                                    XIV.getInstance().getFileManager().saveFile("admins");
                                    ChatLogger.print(String.format("Admin \"\2475%s\247r\" added.", mcname));
                                } else if (arguments.length >= 4) {
                                    String mcname = arguments[2];
                                    String alias = arguments[3];
                                    XIV.getInstance().getAdminManager().add(mcname, alias);
                                    XIV.getInstance().getFileManager().saveFile("admins");
                                    ChatLogger.print(String.format("Admin \"\2475%s\247r\" added.", alias));
                                } else {
                                    ChatLogger.print("Invalid arguments, valid: admin add <mcname> [alias]");
                                }
                                break;
                            case "r" :
                            case "remove" :
                            case "del":
                            case "d":
                                if (arguments.length >= 3) {
                                    String mcname = arguments[2];
                                    XIV.getInstance().getAdminManager().remove(mcname);
                                    XIV.getInstance().getFileManager().saveFile("admins");
                                    ChatLogger.print(String.format("Admin \"%s\" removed.", mcname));
                                } else {
                                    ChatLogger.print("Invalid arguments, valid: admin del <mcname>");
                                }
                                break;
                            default:
                                ChatLogger.print("Invalid action, valid: add, del");
                                break;
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: admin <action>");
                    }
                }).build();
        XIV.getInstance().getLogger().info(String.format("Successfully setup %s.", getClass().getSimpleName()));
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
                alias = String.format("\2475%s\247r", alias);
            }
            string = string.replaceAll("(?i)" + Matcher.quoteReplacement(mcname), Matcher.quoteReplacement(alias));
        }
        return string;
    }

    public boolean isAdmin(String mcname) {
        return contents.containsKey(StringUtils.stripControlCodes(mcname));
    }
}
