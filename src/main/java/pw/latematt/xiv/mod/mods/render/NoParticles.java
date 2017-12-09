package pw.latematt.xiv.mod.mods.render;

import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.ReadPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Tasmanian
 * @since Nov 25, 2015
 */
public class NoParticles extends Mod implements Listener<ReadPacketEvent> {

    public NoParticles() {
        super("NoParticles", ModType.RENDER);
    }

    @Override
    public void onEventCalled(ReadPacketEvent event) {
        if (event.getPacket() instanceof S2APacketParticles || event.getPacket() instanceof S28PacketEffect) {
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
