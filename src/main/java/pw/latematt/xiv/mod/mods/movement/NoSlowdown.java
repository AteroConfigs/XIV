package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SoulSandSlowdownEvent;
import pw.latematt.xiv.event.events.UsingItemSlowdownEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.combat.aura.KillAura;
import pw.latematt.xiv.mod.mods.player.FastUse;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

/**
 * @author Matthew
 */
public class NoSlowdown extends Mod implements CommandHandler {
    private final Listener itemSlowdownListener, soulSandSlowdownListener, motionUpdateListener;
    private Value<Boolean> shotBow = new Value<>("noslowdown_shotbow", false);

    public NoSlowdown() {
        super("NoSlowdown", ModType.MOVEMENT, Keyboard.KEY_NONE);
        Command.newCommand().cmd("noslowdown").aliases("nos", "noslow").description("Base command for NoSlowdown mod.").arguments("<action>").handler(this).build();

        itemSlowdownListener = new Listener<UsingItemSlowdownEvent>() {
            @Override
            public void onEventCalled(UsingItemSlowdownEvent event) {
                if (shouldSlowdown())
                    return;

                event.setCancelled(true);
            }
        };

        soulSandSlowdownListener = new Listener<SoulSandSlowdownEvent>() {
            @Override
            public void onEventCalled(SoulSandSlowdownEvent event) {
                event.setCancelled(true);
            }
        };

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            public boolean blockingFix;

            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (mc.thePlayer.getCurrentEquippedItem() == null || !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword))
                    return;

                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (mc.thePlayer.isBlocking()) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {
                    KillAura aura = (KillAura) XIV.getInstance().getModManager().find("killaura");
                    if (aura.isAttacking()) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    } else if (mc.thePlayer.isBlocking()) {
                        if (!blockingFix)
                            blockingFix = true;

                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                    } else if (blockingFix) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        blockingFix = false;
                    }
                }
            }
        };

        setEnabled(true);
    }

    public boolean shouldSlowdown() {
        if (shotBow.getValue() && mc.thePlayer.getItemInUse() != null) {
            int maxUseDuration = mc.thePlayer.getItemInUse().getMaxItemUseDuration();
            FastUse fastUse = (FastUse) XIV.getInstance().getModManager().find("fastuse");
            if (fastUse != null && fastUse.isEnabled())
                maxUseDuration = fastUse.getTicksToWait().getValue();
            if (mc.thePlayer.getItemInUseDuration() > maxUseDuration - 2)
                return true;
        }

        return false;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "shotbow":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            shotBow.setValue(shotBow.getDefault());
                        } else {
                            shotBow.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        shotBow.setValue(!shotBow.getValue());
                    }
                    ChatLogger.print(String.format("NoSlowdown will %s bypass Shotbow's anticheat.", (shotBow.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: shotbow");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: noslowdown <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(itemSlowdownListener);
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        XIV.getInstance().getListenerManager().add(soulSandSlowdownListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(itemSlowdownListener);
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(soulSandSlowdownListener);
        if (mc.thePlayer != null) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }
}
