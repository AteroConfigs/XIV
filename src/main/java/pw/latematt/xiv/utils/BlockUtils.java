package pw.latematt.xiv.utils;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Objects;

/**
 * @author Matthew
 */
public class BlockUtils {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static int getBestTool(BlockPos pos) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        int slot = 0;
        float dmg = 0.1F;
        for (int index = 36; index < 45; index++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer
                    .getSlot(index).getStack();
            if (itemStack != null
                    && block != null
                    && itemStack.getItem().getStrVsBlock(itemStack, block) > dmg) {
                slot = index - 36;
                dmg = itemStack.getItem().getStrVsBlock(itemStack, block);
            }
        }
        if (dmg > 0.1F)
            return slot;
        return mc.thePlayer.inventory.currentItem;
    }

    // this is darkmagician's. credits to him.
    public static boolean isInLiquid(Entity entity) {
        if (entity == null)
            return false;
        boolean inLiquid = false;
        final int y = (int) entity.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double(entity.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(entity.getEntityBoundingBox().minZ); z < MathHelper.floor_double(entity.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid))
                        return false;
                    inLiquid = true;
                }
            }
        }
        return inLiquid || mc.thePlayer.isInWater();
    }

    // this method is N3xuz_DK's I believe. credits to him.
    public static boolean isOnLiquid(Entity entity) {
        if (entity == null)
            return false;
        boolean onLiquid = false;
        final int y = (int) entity.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
        for (int x = MathHelper.floor_double(entity.getEntityBoundingBox().minX); x < MathHelper.floor_double(entity.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(entity.getEntityBoundingBox().minZ); z < MathHelper.floor_double(entity.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid))
                        return false;
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static boolean isOnIce(Entity entity) {
        if (entity == null)
            return false;
        boolean onLiquid = false;
        final int y = (int) entity.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
        for (int x = MathHelper.floor_double(entity.getEntityBoundingBox().minX); x < MathHelper.floor_double(entity.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(entity.getEntityBoundingBox().minZ); z < MathHelper.floor_double(entity.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockIce) && !(block instanceof BlockPackedIce))
                        return false;
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public static boolean isOnLadder(Entity entity) {
        if (entity == null)
            return false;
        boolean onLadder = false;
        final int y = (int) entity.getEntityBoundingBox().offset(0.0D, 1.0D, 0.0D).minY;
        for (int x = MathHelper.floor_double(entity.getEntityBoundingBox().minX);
             x < MathHelper.floor_double(entity.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(entity.getEntityBoundingBox().minZ);
                 z < MathHelper.floor_double(entity.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block =mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (Objects.nonNull(block) && !(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLadder || block instanceof BlockVine))
                        return false;
                    onLadder = true;
                }
            }
        }
        return onLadder || mc.thePlayer.isOnLadder();
    }

    public static boolean isInsideBlock(Entity entity) {
        for(int x = MathHelper.floor_double(entity.getEntityBoundingBox().minX); x < MathHelper.floor_double(entity.getEntityBoundingBox().maxX) + 1; x++) {
            for(int y = MathHelper.floor_double(entity.getEntityBoundingBox().minY); y < MathHelper.floor_double(entity.getEntityBoundingBox().maxY) + 1; y++) {
                for(int z = MathHelper.floor_double(entity.getEntityBoundingBox().minZ); z < MathHelper.floor_double(entity.getEntityBoundingBox().maxZ) + 1; z++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if(block != null) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if(block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        }

                        if(boundingBox != null && entity.getEntityBoundingBox().intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
