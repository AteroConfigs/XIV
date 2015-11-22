package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BlockAddBBEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 * @author Friendly (the nigger jew)
 */
public class Jesus extends Mod implements CommandHandler {
    private final Value<Mode> currentMode = new Value<>("jesus_mode", Mode.OLD);

    private final Listener blockAddBBListener, motionUpdatesListener, sendPacketListener;
    private boolean nextTick, shouldJump;

    public Jesus() {
        super("Jesus", ModType.MOVEMENT, Keyboard.KEY_J, 0xFF56BFE3);
        Command.newCommand().cmd("jesus").description("Base command for the Jesus mod.").arguments("<action>").handler(this).build();

        blockAddBBListener = new Listener<BlockAddBBEvent>() {
            @Override
            public void onEventCalled(BlockAddBBEvent event) {
                if (event.getBlock() instanceof BlockLiquid && event.getBlock() != null && mc.theWorld != null && mc.thePlayer != null) {
                    IBlockState state = mc.theWorld.getBlockState(event.getPos());

                    switch (currentMode.getValue()) {
                        case NEW:
                            if (state != null) {
                                float blockHeight = BlockLiquid.getLiquidHeightPercent(event.getBlock().getMetaFromState(state));

                                shouldJump = blockHeight < 0.66F;
                            } else {
                                shouldJump = false;
                            }

                            if (shouldJump && event.getEntity() == mc.thePlayer && !(BlockUtils.getBlock(mc.thePlayer, 0.2) instanceof BlockLiquid) && mc.thePlayer.fallDistance <= 3.0F && !mc.thePlayer.isSneaking()) {
                                BlockPos pos = event.getPos();
                                event.setAxisAlignedBB(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 0.805F, pos.getZ() + 1));
                            }
                            break;
                        case OLD:
                            if (state != null) {
                                float blockHeight = BlockLiquid.getLiquidHeightPercent(event.getBlock().getMetaFromState(state));

                                shouldJump = blockHeight < 0.55F;
                            } else {
                                shouldJump = false;
                            }

                            if (shouldJump && event.getEntity() == mc.thePlayer && !BlockUtils.isInLiquid(mc.thePlayer) && mc.thePlayer.fallDistance <= 3.0F && !mc.thePlayer.isSneaking()) {
                                BlockPos pos = event.getPos();
                                event.setAxisAlignedBB(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1));
                            }
                            break;
                        case DOLPHIN:
                            shouldJump = true;
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        motionUpdatesListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (shouldJump && mc.thePlayer.isInsideOfMaterial(Material.air) && !mc.thePlayer.isSneaking()) {
                    switch (currentMode.getValue()) {
                        case NEW:
                            if (BlockUtils.getBlock(mc.thePlayer, 0.2) instanceof BlockLiquid) {
                                mc.thePlayer.motionY = 0.08;
                            }
                            break;
                        case OLD:
                            if (BlockUtils.isInLiquid(mc.thePlayer)) {
                                mc.thePlayer.motionY = 0.08;
                            }
                            break;
                        case DOLPHIN:
                            if (mc.thePlayer.isInWater()) {
                                mc.thePlayer.motionY = 0.05;
                            }
                            break;
                    }
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (!player.isMoving())
                        return;
                    if (BlockUtils.isOnLiquid(mc.thePlayer) && shouldJump && currentMode.getValue() != Mode.DOLPHIN) {
                        nextTick = !nextTick;
                        if (nextTick) {
                            player.setY(player.getY() - 0.01);
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(blockAddBBListener, motionUpdatesListener, sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(blockAddBBListener, motionUpdatesListener, sendPacketListener);
        nextTick = false;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "mode":
                    if (arguments.length >= 3) {
                        String mode = arguments[2];
                        switch (mode.toLowerCase()) {
                            case "new":
                            case "bypass":
                                currentMode.setValue(Mode.NEW);
                                ChatLogger.print(String.format("Jesus Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "old":
                                currentMode.setValue(Mode.OLD);
                                ChatLogger.print(String.format("Jesus Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "dolphin":
                            case "jump":
                            case "legit":
                                currentMode.setValue(Mode.DOLPHIN);
                                ChatLogger.print(String.format("Jesus Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "-d":
                                currentMode.setValue(currentMode.getDefault());
                                ChatLogger.print(String.format("Jesus Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: new, old, dolphin");
                                break;
                        }
                        setTag(currentMode.getValue().getName());
                    } else {
                        ChatLogger.print("Invalid arguments, valid: jesus mode <mode>");
                    }
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: jesus <action>");
        }
    }

    public enum Mode {
        NEW, OLD, DOLPHIN;

        public String getName() {
            String prettyName = "";
            String[] actualNameSplit = name().split("_");
            if (actualNameSplit.length > 0) {
                for (String arg : actualNameSplit) {
                    arg = arg.substring(0, 1).toUpperCase() + arg.substring(1, arg.length()).toLowerCase();
                    prettyName += arg + " ";
                }
            } else {
                prettyName = actualNameSplit[0].substring(0, 1).toUpperCase() + actualNameSplit[0].substring(1, actualNameSplit[0].length()).toLowerCase();
            }
            return prettyName.trim();
        }
    }
}
