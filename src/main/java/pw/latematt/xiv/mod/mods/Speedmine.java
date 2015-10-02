package pw.latematt.xiv.mod.mods;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BreakingBlockEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

import java.util.Objects;

/**
 * @author Jack
 */

public class Speedmine extends Mod {
    private final Listener motionUpdateListener;
    private final Listener breakingBlockListener;
    public Speedmine() {
        super("Speedmine", ModType.WORLD, Keyboard.KEY_G, 0xFF77A24E);

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (Objects.equals(event.getCurrentState(), MotionUpdateEvent.State.PRE)) {
                    mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 16350, 0));
                }
            }
        };

        breakingBlockListener = new Listener<BreakingBlockEvent>() {
            @Override
            public void onEventCalled(BreakingBlockEvent event) {
                event.setHitDelay(0);
            }
        };
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        XIV.getInstance().getListenerManager().add(breakingBlockListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(breakingBlockListener);
        mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
    }
}
