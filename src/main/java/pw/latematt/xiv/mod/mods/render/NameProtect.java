package pw.latematt.xiv.mod.mods.render;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.RenderStringEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Matthew
 */
public class NameProtect extends Mod implements Listener<RenderStringEvent>, CommandHandler {
    private final Value<Boolean> tab = new Value<>("nameprotect_tab", true);
    private final Value<Boolean> scoreboard = new Value<>("nameprotect_scoreboard", true);
    private final Value<Boolean> nametag = new Value<>("nameprotect_nametag", true);
    private final Value<Boolean> chat = new Value<>("nameprotect_chat", true);

    public NameProtect() {
        super("NameProtect", ModType.RENDER);
        this.setEnabled(true);

        Command.newCommand()
                .cmd("nameprotect")
                .description("Base command for NameProtect mod.")
                .arguments("<action>")
                .aliases("np")
                .handler(this)
                .build();
    }

    @Override
    public void onEventCalled(RenderStringEvent event) {
        if (Objects.equals(event.getState(), RenderStringEvent.State.CHAT) && !chat.getValue())
            return;
        if (Objects.equals(event.getState(), RenderStringEvent.State.TAB) && !tab.getValue())
            return;
        if (Objects.equals(event.getState(), RenderStringEvent.State.SCOREBOARD) && !scoreboard.getValue())
            return;
        if (Objects.equals(event.getState(), RenderStringEvent.State.NAMETAG) && !nametag.getValue())
            return;
        event.setString(XIV.getInstance().getFriendManager().replace(event.getString(), !Objects.equals(event.getState(), RenderStringEvent.State.NAMETAG)));
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "tab":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            tab.setValue(tab.getDefault());
                        } else {
                            tab.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        tab.setValue(!tab.getValue());
                    }
                    ChatLogger.print(String.format("NameProtect will %s protect in tab.", (tab.getValue() ? "now" : "no longer")));
                    break;
                case "chat":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            chat.setValue(chat.getDefault());
                        } else {
                            chat.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        chat.setValue(!chat.getValue());
                    }
                    ChatLogger.print(String.format("NameProtect will %s protect in chat.", (chat.getValue() ? "now" : "no longer")));
                    break;
                case "nametag":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            nametag.setValue(nametag.getDefault());
                        } else {
                            nametag.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        nametag.setValue(!nametag.getValue());
                    }
                    ChatLogger.print(String.format("NameProtect will %s protect in nametag.", (nametag.getValue() ? "now" : "no longer")));
                    break;
                case "scoreboard":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            scoreboard.setValue(scoreboard.getDefault());
                        } else {
                            scoreboard.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        scoreboard.setValue(!scoreboard.getValue());
                    }
                    ChatLogger.print(String.format("NameProtect will %s protect in scoreboard.", (scoreboard.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: chat, nametag, scoreboard, tab");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: nameprotect <action>");
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
