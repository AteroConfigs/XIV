package pw.latematt.xiv.mod.mods;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
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
    public final Value<Boolean> commandBlocks = new Value<>("storage_esp_command_blocks", false);
    public final Value<Boolean> mobSpawners = new Value<>("storage_esp_mob_spawners", false);
    public final Value<Boolean> enchantmentTables = new Value<>("storage_esp_enchantment_tables", false);
    public final Value<Boolean> boxes = new Value<>("storage_esp_boxes", true);
    public final Value<Boolean> tracerLines = new Value<>("storage_esp_tracer_lines", false);
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
            float partialTicks = event.getPartialTicks();
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
                            drawESP(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 2.0D, partialTicks);
                        } else if (border4 == Blocks.chest) {
                            drawESP(tileChest.getBlockType(), x, y, z, x + 2.0D, y + 1.0D, z + 1.0D, partialTicks);
                        } else if (border4 == Blocks.chest) {
                            drawESP(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
                        } else if ((border != Blocks.chest) && (border2 != Blocks.chest) && (border3 != Blocks.chest) && (border4 != Blocks.chest)) {
                            drawESP(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
                        }
                    }
                } else if ((chest == Blocks.trapped_chest) && (border != Blocks.trapped_chest)) {
                    if (border2 == Blocks.trapped_chest) {
                        drawESP(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 2.0D, partialTicks);
                    } else if (border4 == Blocks.trapped_chest) {
                        drawESP(tileChest.getBlockType(), x, y, z, x + 2.0D, y + 1.0D, z + 1.0D, partialTicks);
                    } else if (border4 == Blocks.trapped_chest) {
                        drawESP(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
                    } else if ((border != Blocks.trapped_chest) && (border2 != Blocks.trapped_chest) && (border3 != Blocks.trapped_chest) && (border4 != Blocks.trapped_chest)) {
                        drawESP(tileChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
                    }
                }
            } else if (tileEntity instanceof TileEntityEnderChest && enderchests.getValue()) {
                TileEntityEnderChest tileEnderChest = (TileEntityEnderChest) tileEntity;

                drawESP(tileEnderChest.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityFurnace && furnaces.getValue()) {
                TileEntityFurnace tileFurnace = (TileEntityFurnace) tileEntity;

                drawESP(tileFurnace.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityBrewingStand && brewingStands.getValue()) {
                TileEntityBrewingStand tileBrewingStand = (TileEntityBrewingStand) tileEntity;

                drawESP(tileBrewingStand.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityHopper && hoppers.getValue()) {
                TileEntityHopper tileHopper = (TileEntityHopper) tileEntity;

                drawESP(tileHopper.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityDropper && droppers.getValue()) {
                TileEntityDropper tileDropper = (TileEntityDropper) tileEntity;

                drawESP(tileDropper.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityDispenser && dispensers.getValue()) {
                TileEntityDispenser tileDispenser = (TileEntityDispenser) tileEntity;

                drawESP(tileDispenser.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityCommandBlock && commandBlocks.getValue()) {
                TileEntityCommandBlock tileCommandBlock = (TileEntityCommandBlock) tileEntity;

                drawESP(tileCommandBlock.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityMobSpawner && mobSpawners.getValue()) {
                TileEntityMobSpawner tileMobSpawner = (TileEntityMobSpawner) tileEntity;

                drawESP(tileMobSpawner.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
            } else if (tileEntity instanceof TileEntityEnchantmentTable && enchantmentTables.getValue()) {
                TileEntityEnchantmentTable tileEnchantmentTable = (TileEntityEnchantmentTable) tileEntity;

                drawESP(tileEnchantmentTable.getBlockType(), x, y, z, x + 1.0D, y + 1.0D, z + 1.0D, partialTicks);
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

    private void drawESP(Block block, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float partialTicks) {
        float[] color = new float[]{1.0F, 1.0F, 1.0F};
        if (block == Blocks.ender_chest) {
            color = new float[]{0.4F, 0.2F, 1.0F};

            minX += 0.05;
            minZ += 0.05;

            maxX -= 0.05;
            maxY -= 0.1;
            maxZ -= 0.05;
        } else if (block == Blocks.chest) {
            color = new float[]{1.0F, 1.0F, 0.0F};

            minX += 0.05;
            minZ += 0.05;

            maxX -= 0.05;
            maxY -= 0.1;
            maxZ -= 0.05;
        } else if (block == Blocks.trapped_chest) {
            color = new float[]{1.0F, 0.6F, 0.0F};

            minX += 0.05;
            minZ += 0.05;

            maxX -= 0.05;
            maxY -= 0.1;
            maxZ -= 0.05;
        } else if (block == Blocks.brewing_stand) {
            color = new float[]{1.0F, 0.3F, 0.3F};

            minX += 0.1;
            minZ += 0.05;

            maxX -= 0.05;
            maxY -= 0.85;
            maxZ -= 0.05;
        } else if (block == Blocks.furnace) {
            color = new float[]{0.6F, 0.6F, 0.6F};
        } else if (block == Blocks.lit_furnace) {
            color = new float[]{0.2F, 0.6F, 0.6F};
        } else if ((block == Blocks.dropper) || (block == Blocks.dispenser)) {
            color = new float[]{0.4F, 0.4F, 0.4F};
        } else if ((block == Blocks.hopper)) {
            color = new float[]{0.4F, 0.4F, 0.4F};

            minY += 0.625;
        } else if ((block == Blocks.mob_spawner)) {
            color = new float[]{1.0F, 0.2F, 0.2F};
        } else if ((block == Blocks.command_block)) {
            color = new float[]{0.0F, 0.9F, 0.5F};
        } else if ((block == Blocks.enchanting_table)) {
            color = new float[]{0.4F, 0.0F, 1.0F};

            maxY -= 0.25;
        }

        AxisAlignedBB bb = AxisAlignedBB.fromBounds(minX, minY, minZ, maxX, maxY, maxZ);
        if (boxes.getValue()) {
            GlStateManager.color(color[0], color[1], color[2], 0.6F);
            RenderUtils.drawLines(bb);
            RenderGlobal.drawOutlinedBoundingBox(bb, -1);
            GlStateManager.color(color[0], color[1], color[2], 0.11F);
            RenderUtils.drawFilledBox(bb);
        }

        if (tracerLines.getValue()) {
            GlStateManager.color(color[0], color[1], color[2], 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            mc.entityRenderer.orientCamera(partialTicks);

            Tessellator var2 = Tessellator.getInstance();
            WorldRenderer var3 = var2.getWorldRenderer();
            var3.startDrawing(2);
            var3.addVertex(0, mc.thePlayer.getEyeHeight(), 0);
            var3.addVertex(maxX - ((maxX - minX) / 2), maxY - ((maxY - minY) / 2), maxZ - ((maxZ - minZ) / 2));
            var2.draw();

            GlStateManager.popMatrix();
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
