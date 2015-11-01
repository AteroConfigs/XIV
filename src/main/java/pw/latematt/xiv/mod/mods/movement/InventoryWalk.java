package pw.latematt.xiv.mod.mods.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;

/**
 * @author TehNeon
 */
public class InventoryWalk extends Mod implements Listener<MotionUpdateEvent> {
    public InventoryWalk() {
        super("Inventory Walk", ModType.MOVEMENT);
    }

    public void onEventCalled(MotionUpdateEvent event) {
        if (mc.currentScreen == null)
            return;

        if (mc.currentScreen instanceof GuiChat)
            return;

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            mc.thePlayer.rotationPitch = MathHelper.clamp_float(mc.thePlayer.rotationPitch - 4, -90.0F, 90.0F);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            mc.thePlayer.rotationPitch = MathHelper.clamp_float(mc.thePlayer.rotationPitch + 4, -90.0F, 90.0F);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            mc.thePlayer.rotationYaw -= 4;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            mc.thePlayer.rotationYaw += 4;
        }

        if (mc.currentScreen instanceof GuiEditSign || mc.currentScreen instanceof GuiScreenBook || mc.currentScreen instanceof GuiRepair)
            return;

        KeyBinding[] keyBindings = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump};

        for (KeyBinding keyBinding : keyBindings) {
            KeyBinding.setKeyBindState(keyBinding.getKeyCode(), Keyboard.isKeyDown(keyBinding.getKeyCode()));
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
