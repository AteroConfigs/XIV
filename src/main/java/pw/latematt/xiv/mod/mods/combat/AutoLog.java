package pw.latematt.xiv.mod.mods.combat;

import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.value.Value;

import java.util.Objects;

/**
 * @author Jack
 */

public class AutoLog extends Mod implements Listener<MotionUpdateEvent> {
    private final Value<Float> health = new Value<>("autolog_health", 6.0F);

    public AutoLog() {
        super("AutoLog", ModType.COMBAT, Keyboard.KEY_NONE);
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
            if (mc.thePlayer.getHealth() <= this.health.getValue()) {
                mc.playerController.attackEntity(mc.thePlayer, mc.thePlayer);
                this.toggle();
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
