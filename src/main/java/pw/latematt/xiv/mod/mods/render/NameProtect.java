package pw.latematt.xiv.mod.mods.render;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.MouseClickEvent;
import pw.latematt.xiv.event.events.RenderEntityEvent;
import pw.latematt.xiv.event.events.RenderStringEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.Timer;
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
    private final Value<Boolean> middleClickFriends = new Value<>("nameprotect_middle_click_friends", true);

    private final Listener mouseClickListener;

    public NameProtect() {
        super("NameProtect", ModType.RENDER);
        mouseClickListener = new Listener<MouseClickEvent>() {
            @Override
            public void onEventCalled(MouseClickEvent event) {
                if(event.getButton() == 2 && middleClickFriends.getValue() && mc.thePlayer != null) {
                    if(mc.objectMouseOver.entityHit != null) {
                        if(mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                            EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;

                            if(XIV.getInstance().getFriendManager().isFriend(player.getName())) {
                                XIV.getInstance().getFriendManager().remove(player.getName());
                                ChatLogger.print(String.format("Friend \"%s\" removed.", player.getName()));
                            }else{
                                XIV.getInstance().getFriendManager().add(player.getName(), player.getName());
                                ChatLogger.print(String.format("Friend \"\2473%s\247r\" added.", player.getName()));
                            }
                        }
                    }
                }
            }
        };

        Command.newCommand()
                .cmd("nameprotect")
                .description("Base command for NameProtect mod.")
                .arguments("<action>")
                .aliases("np")
                .handler(this)
                .build();

        this.setEnabled(true);
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
                case "middleclick" :
                case "middleclickfriends" :
                case "middle":
                case "click":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            middleClickFriends.setValue(middleClickFriends.getDefault());
                        } else {
                            middleClickFriends.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        middleClickFriends.setValue(!middleClickFriends.getValue());
                    }
                    ChatLogger.print(String.format("NameProtect will %s allow middle clicking friends.", (nametag.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: chat, nametag, scoreboard, tab, middleclickfriends");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: nameprotect <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(mouseClickListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(mouseClickListener);
    }
}
