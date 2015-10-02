package pw.latematt.xiv.mod.mods.aura;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.event.events.MotionUpdateEvent;

/**
 * @author Matthew
 */
public abstract class AuraMode {
    private String name;
    protected final Minecraft mc = Minecraft.getMinecraft();

    public AuraMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void onPreMotionUpdate(MotionUpdateEvent event);

    public abstract void onPostMotionUpdate(MotionUpdateEvent event);

    public abstract void onMotionPacket(C03PacketPlayer packet);

    public abstract boolean isAttacking();

    public abstract void onDisabled();
}
