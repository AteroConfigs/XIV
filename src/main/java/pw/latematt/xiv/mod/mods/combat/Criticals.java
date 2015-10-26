package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class Criticals extends Mod implements CommandHandler {
    private final Value<Mode> currentMode = new Value<>("criticals_mode", Mode.OFF_GROUND);
    private final Listener sendPacketListener, attackEntityListener, motionUpdateListener;
    private float fallDist;
    private Timer timer = new Timer();

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);
        setTag(String.format("%s \2477%s", getName(), currentMode.getValue().getName()));

        Command.newCommand()
                .cmd("criticals")
                .description("Base command for the Speed mod.")
                .aliases("crits")
                .arguments("<action>")
                .handler(this)
                .build();

        attackEntityListener = new Listener<AttackEntityEvent>() {
            @Override
            public void onEventCalled(AttackEntityEvent event) {
                if (!isSafe())
                    return;
                if (currentMode.getValue() != Mode.OFFSET)
                    return;
                if (!mc.thePlayer.onGround)
                    return;
                KillAura killAura = (KillAura) XIV.getInstance().getModManager().find("killaura");
                if (!killAura.isAttacking())
                    return;

                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625101, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            }
        };

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (!isSafe())
                        return;
                    if (currentMode.getValue() != Mode.OFFSET)
                        return;
                    if (!mc.thePlayer.onGround)
                        return;
                    KillAura killAura = (KillAura) XIV.getInstance().getModManager().find("killaura");
                    if (!killAura.isAttacking())
                        return;

                    if (timer.hasReached(100)) {
                        event.setCancelled(true);
                        timer.reset();
                    }
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (currentMode.getValue() != Mode.OFF_GROUND)
                    return;

                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (isSafe())
                        fallDist += mc.thePlayer.fallDistance;

                    if (!isSafe() || fallDist >= 3.0F) {
                        player.setOnGround(true);
                        fallDist = 0.0F;
                        mc.thePlayer.fallDistance = 0.0F;

//                        if (mc.thePlayer.onGround) {
//                            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.01, mc.thePlayer.posZ, false));
//                            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
//                            fallDist += 1.01F;
//                        }
                    } else if (fallDist > 0.0F) {
                        player.setOnGround(false);
                    }
                }
            }
        };
    }

    public float getFallDistance() {
        return fallDist;
    }

    public void setFallDistance(float fallDist) {
        this.fallDist = fallDist;
    }

    private boolean isSafe() {
        return !mc.thePlayer.isInWater() &&
                !mc.thePlayer.isInsideOfMaterial(Material.lava) &&
                !mc.thePlayer.isOnLadder() &&
                !mc.thePlayer.isPotionActive(Potion.BLINDNESS) &&
                mc.thePlayer.ridingEntity == null;
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
                            case "offset":
                                currentMode.setValue(Mode.OFFSET);
                                ChatLogger.print(String.format("Criticals Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "offground":
                                currentMode.setValue(Mode.OFF_GROUND);
                                ChatLogger.print(String.format("Criticals Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "-d":
                                currentMode.setValue(currentMode.getDefault());
                                ChatLogger.print(String.format("Criticals Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: offset, offground");
                                break;
                        }
                        setTag(String.format("%s \2477%s", getName(), currentMode.getValue().getName()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: criticals mode <mode>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: mode");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: criticals <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(sendPacketListener);
        XIV.getInstance().getListenerManager().add(attackEntityListener);
        XIV.getInstance().getListenerManager().add(motionUpdateListener);

        if (mc.thePlayer != null && currentMode.getValue() == Mode.OFF_GROUND) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.01, mc.thePlayer.posZ, false));
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            fallDist += 1.01F;
        }
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        XIV.getInstance().getListenerManager().remove(attackEntityListener);
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        if (currentMode.getValue() == Mode.OFF_GROUND)
            fallDist = 0.0F;
    }

    public enum Mode {
        OFFSET, OFF_GROUND;

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
