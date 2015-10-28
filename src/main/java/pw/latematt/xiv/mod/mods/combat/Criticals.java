package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;

/**
 * @author Matthew
 */
public class Criticals extends Mod implements Listener<SendPacketEvent> {
    private final Timer timer = new Timer();

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
            KillAura killAura = (KillAura) XIV.getInstance().getModManager().find("killaura");
            if (killAura == null || !killAura.isAttacking())
                return;
            if (!timer.hasReached(killAura.getDelay()))
                return;

            if (canStrikeCriticals() && mc.thePlayer.onGround) {
                player.setY(player.getY() + 0.0625101);
                player.setOnGround(false);
                mc.getNetHandler().getNetworkManager().sendPacket(player);
                player.setY(player.getY() - 0.0625101);
                timer.reset();
            }
        }
    }

    private boolean canStrikeCriticals() {
        return !mc.thePlayer.isInWater() &&
                !mc.thePlayer.isInsideOfMaterial(Material.lava) &&
                !mc.thePlayer.isOnLadder() &&
                !mc.thePlayer.isPotionActive(Potion.BLINDNESS) &&
                mc.thePlayer.ridingEntity == null;
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
