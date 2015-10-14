package pw.latematt.xiv.mod.mods.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;

/**
 * @author Rederpz
 */
public class Regen extends Mod implements Listener<MotionUpdateEvent> {
    private boolean set = false;

    public Regen() {
        super("Regen", ModType.PLAYER, Keyboard.KEY_P, 0xFF9681D6);
    }

    /**
     * This mod is ment to speed up regen potions to make them instant, on servers that give you infinite regen (or if you have a regen beacon), it should act as a normal regen.
     * keep in for martin kays?
     */
    public void onEventCalled(MotionUpdateEvent event) {
        if (mc.thePlayer.getActivePotionEffect(Potion.REGENERATION) != null) {
            if (!set) {
                set = true;
            }

            if (mc.thePlayer.onGround || BlockUtils.isOnLadder(mc.thePlayer) || BlockUtils.isInLiquid(mc.thePlayer) || BlockUtils.isOnLiquid(mc.thePlayer)) {
                if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
                    for (int i = 0; i < mc.thePlayer.getMaxHealth() - mc.thePlayer.getHealth(); i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                }
            }
        } else {
            set = false;
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
