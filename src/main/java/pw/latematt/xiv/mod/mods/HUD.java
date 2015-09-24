package pw.latematt.xiv.mod.mods;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.IngameHUDRenderEvent;
import pw.latematt.xiv.management.file.XIVFile;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.value.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * I HATE FUCKING NORMIES AND RUDY IS A FUCKING NORMIE REEEEEEEEEE
 * @author Matthew
 */
public class HUD extends Mod implements Listener<IngameHUDRenderEvent> {
    private final Value<Boolean> watermark = new Value<>("hud_watermark", false);
    private final Value<Boolean> arraylist = new Value<>("hud_arraylist", true);
    private final Value<Boolean> coords = new Value<>("hud_coords", true);
    private final Value<Boolean> fps = new Value<>("hud_fps", true);
    private final Value<Boolean> ign = new Value<>("hud_ign", false);
    private final Value<Boolean> time = new Value<>("hud_time", true);
    private final Value<Boolean> potions = new Value<>("hud_potions", true);
    private final Value<Boolean> armor = new Value<>("hud_armor", true);
    private final Value<Boolean> rudysucks = new Value<>("hud_rudysucks", false);
    private final XIVFile hudConfigFile;

    public HUD() {
        super("HUD", new String[]{"<action>"}, new String[]{});
        setEnabled(true);

        hudConfigFile = new XIVFile("hudconfig", "json") {
            @Override
            @SuppressWarnings("unchecked")
            public void load() throws IOException {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                List<Value> values = gson.fromJson(reader, new TypeToken<List<Value>>() {}.getType());
                for (Value value : values) {
                    XIV.getInstance().getValueManager().getContents().stream().filter(value1 -> value.getName().equals(value1.getName())).forEach(value1 -> {
                        value1.setValue(value.getValue());
                    });
                }
            }

            @Override
            public void save() throws IOException {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                List<Value> values = XIV.getInstance().getValueManager().getContents().stream().filter(value -> value.getName().startsWith("hud_")).collect(Collectors.toList());
                Files.write(gson.toJson(values).getBytes("UTF-8"), file);
            }
        };

        try {
            hudConfigFile.load();
        } catch (IOException e) {
            XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not load, a stack trace has been printed.", hudConfigFile.getName(), hudConfigFile.getExtension()));
            e.printStackTrace();
        }
    }

    @Override
    public void onEventCalled(IngameHUDRenderEvent event) {
        ScaledResolution scaledResolution = XIV.getInstance().newScaledResolution();
        GlStateManager.enableBlend();
        if (watermark.getValue()) {
            mc.fontRendererObj.drawStringWithShadow("XIV", 2, 2, 0xFFFFFFFF);
        }
        if (time.getValue()) {
            int x = 2;
            if (watermark.getValue())
                x += mc.fontRendererObj.getStringWidth("XIV ");
            else if (rudysucks.getValue())
                x += mc.fontRendererObj.getStringWidth("rudy sucks ");
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            mc.fontRendererObj.drawStringWithShadow(format.format(new Date()), x, 2, 0xDDDDDDDD);
        }
        if (rudysucks.getValue()) {
            int y = 2;
            if (watermark.getValue() && time.getValue())
                y += 8;
            mc.fontRendererObj.drawStringWithShadow("\2471r\2472u\2473d\2474y \2475s\2476u\2477c\2478k\2479s", 2, y, 0xFFFFFFFF);
        }

        drawInfo(scaledResolution);
        drawArraylist(scaledResolution);
        drawPotions(scaledResolution);
        drawArmor(scaledResolution);
        GlStateManager.disableBlend();
    }

    private void drawArmor(ScaledResolution scaledResolution) {
        if (mc.playerController.isNotCreative()) {
            int x = 15;

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.disableBlend();
            RenderHelper.enableStandardItemLighting();
            for (int index = 3; index >= 0; index--) {
                final ItemStack stack = mc.thePlayer.inventory.armorInventory[index];
                if (stack != null) {
                    int height = scaledResolution.getScaledHeight()
                            - (mc.thePlayer.isInsideOfMaterial(Material.water) ? mc.thePlayer.getActivePotionEffect(Potion.absorption) != null ? 75 : 65 : 55);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, scaledResolution.getScaledWidth() / 2 + x, height);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, scaledResolution.getScaledWidth() / 2 + x, height);
                    x += 18;
                }
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }

    private void drawPotions(ScaledResolution scaledResolution) {
        int x = scaledResolution.getScaledWidth() - 2;
        int y = scaledResolution.getScaledHeight() - 10;
        for (Object o : mc.thePlayer.getActivePotionEffects()) {
            PotionEffect effect = (PotionEffect) o;
            String name = I18n.format(effect.getEffectName());

            if (effect.getAmplifier() == 1) {
                name = name + " " + I18n.format("enchantment.level.2");
            } else if (effect.getAmplifier() == 2) {
                name = name + " " + I18n.format("enchantment.level.3");
            } else if (effect.getAmplifier() == 3) {
                name = name + " " + I18n.format("enchantment.level.4");
            } else {
                name = name + " " + (effect.getAmplifier() + 1);
            }

            name = String.format("%s (%s)", name, Potion.getDurationString(effect));
            mc.fontRendererObj.drawStringWithShadow(name, x - mc.fontRendererObj.getStringWidth(name), y, Potion.potionTypes[effect.getPotionID()].getLiquidColor());
            y -= 10;
        }
    }

    private void drawArraylist(ScaledResolution scaledResolution) {
        int x = scaledResolution.getScaledWidth() - 2;
        int y = 2;
        for (Mod mod : XIV.getInstance().getModManager().getContents()) {
            if (!mod.isVisible() || !mod.isEnabled())
                continue;
            mc.fontRendererObj.drawStringWithShadow(mod.getName(), x - mc.fontRendererObj.getStringWidth(mod.getName()), y, mod.getColor());
            y += 10;
        }
    }

    private void drawInfo(ScaledResolution scaledResolution) {
        List<String> info = new ArrayList<>();

        if (fps.getValue()) {
            info.add(String.format("\2477FPS\247r: %s", mc.debug.split(" fps")[0]));
        }
        if (ign.getValue()) {
            info.add(String.format("\2477IGN\247r: %s", mc.getSession().getUsername()));
        }
        if (coords.getValue()) {
            info.add(String.format("\2477XYZ\247r: %s, %s, %s", MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.posY), MathHelper.floor_double(mc.thePlayer.posZ)));
        }

        int y = scaledResolution.getScaledHeight() - 10;
        for (String infoString : info) {
            mc.fontRendererObj.drawStringWithShadow(infoString, 2, y, 0xFFFFFFFF);
            y -= 9;
        }
    }

    @Override
    public void onCommandRan(String message) {
        String[] arguments = message.split(" ");
        if (arguments.length >= 2) {
            String action = arguments[1];
            switch (action) {
                case "watermark":
                    watermark.setValue(!watermark.getValue());
                    ChatLogger.print(String.format("HUD will %s display the watermark.", (watermark.getValue() ? "now" : "no longer")));
                    break;
                case "arraylist":
                    arraylist.setValue(!arraylist.getValue());
                    ChatLogger.print(String.format("HUD will %s display the arraylist.", (arraylist.getValue() ? "now" : "no longer")));
                    break;
                case "coords":
                    coords.setValue(!coords.getValue());
                    ChatLogger.print(String.format("HUD will %s display your coords.", (coords.getValue() ? "now" : "no longer")));
                    break;
                case "fps":
                    fps.setValue(!fps.getValue());
                    ChatLogger.print(String.format("HUD will %s display your FPS.", (fps.getValue() ? "now" : "no longer")));
                    break;
                case "ign":
                    ign.setValue(!ign.getValue());
                    ChatLogger.print(String.format("HUD will %s display your in-game name.", (ign.getValue() ? "now" : "no longer")));
                    break;
                case "time":
                    time.setValue(!time.getValue());
                    ChatLogger.print(String.format("HUD will %s display the current time.", (time.getValue() ? "now" : "no longer")));
                    break;
                case "potions":
                    potions.setValue(!potions.getValue());
                    ChatLogger.print(String.format("HUD will %s display potion status.", (potions.getValue() ? "now" : "no longer")));
                    break;
                case "armor":
                    armor.setValue(!armor.getValue());
                    ChatLogger.print(String.format("HUD will %s display armor status.", (armor.getValue() ? "now" : "no longer")));
                    break;
                case "rudysucks":
                    rudysucks.setValue(!rudysucks.getValue());
                    ChatLogger.print(String.format("HUD will %s display \"rudy sucks\".", (rudysucks.getValue() ? "now" : "no longer")));
                    break;
                default:
                    ChatLogger.print("Invalid action, valid: watermark, arraylist, coords, fps, ign, time, potions, armor");
                    break;
            }
        } else {
            ChatLogger.print("Invalid arguments, valid: hud <action>");
        }
        try {
            hudConfigFile.save();
        } catch (IOException e) {
            XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not save, a stack trace has been printed.", hudConfigFile.getName(), hudConfigFile.getExtension()));
            e.printStackTrace();
        }
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
