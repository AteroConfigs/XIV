package pw.latematt.xiv.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

/**
 * @author Matthew
 */
public class ItemUtils {
    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    /* normal stuff */
    public static void getFromInventory(Item item) {
        for (int index = 9; index <= 36; index++) {
            ItemStack stack = MINECRAFT.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null)
                continue;
            if (stack.getItem() == item) {
                MINECRAFT.playerController.windowClick(0, index, 0, 1, MINECRAFT.thePlayer);
                break;
            }
        }
    }

    public static boolean hotbarHas(Item item) {
        for (int index = 0; index <= 8; index++) {
            ItemStack stack = MINECRAFT.thePlayer.inventory.getStackInSlot(index);
            if (stack == null)
                continue;
            if (stack.getItem() == item)
                return true;
        }
        return false;
    }

    public static void useFirst(Item item) {
        for (int index = 0; index <= 8; index++) {
            ItemStack stack = MINECRAFT.thePlayer.inventory.getStackInSlot(index);
            if (stack == null)
                continue;
            if (stack.getItem() == item) {
                int oldItem = MINECRAFT.thePlayer.inventory.currentItem;
                MINECRAFT.thePlayer.inventory.currentItem = index;
                MINECRAFT.playerController.updateController();
                MINECRAFT.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MINECRAFT.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                MINECRAFT.thePlayer.inventory.currentItem = oldItem;
                MINECRAFT.playerController.updateController();
                break;
            }
        }
    }

    public static void dropFirst(Item item) {
        for (int index = 0; index <= 8; index++) {
            ItemStack stack = MINECRAFT.thePlayer.inventory.getStackInSlot(index);
            if (stack == null)
                continue;
            if (stack.getItem() == item) {
                MINECRAFT.playerController.windowClick(0, 36 + index, 1, 4, MINECRAFT.thePlayer);
                break;
            }
        }
    }

    /* instanthealth stuff (used in AutoHeal) */
    public static boolean isValidHealthPotion(ItemStack stack) {
        if (stack == null)
            return false;
        if (!(stack.getItem() instanceof ItemPotion))
            return false;
        ItemPotion potion = (ItemPotion) stack.getItem();
        if (!ItemPotion.isSplash(stack.getItemDamage()))
            return false;
        if (potion.getEffects(stack) == null)
            return false;
        for (Object o : potion.getEffects(stack)) {
            PotionEffect effect = (PotionEffect) o;
            if (effect.getPotionID() == Potion.INSTANT_HEALTH.getId())
                return true;
        }
        return false;
    }

    public static void getInstantHealthFromInventory() {
        for (int index = 9; index <= 36; index++) {
            ItemStack stack = MINECRAFT.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null)
                continue;
            if (isValidHealthPotion(stack)) {
                MINECRAFT.playerController.windowClick(0, index, 0, 1, MINECRAFT.thePlayer);
                break;
            }
        }
    }

    public static boolean hotbarHasInstantHealth() {
        for (int index = 0; index <= 8; index++) {
            ItemStack stack = MINECRAFT.thePlayer.inventory.getStackInSlot(index);
            if (stack == null)
                continue;
            if (isValidHealthPotion(stack))
                return true;
        }
        return false;
    }

    public static void useFirstInstantHealth() {
        for (int index = 0; index <= 8; index++) {
            ItemStack stack = MINECRAFT.thePlayer.inventory.getStackInSlot(index);
            if (stack == null)
                continue;
            if (isValidHealthPotion(stack)) {
                int oldItem = MINECRAFT.thePlayer.inventory.currentItem;
                MINECRAFT.thePlayer.inventory.currentItem = index;
                MINECRAFT.playerController.updateController();
                MINECRAFT.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MINECRAFT.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                MINECRAFT.thePlayer.inventory.currentItem = oldItem;
                MINECRAFT.playerController.updateController();
                break;
            }
        }
    }
}
