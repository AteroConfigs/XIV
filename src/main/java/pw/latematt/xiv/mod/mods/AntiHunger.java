package pw.latematt.xiv.mod.mods;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.util.Objects;

/**
 * @author Jack
 */

public final class AntiHunger extends Mod implements Listener<MotionUpdateEvent> {
    public AntiHunger() {
        super("AntiHunger", ModType.PLAYER, Keyboard.KEY_NONE, 65407);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            if (Objects.equals(event.getY(), mc.thePlayer.lastTickPosY) && !mc.playerController.isHittingBlock) {
                event.setOnGround(false);
            }
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
