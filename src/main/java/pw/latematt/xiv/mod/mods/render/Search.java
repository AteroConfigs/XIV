package pw.latematt.xiv.mod.mods.render;

import com.sun.javafx.geom.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BlockModelRenderEvent;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthew
 */
public class Search extends Mod implements Listener<Render3DEvent>,CommandHandler {
    private final Listener blockModelRenderListener;
    private final List<Block> blockList = new ArrayList<>();
    private final List<Vec3d> blockCache = new ArrayList<>();
    private final Value<Float> range = new Value<>("search_range", 128.0F);

    public Search() {
        super("Search", ModType.RENDER);
        blockModelRenderListener = new Listener<BlockModelRenderEvent>() {
            @Override
            public void onEventCalled(BlockModelRenderEvent event) {
                Vec3d blockPos = new Vec3d(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
                if (blockList.equals(event.getBlock()) && !isCached(blockPos)) {
                    blockCache.add(blockPos);
                }
            }
        };

        setEnabled(true);
    }

    private boolean isCached(Vec3d blockPos) {
        for (Vec3d vec3d : blockCache) {
            if (vec3d.x == blockPos.x && vec3d.y == blockPos.y && vec3d.z == blockPos.z) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEventCalled(Render3DEvent event) {
        RenderUtils.beginGl();
        for (Vec3d vec3d : blockCache) {
            if (mc.thePlayer.getDistance(vec3d.x, vec3d.y, vec3d.z) > range.getValue()) {
                blockCache.remove(vec3d);
            }
            Block block = mc.theWorld.getBlockState(new BlockPos(vec3d.x, vec3d.y, vec3d.z)).getBlock();

            renderBox(block, vec3d);
        }
        RenderUtils.endGl();
    }

    private void renderBox(Block block, Vec3d vec3d) {

    }

    @Override
    public void onCommandRan(String message) {

    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(blockModelRenderListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(blockModelRenderListener);
    }
}
