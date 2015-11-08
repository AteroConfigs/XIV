package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BlockReachEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.render.Freecam;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.RenderUtils;

/**
 * @author Klintos
 */
public class ClickTeleport extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private BlockPos teleportPosition;
    private boolean canTeleport, canDraw;
    private int delay = 0;

    private Listener blockReachListener, renderListener;

    public ClickTeleport() {
        super("Click Teleport", ModType.MOVEMENT, Keyboard.KEY_NONE, 0xFFFF697C);

        Command.newCommand().cmd("forward").aliases("fwd").description("Teleport forward a few blocks.").arguments("<blocks>").handler(this).build();

        blockReachListener = new Listener<BlockReachEvent>() {
            @Override
            public void onEventCalled(BlockReachEvent event) {
                if (!Mouse.isButtonDown(0) && !mc.thePlayer.isSneaking() && mc.inGameHasFocus) {
                    event.setRange(35.0F);

                    canDraw = true;
                } else {
                    canDraw = false;
                }
            }
        };

        renderListener = new Listener<Render3DEvent>() {
            @Override
            public void onEventCalled(Render3DEvent event) {
                if (mc.objectMouseOver != null && mc.objectMouseOver.func_178782_a() != null && canDraw) {
                    double[] mouseOverPos = new double[]{mc.objectMouseOver.func_178782_a().getX(), mc.objectMouseOver.func_178782_a().getY() + 1.0F, mc.objectMouseOver.func_178782_a().getZ()};

                    if (canRenderBox(mouseOverPos)) {
                        Block blockBelowPos = mc.theWorld.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0F, mouseOverPos[2])).getBlock();

                        RenderUtils.beginGl();
                        renderBox(blockBelowPos, new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2]));
                        RenderUtils.endGl();

                        canTeleport = true;
                    } else {
                        mouseOverPos = new double[]{mc.objectMouseOver.func_178782_a().getX(), mc.objectMouseOver.func_178782_a().getY(), mc.objectMouseOver.func_178782_a().getZ()};

                        if (canRenderBox(mouseOverPos)) {
                            Block blockBelowPos = mc.theWorld.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0F, mouseOverPos[2])).getBlock();

                            RenderUtils.beginGl();
                            renderBox(blockBelowPos, new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2]));
                            RenderUtils.endGl();

                            canTeleport = true;
                        } else {
                            canTeleport = false;
                        }
                    }
                } else {
                    canTeleport = false;
                }
            }
        };
    }

    public boolean canRenderBox(double[] mouseOverPos) {
        boolean canTeleport = false;

        Block blockBelowPos = mc.theWorld.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0F, mouseOverPos[2])).getBlock();
        Block blockPos = mc.theWorld.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] + 1.0F, mouseOverPos[2])).getBlock();
        Block blockAbovePos = mc.theWorld.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] + 2.0F, mouseOverPos[2])).getBlock();

        boolean validBlockBelow = !isValidBlock(blockBelowPos) && blockBelowPos.getMaterial().isSolid();
        boolean validBlock = isValidBlock(blockPos);
        boolean validBlockAbove = isValidBlock(blockAbovePos);

        if (mc.theWorld.getBlockState(mc.objectMouseOver.func_178782_a()).getBlock().getMaterial() != Material.air && (validBlockBelow && validBlock && validBlockAbove)) {
            canTeleport = true;
        }

        return canTeleport;
    }

    public boolean isValidBlock(Block block) {
        return block instanceof BlockSign || block instanceof BlockAir || block instanceof BlockPressurePlate || !block.getMaterial().isSolid();

    }

    private void renderBox(Block block, BlockPos pos) {
        double x = pos.getX() - mc.getRenderManager().renderPosX;
        double y = pos.getY() - mc.getRenderManager().renderPosY;
        double z = pos.getZ() - mc.getRenderManager().renderPosZ;
        AxisAlignedBB box = AxisAlignedBB.fromBounds(x, y, z, x + 1, y + 0.06F, z + 1);
        final int color = this.getColor();
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, 0.11F);
        RenderUtils.drawFilledBox(box);
        GlStateManager.color(red, green, blue, 0.60F);
        RenderUtils.drawLines(box);
        RenderGlobal.drawOutlinedBoundingBox(box, -1);
    }

    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            if (canTeleport && delay == 0 && Mouse.isButtonDown(1)) {

                this.teleportPosition = mc.objectMouseOver.func_178782_a();

                double[] playerPosition = new double[]{EntityUtils.getReference().posX, EntityUtils.getReference().posY, EntityUtils.getReference().posZ};
                double[] blockPosition = new double[]{teleportPosition.getX() + 0.5F, teleportPosition.getY() + 1.0F, teleportPosition.getZ() + 0.5F};

                Freecam freecam = (Freecam) XIV.getInstance().getModManager().find("freecam");

                if (freecam != null && freecam.isEnabled()) {
                    freecam.setEnabled(false);
                }

                EntityUtils.teleportToPosition(playerPosition, blockPosition, 0.25D, 0.0D, true, true);
                mc.thePlayer.setPosition(blockPosition[0], blockPosition[1], blockPosition[2]);
                delay = 5;
            }

            if (delay > 0) {
                delay--;
            }
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(renderListener);
        XIV.getInstance().getListenerManager().add(blockReachListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(renderListener);
        XIV.getInstance().getListenerManager().remove(blockReachListener);
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String distanceString = arguments[1];
            try {
                double distance = Double.parseDouble(distanceString);

                if (distance > 25) {
                    distance = 25;
                } else if (distance < -25) {
                    distance = -25;
                } else if (distance == 0) {
                    distance = 5;
                }

                float dir = mc.thePlayer.rotationYaw;
                if (mc.thePlayer.moveForward < 0.0F)
                    dir += 180.0F;

                if (mc.thePlayer.moveStrafing > 0.0F)
                    dir -= 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);

                if (mc.thePlayer.moveStrafing < 0.0F)
                    dir += 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);

                float xD = (float) Math.cos((dir + 90.0F) * Math.PI / 180.0D);
                float zD = (float) Math.sin((dir + 90.0F) * Math.PI / 180.0D);

                double[] playerPosition = new double[]{EntityUtils.getReference().posX, EntityUtils.getReference().posY, EntityUtils.getReference().posZ};
                this.teleportPosition = new BlockPos(playerPosition[0] + (xD * distance), playerPosition[1], playerPosition[2] + (zD * distance));

                double[] blockPosition = new double[]{teleportPosition.getX() + 0.5F, teleportPosition.getY(), teleportPosition.getZ() + 0.5F};

                EntityUtils.teleportToPosition(playerPosition, blockPosition, 0.25D, 0.0D, true, true);
                mc.thePlayer.setPosition(blockPosition[0], blockPosition[1], blockPosition[2]);

                ChatLogger.print(String.format("Teleported %s blocks %s.", distance, distance > 0 ? "forward" : "backward"));

                delay = 5;
            } catch (NumberFormatException e) {
                ChatLogger.print(String.format("\"%s\" is not a number.", distanceString));
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: forward <blocks>");
        }
    }
}
