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
    private final Value<Boolean> miniJumps = new Value<>("criticals_mini_jumps", true);
    private final Listener attackEntityListener;
    private boolean next;
    private float fallDist;

    public Criticals() {
        super("Criticals", ModType.COMBAT, Keyboard.KEY_NONE, 0xFFA38EC7);
        Command.newCommand().cmd("criticals").description("Base command for Criticals mod.").arguments("<action>").aliases("crits").handler(this).build();

        attackEntityListener = new Listener<AttackEntityEvent>() {
            @Override
            public void onEventCalled(AttackEntityEvent event) {
                if (miniJumps.getValue()) {
                    if (!BlockUtils.isOnLiquid(mc.thePlayer) && mc.thePlayer.isCollidedVertically && !BlockUtils.isInLiquid(mc.thePlayer)) {
                        if ((next = !next)) {
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
        if (miniJumps.getValue())
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
        if (mc.thePlayer != null && !miniJumps.getValue()) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.01, mc.thePlayer.posZ, false));
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            fallDist += 1.01F;
        }
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(this, attackEntityListener);
        fallDist = 0.0F;
        next = false;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "minijumps":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            miniJumps.setValue(miniJumps.getDefault());
                        } else {
                            miniJumps.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        miniJumps.setValue(!miniJumps.getValue());
                    }
                    ChatLogger.print(String.format("Criticals will %s perform mini jumps.", (miniJumps.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: minijumps");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: criticals <action>");
        }
    }
}