package pw.latematt.xiv.mod.mods.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.Render3DEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.RenderUtils;

/**
 * @author Rederpz / EnterX
 *         <p>
 *         This isn't really a nocheat bypass, and it prob doesn't work on servers at all but ITS HERE M9
 *         This mod is also really fucking buggy.
 */

public class CreativeWorldEdit extends Mod implements Listener<Render3DEvent>, CommandHandler {

    public CreativeWorldEdit() {
        super("CreativeWorldEdit", ModType.PLAYER, Keyboard.KEY_NONE, 0xFFCC99FF);

        Command.newCommand()
                .cmd("creativeworldedit")
                .aliases("cwe")
                .description("Base command for the CreativeWorldEdit mod.")
                .arguments("<action>")
                .handler(this).build();
    }

    private BlockPos pos1, pos2;
    private double posX, posY, posZ, oposX, oposY, oposZ;

    @Override
    public void onEventCalled(Render3DEvent event) {
        if (pos1 != null && pos2 != null) {
            RenderUtils.beginGl();

            float partialTicks = event.getPartialTicks();
            double renderPosX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
            double renderPosY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks;
            double renderPosZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;

            GlStateManager.pushMatrix();
            drawBoxes(pos1.getX() - renderPosX, pos1.getY() - renderPosY, pos1.getZ() - renderPosZ, pos2.getX() - renderPosX, pos2.getY() - renderPosY, pos2.getZ() - renderPosZ);
            GlStateManager.popMatrix();

            RenderUtils.endGl();
        }
    }

    public void placeBlocks(String blockid) {
        if (!isEnabled()) {
            ChatLogger.print("Enable the CreateWorldEdit mod to start world editing.");
            return;
        }

        Item item = null;
        ItemStack stack = null;

        boolean isBreaking = false;

        if (!blockid.equals("random") && !blockid.equals("0") && !blockid.equals("air")) {
            try {
                item = Item.getItemFromBlock(Block.getBlockById(Integer.parseInt(blockid)));

                if (item != null) {
                    stack = new ItemStack(item, 1, 0);
                    mc.playerController.sendSlotPacket(stack, 36 + mc.thePlayer.inventory.currentItem);
                }
            } catch (NumberFormatException e) {
                ChatLogger.print(String.format("\"%s\" is not a number.", blockid));
                return;
            }
        } else if (blockid.equals("0") || blockid.equals("air")) {
            isBreaking = true;
        } else {
            item = Item.getItemFromBlock(Block.getBlockById((int) (Math.random() * 330)));

            if (item != null) {
                stack = new ItemStack(item, 1, 0);
                mc.playerController.sendSlotPacket(stack, 36 + mc.thePlayer.inventory.currentItem);
            }
        }

        if (pos1 == null || pos2 == null) {
            ChatLogger.print("One or more positions is not set.");
            return;
        }

        if (item == null && !isBreaking) {
            ChatLogger.print("Invalid item.");
            return;
        }

        oposX = posX = mc.thePlayer.posX;
        oposY = posY = mc.thePlayer.posY;
        oposZ = posZ = mc.thePlayer.posZ;

        int startx = 0, starty = 0, startz = 0;
        int endx = 0, endy = 0, endz = 0;

        if (pos1.getX() >= pos2.getX()) {
            startx = pos2.getX();
            endx = pos1.getX();
        } else {
            startx = pos1.getX();
            endx = pos2.getX();
        }

        if (pos1.getZ() >= pos2.getZ()) {
            startz = pos2.getZ();
            endz = pos1.getZ();
        } else {
            startz = pos1.getZ();
            endz = pos2.getZ();
        }

        if (pos1.getY() >= pos2.getY()) {
            starty = pos2.getY();
            endy = pos1.getY();
        } else {
            starty = pos1.getY();
            endy = pos2.getY();
        }

        for (int y = starty; y <= endy; y++) {
            for (int x = startx; x <= endx; x++) {
                for (int z = startz; z <= endz; z++) {
                    int xDist = (int) Math.abs(x - posX);
                    int yDist = (int) Math.abs(y - posY);
                    int zDist = (int) Math.abs(z - posZ);

                    if (xDist + yDist + zDist > 5) {
                        posX = (int) x;
                        posY = (int) y;
                        posZ = (int) z;

                        BlockPos pos1 = new BlockPos(x, y + 4, z);
                        BlockPos pos2 = new BlockPos(x, y + 3, z);
                        BlockPos pos3 = new BlockPos(x, y + 2, z);
                        BlockPos pos4 = new BlockPos(x, y + 1, z);

                        Block block1 = mc.theWorld.getBlockState(pos1).getBlock();
                        Block block2 = mc.theWorld.getBlockState(pos2).getBlock();
                        Block block3 = mc.theWorld.getBlockState(pos3).getBlock();
                        Block block4 = mc.theWorld.getBlockState(pos4).getBlock();

                        if (!block1.isFullBlock() && !block2.isFullBlock() && !block3.isFullBlock() && !block4.isFullBlock()) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1, z, true));
                        }
                    }

                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 3, z, true));

                    if (blockid.equals("random") && !isBreaking) {
                        item = Item.getItemFromBlock(Block.getBlockById((int) (Math.random() * 330)));

                        if (item != null) {
                            stack = new ItemStack(item, 1, 0);
                            mc.playerController.sendSlotPacket(stack, 36 + mc.thePlayer.inventory.currentItem);
                        }
                    }

                    if (isBreaking) {
                        Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();

                        if (!(block instanceof BlockAir)) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(x, y, z), EnumFacing.DOWN));
                            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(x, y, z), EnumFacing.DOWN));
                        }
                    } else {
                        Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();

                        if (block instanceof BlockAir) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                            mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(x, y - 1, z), EnumFacing.UP.getIndex(), mc.thePlayer.getHeldItem(), 0, 0, 0));
                        }
                    }
                }
            }
        }

        ChatLogger.print(isBreaking ? "Tried to destroy blocks." : "Tried to place blocks.");

        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(oposX, oposY, oposZ, true));
        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "pos1":
                    try {
                        if (arguments[2].equalsIgnoreCase("clear")) {
                            pos1 = null;
                        } else {
                            pos1 = new BlockPos(Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]), Integer.parseInt(arguments[4]));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        pos1 = new BlockPos(mc.thePlayer.getPosition());
                    } catch (NumberFormatException e) {
                        ChatLogger.print("Invalid arguments, valid: creativeworldedit pos1 <x> <y> <z>");
                    }

                    if (pos1 != null) {
                        ChatLogger.print("First Position set to: " + pos1);
                    }
                    break;
                case "pos2":
                    try {
                        if (arguments[2].equalsIgnoreCase("clear")) {
                            pos2 = null;
                        } else {
                            pos2 = new BlockPos(Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]), Integer.parseInt(arguments[4]));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        pos2 = new BlockPos(mc.thePlayer.getPosition());
                    } catch (NumberFormatException e) {
                        ChatLogger.print("Invalid arguments, valid: creativeworldedit pos2 <x> <y> <z>");
                    }

                    if (pos2 != null) {
                        ChatLogger.print("Second Position set to: " + pos2);
                    }
                    break;
                case "set":
                    placeBlocks(arguments[2]);
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: pos1, pos2, set");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: creativeworldedit <action>");
        }
    }

    private void drawBoxes(double x, double y, double z, double xo, double yo, double zo) {
        double x1 = x;
        double y1 = y;
        double z1 = z;
        double x2 = xo;
        double y2 = yo;
        double z2 = zo;

        if (x1 > x2) {
            x1 += 1.0D;
        }

        if (x1 == x2) {
            x1 += 1.0D;
        }

        if (x2 > x1) {
            x2 += 1.0D;
        }

        if (y1 > y2) {
            y1 += 1.0D;
        }

        if (y1 == y2) {
            y1 += 1.0D;
        }

        if (y2 > y1) {
            y2 += 1.0D;
        }

        if (z1 > z2) {
            z1 += 1.0D;
        }

        if (z1 == z2) {
            z1 += 1.0D;
        }

        if (z2 > z1) {
            z2 += 1.0D;
        }

        AxisAlignedBB box = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);

        GlStateManager.color(0.7F, 0.4F, 1.0F, 0.6F);
        RenderUtils.drawLines(box);
        GlStateManager.color(0.7F, 0.4F, 1.0F, 0.6F);
        RenderGlobal.drawOutlinedBoundingBox(box, -1);
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
