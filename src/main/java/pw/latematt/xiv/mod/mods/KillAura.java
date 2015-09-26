package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.utils.ChatLogger;

/**
 * @author Matthew
 */
public class KillAura extends Mod implements CommandHandler {
    private final Listener motionUpdateListener;
    public KillAura() {
        super("Kill Aura", Keyboard.KEY_F, 0xFFC6172B);

        Command.newCommand()
                .cmd("killaura")
                .description("Base command for the Kill Aura mod.")
                .aliases("killa", "ka")
                .arguments("<action>")
                .handler(this)
                .build();

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {

                }
            }
        };
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action) {
                default:
                    ChatLogger.print("Invalid action, valid: delay, range, players, mobs, animals, invisible, team, teamcolor, silent");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: killaura <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
    }

    @Override
    public void onDisabled() {

    }
}
