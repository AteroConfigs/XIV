package pw.latematt.xiv.mod.mods;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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

public class Speedmine extends Mod implements Listener<MotionUpdateEvent> {
    public Speedmine() {
        super("Speedmine", ModType.WORLD, Keyboard.KEY_NONE, 0xFFE0A341); // TODO: Bind and colour
    }

    @Override
    public void onEventCalled(MotionUpdateEvent event) {
        if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
            mc.playerController.blockHitDelay = 0;
            mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 16350, 0));
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
    }
}
