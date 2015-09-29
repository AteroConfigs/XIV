package pw.latematt.xiv.mod.mods;

import net.minecraft.network.play.client.C03PacketPlayer;

import org.lwjgl.input.Keyboard;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Rederpz
 */
public class Regen extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    public Regen() {
        super("Regen", ModType.PLAYER, Keyboard.KEY_P, -1);
    }

    public void onEventCalled(MotionUpdateEvent event) {
        double motionX = 0, motionY = 0, motionZ = 0;

        if(mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth()) {
        	for(int i = 0; i < 200; i++) {
        		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
        	}
        }
    }

    @Override
    public void onCommandRan(String message) {

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
