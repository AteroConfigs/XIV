package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.BlockAddBBEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.PushOutOfBlocksEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Rederpz
 */
public class Phase extends Mod implements Listener<MotionUpdateEvent>, CommandHandler {
    private final Value<Mode> mode = new Value<>("phase_mode", Mode.SKIP);
    private final Listener blockAddBBListener;
    private final Listener pushOutOfBlocksListener;
    private final Listener sendPacketListener;
    private boolean collided = false;

    public Phase() {
        super("Phase", ModType.MOVEMENT, Keyboard.KEY_LBRACKET, 0xFF66FF99);
        setTag(String.format("%s \2477%s", getName(), this.mode.getValue().getName()));

        Command.newCommand()
                .cmd("phase")
                .description("Base command for the Phase mod.")
                .aliases("noclip", "ph", "noc")
                .arguments("<action>")
                .handler(this)
                .build();

        blockAddBBListener = new Listener<BlockAddBBEvent>() {
            @Override
            public void onEventCalled(BlockAddBBEvent event) {
                if (mc.thePlayer.getEntityBoundingBox().minY - 0.5F < event.getPos().getY() && BlockUtils.isInsideBlock(mc.thePlayer)) {
                    event.setAxisAlignedBB(null);
                }

                if (mc.thePlayer.getEntityBoundingBox().maxY < event.getPos().getY() && !BlockUtils.isInsideBlock(mc.thePlayer)) {
                    event.setAxisAlignedBB(null);
                }
            }
        };

        this.pushOutOfBlocksListener = new Listener<PushOutOfBlocksEvent>() {
            @Override
            public void onEventCalled(PushOutOfBlocksEvent event) {
                event.setCancelled(true);
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (mode.getValue() == Mode.VANILLA && BlockUtils.isInsideBlock(mc.thePlayer)) {
                        player.setY(player.getY() + 0.1F);
                    }
                }
            }
        };
    }

    public void onEventCalled(MotionUpdateEvent event) {
        if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
            if (mode.getValue() == Mode.SKIP || mode.getValue() == Mode.VANILLA_SKIP) {
                if (!mc.thePlayer.isCollidedHorizontally && collided) {
                    collided = false;
                }

                boolean moving = mc.gameSettings.keyBindForward.getIsKeyPressed() || mc.gameSettings.keyBindBack.getIsKeyPressed() || mc.gameSettings.keyBindLeft.getIsKeyPressed() || mc.gameSettings.keyBindRight.getIsKeyPressed();

                if (mc.thePlayer.isCollidedHorizontally && !collided && mc.thePlayer.onGround && !BlockUtils.isInsideBlock(mc.thePlayer) && moving) {
                    if (mc.thePlayer.isCollidedHorizontally) {
                        collided = true;
                    }

                    float dir = mc.thePlayer.rotationYaw;

                    if (mc.thePlayer.moveForward < 0.0F) {
                        dir += 180.0F;
                    }

                    if (mc.thePlayer.moveStrafing > 0.0F) {
                        dir -= 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);
                    }

                    if (mc.thePlayer.moveStrafing < 0.0F) {
                        dir += 90.0F * (mc.thePlayer.moveForward < 0.0F ? -0.5F : mc.thePlayer.moveForward > 0.0F ? 0.5F : 1.0F);
                    }

                    float xD = (float) Math.cos((dir + 90.0F) * Math.PI / 180.0D);
                    float zD = (float) Math.sin((dir + 90.0F) * Math.PI / 180.0D);
                    float[] offset = new float[]{xD * 0.25F, 1.0F, zD * 0.25F};

                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));

                    if (mode.getValue() == Mode.SKIP) {
                        /**
                         *  TOO MUCH MEME 6 YOU HAHA XBOYS CODE IN XIV TELL MOM!!!
                         */

                        double[] movements = {
                                -0.025000000372529D,
                                -0.02857142899717604D,
                                -0.0333333338300387D,
                                -0.04000000059604645D};

                        for (int i = 0; i < movements.length; i++) {
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (movements[i] * offset[1]) + 0.025F, mc.thePlayer.posZ, mc.thePlayer.onGround));
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + offset[0] * i, mc.thePlayer.posY, mc.thePlayer.posZ + offset[2] * i, mc.thePlayer.onGround));
                        }

                        mc.thePlayer.setPosition(mc.thePlayer.posX + (offset[0] * 0.05F), mc.thePlayer.posY, mc.thePlayer.posZ + (offset[2] * 0.05F));
                    } else if (mode.getValue() == Mode.VANILLA_SKIP) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + (offset[0] * 6), mc.thePlayer.posY, mc.thePlayer.posZ + (offset[2] * 6), mc.thePlayer.onGround));
                    }

                    if (mc.thePlayer != null && !mc.thePlayer.isSneaking()) {
                        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    }
                }
            }
        }
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
                            case "skip":
                                this.mode.setValue(Mode.SKIP);
                                ChatLogger.print(String.format("Phase Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            case "vanillaskip":
                                this.mode.setValue(Mode.VANILLA_SKIP);
                                ChatLogger.print(String.format("Phase Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            case "vanilla":
                                this.mode.setValue(Mode.VANILLA);
                                ChatLogger.print(String.format("Phase Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            case "-d":
                                this.mode.setValue(this.mode.getDefault());
                                ChatLogger.print(String.format("Phase Mode set to: %s", this.mode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: skip, vanilla");
                                break;
                        }
                        setTag(String.format("%s \2477%s", getName(), this.mode.getValue().getName()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: phase mode <mode>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: mode");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: phase <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this);
        XIV.getInstance().getListenerManager().add(blockAddBBListener);
        XIV.getInstance().getListenerManager().add(pushOutOfBlocksListener);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this);
        XIV.getInstance().getListenerManager().remove(blockAddBBListener);
        XIV.getInstance().getListenerManager().remove(pushOutOfBlocksListener);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);

        this.collided = false;
    }

    private enum Mode {
        SKIP, VANILLA, VANILLA_SKIP;

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
