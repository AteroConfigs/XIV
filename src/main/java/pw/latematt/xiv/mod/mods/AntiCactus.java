package pw.latematt.xiv.mod.mods;

import net.minecraft.block.BlockCactus;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BlockAddBBEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author Matthew
 */
public class AntiCactus extends Mod implements Listener<BlockAddBBEvent> {
    public AntiCactus() {
        super("AntiCactus", ModType.WORLD);
    }

    @Override
    public void onEventCalled(BlockAddBBEvent event) {
        if (event.getBlock() instanceof BlockCactus) {
            BlockPos pos = event.getPos();
            event.setAxisAlignedBB(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
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
