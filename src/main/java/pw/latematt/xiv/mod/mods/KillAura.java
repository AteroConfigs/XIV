package pw.latematt.xiv.mod.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.utils.Timer;
import pw.latematt.xiv.value.Value;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Matthew
 */
public class KillAura extends Mod implements CommandHandler {
    private final Listener motionUpdateListener;
    private final Listener sendPacketListener;
    private final Value<Long> delay = new Value<>("killaura_delay", 83L);
    private final Value<Double> range = new Value<>("killaura_range", 3.9);
    private final Value<Boolean> players = new Value<>("killaura_players", true);
    private final Value<Boolean> mobs = new Value<>("killaura_mobs", false);
    private final Value<Boolean> animals = new Value<>("killaura_animals", false);
    private final Value<Boolean> invisible = new Value<>("killaura_invisible", true);
    private final Value<Boolean> team = new Value<>("killaura_team", false);
    private final Value<Boolean> silent = new Value<>("killaura_silent", true);
    private final Value<Boolean> autosword = new Value<>("killaura_autosword", true);
    private final List<EntityLivingBase> entities;
    private EntityLivingBase entityToAttack;
    private boolean aimed;
    private Timer entitySelectTimer = new Timer();
    private Timer attackTimer = new Timer();

    public KillAura() {
        super("Kill Aura", Keyboard.KEY_F, 0xFFC6172B);

        Command.newCommand()
                .cmd("killaura")
                .description("Base command for the Kill Aura mod.")
                .aliases("killa", "ka")
                .arguments("<action>")
                .handler(this)
                .build();

        entities = new CopyOnWriteArrayList<>();
        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (entities.isEmpty() && entitySelectTimer.hasReached(delay.getValue())) {
                        mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).forEach(entity -> {
                            EntityLivingBase living = (EntityLivingBase) entity;
                            if (isValidEntity(living)) {
                                entities.add(living);
                            }
                        });
                        entitySelectTimer.reset();
                    }

                    if (!entities.isEmpty()) {
                        EntityLivingBase firstInArray = entities.get(0);
                        if (isValidEntity(firstInArray)) {
                            entityToAttack = firstInArray;
                        } else {
                            entities.remove(firstInArray);
                        }
                    }

                    if (entityToAttack != null) {
                        float[] rotations = EntityUtils.getEntityRotations(entityToAttack);
                        if (silent.getValue()) {
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
                        } else {
                            mc.thePlayer.rotationYaw = rotations[0];
                            mc.thePlayer.rotationPitch = rotations[1];
                        }
                    }
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {
                    if (entityToAttack != null && aimed) {
                        if (attackTimer.hasReached(delay.getValue())) {
                            attack(entityToAttack);
                            aimed = false;
                            entities.remove(entityToAttack);
                            entityToAttack = null;
                            attackTimer.reset();
                        }
                    }
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    if (entityToAttack != null) {
                        float[] rotations = EntityUtils.getEntityRotations(entityToAttack);
                        if (silent.getValue()) {
                            player.yaw = rotations[0];
                            player.pitch = rotations[1];
                            player.rotating = true;
                        }
                        aimed = true;
                    }
                }
            }
        };
    }

    private void attack(EntityLivingBase target) {
        final boolean wasSprinting = mc.thePlayer.isSprinting();
        if (autosword.getValue()) {
            mc.thePlayer.inventory.currentItem = EntityUtils
                    .getBestWeapon(target);
            mc.playerController.updateController();
        }

        // stop sprinting
        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));

        mc.thePlayer.swingItem();
        int oldDamage = 0;
        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            oldDamage = mc.thePlayer.getCurrentEquippedItem().getItemDamage();
        }
        mc.playerController.attackEntity(mc.thePlayer, target);
        if (mc.thePlayer.getCurrentEquippedItem() != null) {
            mc.thePlayer.getCurrentEquippedItem().setItemDamage(oldDamage);
        }

        // continue sprinting
        if (wasSprinting) {
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
        mc.thePlayer.setSprinting(wasSprinting);
    }

    private boolean isValidEntity(EntityLivingBase entity) {
        if (entity == null)
            return false;
        if (entity == mc.thePlayer)
            return false;
        if (!entity.isEntityAlive())
            return false;
        if (entity.ticksExisted < 20)
            return false;
        if (mc.thePlayer.getDistanceToEntity(entity) > range.getValue())
            return false;
        if (!(invisible.getValue()) && entity.isInvisibleToPlayer(mc.thePlayer))
            return false;
        if (team.getValue() && entity.getTeam().isSameTeam(mc.thePlayer.getTeam()))
            return false;
        if (entity instanceof EntityPlayer) {
            return players.getValue() && !XIV.getInstance().getFriendManager().isFriend(entity.getDisplayName().getUnformattedText());
        } else if (entity instanceof IAnimals && !(entity instanceof IMob)) {
            return animals.getValue();
        } else if (entity instanceof IMob) {
            return mobs.getValue();
        }
        return false;
    }

    // used in autoblock
    public boolean shouldBlock() {
        return entityToAttack == null && !aimed;
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action) {
                case "delay":
                case "d":
                    break;
                case "range":
                case "r":
                    break;
                case "players":
                case "plyrs":
                    break;
                case "mobs":
                    break;
                case "animals":
                    break;
                case "invisible":
                case "invis":
                    break;
                case "team":
                    break;
                case "silent":
                case "sil":
                    break;
                case "autosword":
                case "as":
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: delay, range, players, mobs, animals, invisible, team, silent, autosword");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: killaura <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        entities.clear();
        entityToAttack = null;
        aimed = false;
    }
}