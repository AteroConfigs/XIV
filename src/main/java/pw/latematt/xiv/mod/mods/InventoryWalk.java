package pw.latematt.xiv.mod.mods;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
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
        if (mc.currentScreen == null) return;

        if (mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign || mc.currentScreen instanceof GuiScreenBook) return;

        KeyBinding[] keyBindings = {mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump};

        for (KeyBinding keyBinding : keyBindings) {
            keyBinding.setKeyBindState(keyBinding.getKeyCode(), Keyboard.isKeyDown(keyBinding.getKeyCode()));
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            mc.thePlayer.rotationPitch--;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            mc.thePlayer.rotationPitch++;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            mc.thePlayer.rotationYaw--;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            mc.thePlayer.rotationYaw++;
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
