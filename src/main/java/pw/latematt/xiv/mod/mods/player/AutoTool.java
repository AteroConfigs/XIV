package pw.latematt.xiv.mod.mods.player;

import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.event.events.ClickBlockEvent;
import pw.latematt.xiv.event.events.MouseClickEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class AutoTool extends Mod implements CommandHandler {
    private final Listener attackEntityListener, clickBlockListener, mouseClickListener;
    private final Value<Boolean> weapons = new Value<>("autotool_weapons", true);
    private final Value<Boolean> tools = new Value<>("autotool_tools", true);

    public AutoTool() {
        super("AutoTool", ModType.PLAYER, Keyboard.KEY_NONE);
        Command.newCommand().cmd("autotool").description("Base command for AutoTool mod.").arguments("<action>").aliases("autot", "atool", "at").handler(this).build();

        attackEntityListener = new Listener<AttackEntityEvent>() {
            @Override
            public void onEventCalled(AttackEntityEvent event) {
                if (!weapons.getValue())
                    return;
                if (event.getEntity() instanceof EntityLivingBase) {
                    EntityLivingBase living = (EntityLivingBase) event.getEntity();
                    mc.thePlayer.inventory.currentItem = EntityUtils.getBestWeapon(living);
                    mc.playerController.updateController();
                }
            }
        };

        clickBlockListener = new Listener<ClickBlockEvent>() {
            @Override
            public void onEventCalled(ClickBlockEvent event) {
                if (!tools.getValue())
                    return;
                mc.thePlayer.inventory.currentItem = BlockUtils.getBestTool(event.getPos());
                mc.playerController.updateController();
            }
        };

        mouseClickListener = new Listener<MouseClickEvent>() {
            @Override
            public void onEventCalled(MouseClickEvent event) {
                if (!weapons.getValue())
                    return;
                if (event.getButton() == 0) {
                    mc.thePlayer.inventory.currentItem = EntityUtils.getBestWeapon(mc.thePlayer);
                    mc.playerController.updateController();
                }
            }
        };
    }

    @Override
    public void onCommandRan(String message) {
        final String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            switch (arguments[1].toLowerCase()) {
                case "weapons":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            weapons.setValue(weapons.getDefault());
                        } else {
                            weapons.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        weapons.setValue(!weapons.getValue());
                    }
                    ChatLogger.print(String.format("AutoTool will %s automatically switch weapons when attacking entities.", (weapons.getValue() ? "now" : "no longer")));
                    break;
                case "tools":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            tools.setValue(tools.getDefault());
                        } else {
                            tools.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        tools.setValue(!tools.getValue());
                    }
                    ChatLogger.print(String.format("AutoTool will %s automatically switch tools when breaking blocks.", (tools.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: weapons, tools");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: autotool <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(attackEntityListener, clickBlockListener, mouseClickListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(attackEntityListener, clickBlockListener, mouseClickListener);
    }
}
