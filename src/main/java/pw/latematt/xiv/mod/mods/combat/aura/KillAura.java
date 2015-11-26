package pw.latematt.xiv.mod.mods.combat.aura;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
import pw.latematt.xiv.mod.mods.combat.aura.mode.modes.SemiMulti;
import pw.latematt.xiv.mod.mods.combat.aura.mode.modes.Singular;
import pw.latematt.xiv.mod.mods.combat.aura.mode.modes.Switch;
import pw.latematt.xiv.mod.mods.player.AutoHeal;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.EntityUtils;
import pw.latematt.xiv.value.ClampedValue;
import pw.latematt.xiv.value.Value;

import java.util.Random;

/**
 * @author Matthew
 */
public class KillAura extends Mod implements CommandHandler {
    private final Listener motionUpdateListener, sendPacketListener, playerDeathListener;
    public final ClampedValue<Long> delay = new ClampedValue<>("killaura_delay", 200L, 0L, 1000L);
    public final ClampedValue<Long> randomDelay = new ClampedValue<>("killaura_random_delay", 0L, 0L, 1000L);
    public final ClampedValue<Double> range = new ClampedValue<>("killaura_range", 3.8D, 3.0D, 6.0D);
    public final ClampedValue<Integer> fov = new ClampedValue<>("killaura_fov", 360, 0, 360);
    public final ClampedValue<Integer> ticksToWait = new ClampedValue<>("killaura_ticks_to_wait", 20, 0, 1000);
    private final Value<Boolean> players = new Value<>("killaura_players", true);
    private final Value<Boolean> mobs = new Value<>("killaura_mobs", false);
    private final Value<Boolean> animals = new Value<>("killaura_animals", false);
    private final Value<Boolean> invisible = new Value<>("killaura_invisible", false);
    private final Value<Boolean> team = new Value<>("killaura_team", true);
    private final Value<Boolean> friends = new Value<>("killaura_friends", false);
    private final Value<Boolean> admins = new Value<>("killaura_admins", false);
    public final Value<Boolean> silent = new Value<>("killaura_silent", true);
    private final Value<Boolean> toggleDeath = new Value<>("killaura_toggle_death", false);
    public final Value<Boolean> autoBlock = new Value<>("killaura_auto_block", false);
    public final Value<Boolean> weaponOnly = new Value<>("killaura_weapon_only", false);
    public final Value<Boolean> witherFriend = new Value<>("killaura_wither_friend", false);
    private final Random random = new Random();
    private final Value<AuraMode> currentMode = new Value<>("killaura_mode", new Singular(this));

    public KillAura() {
        super("Kill Aura", ModType.COMBAT, Keyboard.KEY_R, 0xFFC6172B);
        Command.newCommand().cmd("killaura").description("Base command for the Kill Aura mod.").aliases("killa", "ka").arguments("<action>").handler(this).build();

        motionUpdateListener = new Listener<MotionUpdateEvent>() {
            @Override
            public void onEventCalled(MotionUpdateEvent event) {
                if (event.getCurrentState() == MotionUpdateEvent.State.PRE) {
                    if (autoBlock.getValue()) {
                        boolean entitiesNearby = mc.theWorld.loadedEntityList.stream()
                                .filter(entity -> entity instanceof EntityLivingBase)
                                .filter(entity -> isValidEntity((EntityLivingBase) entity, 6))
                                .findFirst().isPresent();
                        if (entitiesNearby) {
                            ItemStack currentItem = mc.thePlayer.getCurrentEquippedItem();
                            if (currentItem != null && currentItem.getItem() instanceof ItemSword) {
                                mc.thePlayer.setItemInUse(currentItem, currentItem.getMaxItemUseDuration());
                            }
                        }
                    }

                    currentMode.getValue().onPreMotionUpdate(event);
                } else if (event.getCurrentState() == MotionUpdateEvent.State.POST) {
                    currentMode.getValue().onPostMotionUpdate(event);
                }
            }
        };

        sendPacketListener = new Listener<SendPacketEvent>() {
            @Override
            public void onEventCalled(SendPacketEvent event) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();
                    currentMode.getValue().onMotionPacket(player);
                }
            }
        };

        playerDeathListener = new Listener<PlayerDeathEvent>() {
            @Override
            public void onEventCalled(PlayerDeathEvent event) {
                if (toggleDeath.getValue())
                    toggle();
            }
        };

        setTag(currentMode.getValue().getName());
    }

    public void attack(EntityLivingBase target) {
        final ItemStack currentItem = mc.thePlayer.inventory.getCurrentItem();
        // stop sprinting
        // and stop sneaking
        // and stop blocking
        final boolean wasSprinting = mc.thePlayer.isSprinting();
        final boolean wasSneaking = mc.thePlayer.isSneaking();
        final boolean wasBlocking = currentItem != null &&
                currentItem.getItem().getItemUseAction(currentItem) == EnumAction.BLOCK &&
                mc.thePlayer.isBlocking();
        if (wasSprinting)
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        if (wasSneaking)
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        if (wasBlocking)
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

        int oldDamage = 0;
        if (currentItem != null)
            oldDamage = currentItem.getItemDamage();

        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, target);

        if (currentItem != null)
            currentItem.setItemDamage(oldDamage);

        // continue sprinting
        // and continue sneaking
        // and continue blocking
        if (wasSprinting)
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        if (wasSneaking)
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        if (wasBlocking)
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(currentItem));
        mc.thePlayer.setSprinting(wasSprinting);
        mc.thePlayer.setSneaking(wasSneaking);
        if (wasBlocking)
            mc.thePlayer.setItemInUse(currentItem, currentItem.getMaxItemUseDuration());
    }

    public boolean isValidEntity(EntityLivingBase entity) {
        return isValidEntity(entity, range.getValue());
    }

    public boolean isValidEntity(EntityLivingBase entity, double range) {
        if (!checkWeapon(mc.thePlayer.getHeldItem()))
            return false;
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
        if (entity.ticksExisted < ticksToWait.getValue())
            return false;
        if (EntityUtils.getReference().getDistanceToEntity(entity) > range)
            return false;
        if (!invisible.getValue() && entity.isInvisibleToPlayer(mc.thePlayer))
            return false;
        if (!team.getValue() && entity.getTeam() != null && entity.getTeam().isSameTeam(mc.thePlayer.getTeam()))
            return false;
        // 85.136.70.107

        if (entity instanceof EntityWither && this.witherFriend.getValue() && !this.team.getValue()) {
            EntityWither wither = (EntityWither) entity;
            String witherFormatCode = wither.getCustomNameTag().substring(0, 2);
            String playerFormatCode = this.mc.thePlayer.getDisplayName().getFormattedText().substring(0, 2);
            if (witherFormatCode.contains("\247") && playerFormatCode.contains("\247")) {
                if (witherFormatCode.equalsIgnoreCase(playerFormatCode)) {
                    return false;
                }
            }
        }

        if (entity instanceof EntityPlayer) {
            if (!friends.getValue() && XIV.getInstance().getFriendManager().isFriend(entity.getName()))
                return false;
            if (!admins.getValue() && XIV.getInstance().getAdminManager().isAdmin(entity.getName()))
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

    public boolean checkWeapon(ItemStack itemStack) {
        if (!weaponOnly.getValue())
            return true;
        if (itemStack != null) {
            Item item = itemStack.getItem();
            return item instanceof ItemSword || item instanceof ItemAxe;
        }

        return false;
    }

    public Long getDelay() {
        if (randomDelay.getValue() == 0)
            return delay.getValue();
        return delay.getValue() - random.nextInt(randomDelay.getValue().intValue());
    }

    public boolean isHealing() {
        AutoHeal autoHeal = (AutoHeal) XIV.getInstance().getModManager().find("autoheal");
        return autoHeal != null && autoHeal.isHealing();
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
                            delay.setValue(newDelay);
                            if (delay.getValue() > delay.getMax())
                                delay.setValue(delay.getMax());
                            else if (delay.getValue() < delay.getMin())
                                delay.setValue(delay.getMin());

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
                            randomDelay.setValue(newRandomDelay);
                            if (randomDelay.getValue() > randomDelay.getMax())
                                randomDelay.setValue(randomDelay.getMax());
                            else if (randomDelay.getValue() < randomDelay.getMin())
                                randomDelay.setValue(randomDelay.getMin());

                            ChatLogger.print(String.format("Kill Aura Random Delay set to %sms", randomDelay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newRandomDelayString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura randomdelay <number>");
                    }
                    break;
                case "attackspersecond":
                case "aps":
                    if (arguments.length >= 3) {
                        String newApsString = arguments[2];
                        try {
                            long newAps = arguments[2].equalsIgnoreCase("-d") ? delay.getDefault() : Long.parseLong(newApsString);
                            delay.setValue(1000 / newAps);
                            if (delay.getValue() > delay.getMax())
                                delay.setValue(delay.getMax());
                            else if (delay.getValue() < delay.getMin())
                                delay.setValue(delay.getMin());

                            ChatLogger.print(String.format("Kill Aura Attacks/second set to %s (%sms)", newAps, delay.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newApsString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura aps <number>");
                    }
                    break;
                case "reach":
                case "range":
                case "r":
                    if (arguments.length >= 3) {
                        String newRangeString = arguments[2];
                        try {
                            double newRange = arguments[2].equalsIgnoreCase("-d") ? range.getDefault() : Double.parseDouble(newRangeString);
                            range.setValue(newRange);
                            if (range.getValue() > range.getMax())
                                range.setValue(range.getMax());
                            else if (range.getValue() < range.getMin())
                                range.setValue(range.getMin());

                            ChatLogger.print(String.format("Kill Aura Range set to %s", range.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newRangeString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura range <number>");
                    }
                    break;
                case "fov":
                    if (arguments.length >= 3) {
                        String newFOVString = arguments[2];
                        try {
                            int newFOV = arguments[2].equalsIgnoreCase("-d") ? fov.getDefault() : Integer.parseInt(newFOVString);
                            fov.setValue(newFOV);
                            if (fov.getValue() > fov.getMax())
                                fov.setValue(fov.getMax());
                            else if (fov.getValue() < fov.getMin())
                                fov.setValue(fov.getMin());

                            ChatLogger.print(String.format("Kill Aura FOV set to %s", fov.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newFOVString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura fov <number>");
                    }
                    break;
                case "tickstowait":
                case "ticks":
                    if (arguments.length >= 3) {
                        String newTicksString = arguments[2];
                        try {
                            int newTicks = arguments[2].equalsIgnoreCase("-d") ? ticksToWait.getDefault() : Integer.parseInt(newTicksString);
                            ticksToWait.setValue(newTicks);
                            if (ticksToWait.getValue() > ticksToWait.getMax())
                                ticksToWait.setValue(ticksToWait.getMax());
                            else if (range.getValue() < ticksToWait.getMin())
                                ticksToWait.setValue(ticksToWait.getMin());

                            ChatLogger.print(String.format("Kill Aura Ticks to Wait set to %s", ticksToWait.getValue()));
                        } catch (NumberFormatException e) {
                            ChatLogger.print(String.format("\"%s\" is not a number.", newTicksString));
                        }
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura tickstowait <number>");
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
                case "admins":
                case "admns":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            admins.setValue(admins.getDefault());
                        } else {
                            admins.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        admins.setValue(!admins.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s attack admins.", (admins.getValue() ? "now" : "no longer")));
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
                case "weapononly":
                case "wo":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            weaponOnly.setValue(weaponOnly.getDefault());
                        } else {
                            weaponOnly.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        weaponOnly.setValue(!weaponOnly.getValue());
                    }
                    ChatLogger.print(String.format("Kill Aura will %s function only while holding a sword.", (weaponOnly.getValue() ? "now" : "no longer")));
                    break;
                case "mode":
                    if (arguments.length >= 3) {
                        String mode = arguments[2];
                        switch (mode.toLowerCase()) {
                            case "singular":
                                currentMode.setValue(new Singular(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", currentMode.getValue().getName()));
                                break;
                            case "switch":
                                currentMode.setValue(new Switch(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", currentMode.getValue().getName()));
                                break;
                            case "multi":
                                currentMode.setValue(new Multi(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", currentMode.getValue().getName()));
                                break;
                            case "semimulti":
                                currentMode.setValue(new SemiMulti(this));
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", currentMode.getValue().getName()));
                                break;
                            case "-d":
                                currentMode.setValue(currentMode.getDefault());
                                ChatLogger.print(String.format("Kill Aura Mode set to %s", currentMode.getValue().getName()));
                                break;
                            default:
                                ChatLogger.print("Invalid mode, valid: singular, switch, multi, semimulti");
                                break;
                        }
                        setTag(currentMode.getValue().getName());
                    } else {
                        ChatLogger.print("Invalid arguments, valid: killaura mode <mode>");
                    }
                    break;
                case "witherfriend":
                case "wither":
                case "wfriend":
                case "wf":
                    if (arguments.length >= 3) {
                        if (arguments[2].equalsIgnoreCase("-d")) {
                            this.witherFriend.setValue(this.witherFriend.getDefault());
                        } else {
                            this.witherFriend.setValue(Boolean.parseBoolean(arguments[2]));
                        }
                    } else {
                        this.witherFriend.setValue(!this.witherFriend.getValue());
                    }

                    ChatLogger.print(String.format("Kill Aura will %s target friendly withers. [Hypixel]", (!this.witherFriend.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: delay, randomdelay, attackspersecond, range, fov, tickstowait, players, mobs, animals, invisible, team, silent, autoblock, weapononly, mode");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: killaura <action>");
        }
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(motionUpdateListener, sendPacketListener, playerDeathListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(motionUpdateListener, sendPacketListener, playerDeathListener);
        currentMode.getValue().onDisabled();
    }
}