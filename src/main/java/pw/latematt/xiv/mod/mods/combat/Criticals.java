package pw.latematt.xiv.mod.mods.combat;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 * @author Jack
 */
public class Criticals extends Mod implements Listener<SendPacketEvent>, CommandHandler {
    private final Value<Mode> currentMode = new Value<>("criticals_mode", Mode.MINIJUMPS);
    private final Listener attackEntityListener;
    private boolean nextAttack;
    private float fallDist;

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);
        setTag(currentMode.getValue().getName());
        Command.newCommand().cmd("criticals").description("Base command for Criticals mod.").arguments("<action>").aliases("crits").handler(this).build();

        attackEntityListener = new Listener<AttackEntityEvent>() {
            @Override
            public void onEventCalled(AttackEntityEvent event) {
                if (currentMode.getValue() == Mode.MINIJUMPS) {
                    if (!BlockUtils.isOnLiquid(mc.thePlayer) && mc.thePlayer.isCollidedVertically && !BlockUtils.isInLiquid(mc.thePlayer)) {
                        if (nextAttack = !nextAttack) {
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.05, mc.thePlayer.posZ, false));
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.012511, mc.thePlayer.posZ, false));
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onEventCalled(SendPacketEvent event) {
        if (this.currentMode.getValue() != Mode.FLOAT)
            return;
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
            if (isSafe())
                fallDist += mc.thePlayer.fallDistance;

            if (!isSafe() || fallDist >= 3.0F) {
                player.setOnGround(true);
                fallDist = 0.0F;
                mc.thePlayer.fallDistance = 0.0F;

                if (mc.thePlayer.onGround && !BlockUtils.isOnLiquid(mc.thePlayer) && !BlockUtils.isInLiquid(mc.thePlayer)) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.01, mc.thePlayer.posZ, false));
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    fallDist += 1.01F;
                }
            } else if (fallDist > 0.0F) {
                player.setOnGround(false);
            }
        }
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
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(this, attackEntityListener);
        if (mc.thePlayer != null && currentMode.getValue() == Mode.FLOAT) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.01, mc.thePlayer.posZ, false));
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            fallDist += 1.01F;
        }
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this, attackEntityListener);
        fallDist = 0.0F;
        nextAttack = false;
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
                            case "float":
                            case "offground":
                                currentMode.setValue(Mode.FLOAT);
                                ChatLogger.print(String.format("Criticals Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "minijumps":
                                currentMode.setValue(Mode.MINIJUMPS);
                                ChatLogger.print(String.format("Criticals Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            case "-d":
                                currentMode.setValue(currentMode.getDefault());
                                ChatLogger.print(String.format("Criticals Mode set to: %s", currentMode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: float, minijumps");
                                break;
                        }
                        setTag(currentMode.getValue().getName());
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

    public enum Mode {
        FLOAT, MINIJUMPS;

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