package pw.latematt.xiv.mod.mods;

import net.minecraft.network.play.client.C03PacketPlayer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.EntityStepEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class Step extends Mod implements Listener<EntityStepEvent> {
    private final Listener sendPacketListener;
    private boolean editPackets;
    private Value<Float> height = new Value<>("step_height", 1.065F);

    public Step() {
        super("Step", new String[] {"<action>", "[height]"}, new String[] {});

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    // wip (find bypass)
                }
            }
        };
    }

    @Override
    public void onEventCalled(EntityStepEvent event) {
        final boolean shouldStep = event.getEntity() == mc.thePlayer
                && mc.thePlayer.onGround && !mc.thePlayer.isInWater()
                && !mc.thePlayer.isCollidedHorizontally;
        mc.thePlayer.stepHeight = mc.thePlayer.isInWater() ? 0.50F : height.getValue();
        editPackets = shouldStep;
    }

    @Override
    public void onCommandRan(String message) {

    }

    public boolean isEditingPackets() {
        return editPackets;
    }

    public void setEditPackets(boolean editPackets) {
        this.editPackets = editPackets;
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        if (mc.thePlayer != null) {
            mc.thePlayer.stepHeight = 0.5F;
        }
    }
}
