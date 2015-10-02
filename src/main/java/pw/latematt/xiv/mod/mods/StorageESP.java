package pw.latematt.xiv.mod.mods;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class StorageESP extends Mod implements Listener<Render3DEvent> {
    public final Value<Boolean> chests = new Value<>("storage_esp_chests", true);
    public final Value<Boolean> enderchests = new Value<>("storage_esp_ender_chests", false);
    public final Value<Boolean> furnaces = new Value<>("storage_esp_furnaces", true);
    public final Value<Boolean> brewingStands = new Value<>("storage_esp_brewing_stands", false);
    public final Value<Boolean> hoppers = new Value<>("storage_esp_hoppers", false);
    public final Value<Boolean> droppers = new Value<>("storage_esp_droppers", false);
    public final Value<Boolean> dispensers = new Value<>("storage_esp_dispensers", false);
    public final Value<Float> lineWidth = new Value<>("storage_esp_line_width", 1.0F);

    public StorageESP() {
        super("StorageESP", ModType.RENDER);
    }

    @Override
    public void onEventCalled(Render3DEvent event) {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.func_179090_x();
        GL11.glLineWidth(lineWidth.getValue());
        for (Object o : mc.theWorld.loadedTileEntityList) {
            TileEntity tileEntity = (TileEntity) o;
            final double x = tileEntity.getPos().getX() - mc.getRenderManager().renderPosX;
            final double y = tileEntity.getPos().getY() - mc.getRenderManager().renderPosY;
            final double z = tileEntity.getPos().getZ() - mc.getRenderManager().renderPosZ;

            if (tileEntity instanceof TileEntityChest && chests.getValue()) {
                TileEntityChest tileChest = (TileEntityChest) tileEntity;
                Block chest = mc.theWorld.getBlockState(tileChest.getPos()).getBlock();

                Block border = mc.theWorld.getBlockState(new BlockPos(tileChest.getPos().getX(), tileChest.getPos().getY(), tileChest.getPos().getZ() - 1)).getBlock();
                Block border2 = mc.theWorld.getBlockState(new BlockPos(tileChest.getPos().getX(), tileChest.getPos().getY(), tileChest.getPos().getZ() + 1)).getBlock();
                Block border3 = mc.theWorld.getBlockState(new BlockPos(tileChest.getPos().getX() - 1, tileChest.getPos().getY(), tileChest.getPos().getZ())).getBlock();
                Block border4 = mc.theWorld.getBlockState(new BlockPos(tileChest.getPos().getX() + 1, tileChest.getPos().getY(), tileChest.getPos().getZ())).getBlock();
                if (chest == Blocks.chest) {
                    if (border != Blocks.chest) {
                        if (border2 == Blocks.chest) {
                            drawBox(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 2.0D);
                        } else if (border4 == Blocks.chest) {
                            drawBox(tileChest.getBlockType(), x, y, z, x + 2.0D, y + 1.0D, z + 1.0D);
                        } else if (border4 == Blocks.chest) {
                            drawBox(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                        } else if ((border != Blocks.chest) && (border2 != Blocks.chest) && (border3 != Blocks.chest) && (border4 != Blocks.chest)) {
                            drawBox(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                        }
                    }
                } else if ((chest == Blocks.trapped_chest) && (border != Blocks.trapped_chest)) {
                    if (border2 == Blocks.trapped_chest) {
                        drawBox(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 2.0D);
                    } else if (border4 == Blocks.trapped_chest) {
                        drawBox(tileChest.getBlockType(), x, y, z, x + 2.0D, y + 1.0D, z + 1.0D);
                    } else if (border4 == Blocks.trapped_chest) {
                        drawBox(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                    } else if ((border != Blocks.trapped_chest) && (border2 != Blocks.trapped_chest) && (border3 != Blocks.trapped_chest) && (border4 != Blocks.trapped_chest)) {
                        drawBox(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                    }
                }
            } else if (tileEntity instanceof TileEntityEnderChest && enderchests.getValue()) {
                TileEntityEnderChest tileEnderChest = (TileEntityEnderChest) tileEntity;
                drawBox(tileEnderChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            } else if (tileEntity instanceof TileEntityFurnace && furnaces.getValue()) {
                TileEntityFurnace tileFurnace = (TileEntityFurnace) tileEntity;
                drawBox(tileFurnace.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            } else if (tileEntity instanceof TileEntityBrewingStand && brewingStands.getValue()) {
                TileEntityBrewingStand tileBrewingStand = (TileEntityBrewingStand) tileEntity;
                drawBox(tileBrewingStand.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            } else if (tileEntity instanceof TileEntityHopper && hoppers.getValue()) {
                TileEntityHopper tileHopper = (TileEntityHopper) tileEntity;
                drawBox(tileHopper.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            } else if (tileEntity instanceof TileEntityDropper && droppers.getValue()) {
                TileEntityDropper tileDropper = (TileEntityDropper) tileEntity;
                drawBox(tileDropper.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            } else if (tileEntity instanceof TileEntityDispenser && dispensers.getValue()) {
                TileEntityDispenser tileDispenser = (TileEntityDispenser) tileEntity;
                drawBox(tileDispenser.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            }
        }
        GL11.glLineWidth(2.0F);
        GlStateManager.func_179098_w();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void drawBox(Block block, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        float[] color = new float[]{1.0F, 1.0F, 1.0F};
        if (block == Blocks.ender_chest) {
            color = new float[]{0.4F, 0.2F, 1.0F};

            minX += 0.0500000007450581D;
            minZ += 0.0500000007450581D;

            maxX -= 0.1000000014901161D;
            maxY -= 0.1000000014901161D;
            maxZ -= 0.1000000014901161D;
        } else if (block == Blocks.chest) {
            color = new float[]{1.0F, 1.0F, 0.0F};

            minX += 0.0500000007450581D;
            minZ += 0.0500000007450581D;

            maxX -= 0.1000000014901161D;
            maxY -= 0.1000000014901161D;
            maxZ -= 0.1000000014901161D;
        } else if (block == Blocks.trapped_chest) {
            color = new float[]{1.0F, 0.6F, 0.0F};

            minX += 0.0500000007450581D;
            minZ += 0.0500000007450581D;

            maxX -= 0.1000000014901161D;
            maxY -= 0.1000000014901161D;
            maxZ -= 0.1000000014901161D;
        } else if (block == Blocks.brewing_stand) {
            color = new float[]{1.0F, 0.3F, 0.3F};

            minX += 0.0500000007450581D;
            minZ += 0.0500000007450581D;

            maxX -= 0.1000000014901161D;
            maxY -= 0.1000000014901161D;
            maxZ -= 0.1000000014901161D;
        } else if (block == Blocks.furnace) {
            color = new float[]{0.6F, 0.6F, 0.6F};
        } else if (block == Blocks.lit_furnace) {
            color = new float[]{0.4F, 0.2F, 1.0F};
        } else if ((block == Blocks.hopper) || (block == Blocks.dispenser)
                || (block == Blocks.dropper)) {
            color = new float[]{0.3F, 0.3F, 0.3F};
        }

        AxisAlignedBB bb = AxisAlignedBB.fromBounds(minX, minY, minZ, maxX, maxY, maxZ);
        GlStateManager.color(color[0], color[1], color[2], 0.6F);
        RenderUtils.drawLines(bb);
        RenderGlobal.drawOutlinedBoundingBox(bb, -1);
        GlStateManager.color(color[0], color[1], color[2], 0.11F);
        RenderUtils.drawFilledBox(bb);
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
