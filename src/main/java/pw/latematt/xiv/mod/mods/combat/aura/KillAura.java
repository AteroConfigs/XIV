package pw.latematt.xiv.mod.mods.combat.aura;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.event.events.PlayerDeathEvent;
import pw.latematt.xiv.event.events.SendPacketEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.combat.aura.mode.AuraMode;
import pw.latematt.xiv.mod.mods.combat.aura.mode.modes.Multi;
import pw.latematt.xiv.mod.mods.combat.aura.mode.modes.Singular;
import pw.latematt.xiv.mod.mods.combat.aura.mode.modes.Switch;
import pw.latematt.xiv.mod.mods.player.AutoHeal;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.value.Value;

import java.util.Random;

/**
 * @author Matthew
 */
public class KillAura extends Mod implements CommandHandler {
    private final Listener motionUpdateListener;
    private final Listener sendPacketListener;
    private final Listener playerDeathListener;
    public final Value<Long> delay = new Value<>("killaura_delay", 166L);
    public final Value<Long> randomDelay = new Value<>("killaura_random_delay", 0L);
    public final Value<Double> range = new Value<>("killaura_range", 3.8D);
    public final Value<Integer> fov = new Value<>("killaura_fov", 360);
    private final Value<Boolean> players = new Value<>("killaura_players", true);
    private final Value<Boolean> mobs = new Value<>("killaura_mobs", false);
    private final Value<Boolean> animals = new Value<>("killaura_animals", false);
    private final Value<Boolean> invisible = new Value<>("killaura_invisible", false);
    private final Value<Boolean> team = new Value<>("killaura_team", true);
    private final Value<Boolean> friends = new Value<>("killaura_friends", false);
    public final Value<Boolean> silent = new Value<>("killaura_silent", true);
    public final Value<Boolean> autoSword = new Value<>("killaura_auto_sword", true);
    private final Value<Boolean> toggleDeath = new Value<>("killaura_toggle_death", false);
    public final Value<Boolean> autoBlock = new Value<>("killaura_auto_block", false);
    private final Value<AuraMode> mode = new Value<>("killaura_mode", new Singular(this));
    private final Random random = new Random();

    public KillAura() {
        super("Kill Aura", ModType.COMBAT, Keyboard.KEY_R, 0xFFC6172B);

        Command.newCommand()
                .cmd("killaura")
                .description("Base command for the Kill Aura mod.")
                .aliases("killa", "ka")
                .arguments("<action>")
                .handler(this)
                .build();

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    mode.getValue().onPreMotionUpdate(event);
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {
                    mode.getValue().onPostMotionUpdate(event);
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    mode.getValue().onMotionPacket(player);
                }
            }
        };

        playerDeathListener = new Listener<PlayerDeathEvent>() {
            @Override
            public void onEventCalled(PlayerDeathEvent event) {
                if (toggleDeath.getValue()) {
                    toggle();
                }
            }
        };

        setTag(String.format("%s \2477%s", getName(), mode.getValue().getName()));
    }

    public void attack(EntityLivingBase target) {
        final boolean wasSprinting = mc.thePlayer.isSprinting();
        if (autoSword.getValue()) {
            mc.thePlayer.inventory.currentItem = EntityUtils.getBestWeapon(target);
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

    public boolean isValidEntity(EntityLivingBase entity) {
        if (entity == null)
            return false;
        if (entity == mc.thePlayer)
            return false;
        if (entity == EntityUtils.getReference())
            return false;
        if (EntityUtils.getAngle(EntityUtils.getEntityRotations(entity)) > fov.getValue())
            return false;
        if (!entity.isEntityAlive())
            return false;
        if (entity.ticksExisted < 20)
            return false;
        if (EntityUtils.getReference().getDistanceToEntity(entity) > range.getValue())
            return false;
        if (!invisible.getValue() && entity.isInvisibleToPlayer(mc.thePlayer))
            return false;
        if (!team.getValue() && entity.getTeam() != null && entity.getTeam().isSameTeam(mc.thePlayer.getTeam()))
            return false;
        // 85.136.70.107
        if (entity instanceof EntityPlayer) {
            if (!friends.getValue() && XIV.getInstance().getFriendManager().isFriend(entity.getCommandSenderEntity().getName()))
                return false;
            return players.getValue();
        } else if (entity instanceof IAnimals && !(entity instanceof IMob)) {
            if (entity instanceof EntityHorse) {
                EntityHorse horse = (EntityHorse) entity;
                return animals.getValue() && horse.riddenByEntity != mc.thePlayer;
            }
            return animals.getValue();
        } else if (entity instanceof IMob) {
            return mobs.getValue();
        }
        return false;
    }

    // used in autoblock
    public boolean isAttacking() {
        return mode.getValue().isAttacking();
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action.toLowerCase()) {
                case "delay":
                case "d":
                    if (arguments.length >= 3) {
                        String newDelayString = arguments[2];
                        try {
                            long newDelay = arguments[2].equalsIgnoreCase("-d") ? delay.getDefault() : Long.parseLong(newDelayString);
                            if (newDelay < 10) {
                                newDelay = 10;
                            }

                            delay.setValue(newDelay);
                            ChatLogger.print(String.format("Kill Aura Delay set to %sms", delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura delay <number>");
                    }
                    break;
                case "randomdelay":
                case "rd":
                    if (arguments.length >= 3) {
                        String newRandomDelayString = arguments[2];
                        try {
                            long newRandomDelay = arguments[2].equalsIgnoreCase("-d") ? randomDelay.getDefault() : Long.parseLong(newRandomDelayString);
                            if (newRandomDelay < 0) {
                                newRandomDelay = 0;
                            }

                            randomDelay.setValue(newRandomDelay);
                            ChatLogger.print(String.format("Kill Aura Random Delay set to %sms", randomDelay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newRandomDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura randomdelay <number>");
                    }
                    break;
                case "aps":
                    if (arguments.length >= 3) {
                        String newApsString = arguments[2];
                        try {
                            long newAps = arguments[2].equalsIgnoreCase("-d") ? delay.getDefault() : Long.parseLong(newApsString);
                            if (newAps < 1) {
                                newAps = 1;
                            }

                            delay.setValue(1000 / newAps);
                            ChatLogger.print(String.format("Kill Aura Attacks/second set to %s (%sms)", newAps, delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newApsString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura aps <number>");
                    }
                    break;
                case "fov":
                    if (arguments.length >= 3) {
                        String newFOVString = arguments[2];
                        try {
                            int newFOV = arguments[2].equalsIgnoreCase("-d") ? fov.getDefault() : Integer.parseInt(newFOVString);
                            if (newFOV < 10) {
                                newFOV = 10;
                            } else if (newFOV > 360) {
                                newFOV = 360;
                            }

                            fov.setValue(newFOV);
                            ChatLogger.print(String.format("Kill Aura FOV set to %s", fov.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newFOVString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura fov <number>");
                    }
                    break;
                case "range":
                case "r":
                    if (arguments.length >= 3) {
                        String newRangeString = arguments[2];
                        try {
                            double newRange = arguments[2].equalsIgnoreCase("-d") ? range.getDefault() : Double.parseDouble(newRangeString);
                            range.setValue(newRange);
                            ChatLogger.print(String.format("Kill Aura Range set to %s", range.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newRangeString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura range <number>");
                    }
                    break;
                case "friends":
                case "frnds":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            friends.setValue(friends.getDefault());
                        } else {
                            friends.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        friends.setValue(!friends.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s attack friends.", (friends.getValue() ? "now" : "no longer")));
                    break;
                case "players":
                case "plyrs":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            players.setValue(players.getDefault());
                        } else {
                            players.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        players.setValue(!players.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s attack players.", (players.getValue() ? "now" : "no longer")));
                    break;
                case "mobs":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            mobs.setValue(mobs.getDefault());
                        } else {
                            mobs.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        mobs.setValue(!mobs.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s attack mobs.", (mobs.getValue() ? "now" : "no longer")));
                    break;
                case "animals":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            animals.setValue(animals.getDefault());
                        } else {
                            animals.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        animals.setValue(!animals.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s attack animals.", (animals.getValue() ? "now" : "no longer")));
                    break;
                case "invisible":
                case "invis":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            invisible.setValue(invisible.getDefault());
                        } else {
                            invisible.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        invisible.setValue(!invisible.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s attack invisible entities.", (invisible.getValue() ? "now" : "no longer")));
                    break;
                case "toggledeath":
                case "tdeath":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            toggleDeath.setValue(toggleDeath.getDefault());
                        } else {
                            toggleDeath.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        toggleDeath.setValue(!toggleDeath.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s toggle on death.", (toggleDeath.getValue() ? "now" : "no longer")));
                    break;
                case "team":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            team.setValue(team.getDefault());
                        } else {
                            team.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        team.setValue(!team.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s attack your team.", (team.getValue() ? "now" : "no longer")));
                    break;
                case "silent":
                case "sil":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            silent.setValue(silent.getDefault());
                        } else {
                            silent.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        silent.setValue(!silent.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s silently aim.", (silent.getValue() ? "now" : "no longer")));
                    break;
                case "autosword":
                case "as":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            autoSword.setValue(autoSword.getDefault());
                        } else {
                            autoSword.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        autoSword.setValue(!autoSword.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s automatically switch to your sword.", (autoSword.getValue() ? "now" : "no longer")));
                    break;
                case "autoblock":
                case "ab":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            autoBlock.setValue(autoBlock.getDefault());
                        } else {
                            autoBlock.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        autoBlock.setValue(!autoBlock.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s automatically block for you.", (autoBlock.getValue() ? "now" : "no longer")));
                    break;
                case "mode":
                    if (arguments.length >= 3) {
                        String mode = arguments[2];
                        switch (mode.toLowerCase()) {
                            case "singular":
                                this.mode.setValue(new Singular(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", this.mode.getValue().getName()));
                                break;
                            case "switch":
                                this.mode.setValue(new Switch(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", this.mode.getValue().getName()));
                                break;
                            case "multi":
                                this.mode.setValue(new Multi(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", this.mode.getValue().getName()));
                                break;
                            case "-d":
                                this.mode.setValue(this.mode.getDefault());
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", this.mode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: singular, switch, multi");
                                break;
                        }
                        setTag(String.format("%s \2477%s", getName(), this.mode.getValue().getName()));
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura mode <mode>");
                    }
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: delay, randomdelay, aps, range, players, mobs, animals, invisible, team, silent, autosword, autoblock, mode");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: killaura <action>");
        }
    }

    public Long getDelay() {
        return randomDelay.getValue() == 0 ? delay.getValue() : Long.valueOf(delay.getValue() + random.nextInt(randomDelay.getValue().intValue()));
    }

    public boolean isHealing() {
        AutoHeal autoHeal = (AutoHeal) XIV.getInstance().getModManager().find("autoheal");
        return autoHeal != null && autoHeal.isHealing();
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener);
        XIV.getInstance().getListenerManager().add(sendPacketListener);
        XIV.getInstance().getListenerManager().add(playerDeathListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener);
        XIV.getInstance().getListenerManager().remove(sendPacketListener);
        XIV.getInstance().getListenerManager().remove(playerDeathListener);
        mode.getValue().onDisabled();
    }
}