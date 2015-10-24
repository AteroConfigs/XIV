package pw.latematt.xiv.mod.mods.render;

import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.PotionRenderEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.EntityUtils;

import java.util.Objects;

/**
 * @author Jack
 */

public class NoPotion extends Mod implements Listener<PotionRenderEvent> {
    public NoPotion() {
        super("NoPotion", ModType.RENDER, Keyboard.KEY_NONE);
    }

    @Override
    public void onEventCalled(PotionRenderEvent event) {
        if(event.getEffect() == Potion.INVISIBILITY) {
            event.setCancelled(false);
        }else{
            event.setCancelled(true);
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
