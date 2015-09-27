package pw.latematt.xiv.mod.mods;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.GuiScreenEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.ModType;

public class ClickGUI extends Mod implements Listener<GuiScreenEvent> {
	
	private GuiClick screen;
	
	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT);
		this.setEnabled(false);
	}

	@Override
	public void onEnabled() {			
		if(!mc.inGameHasFocus) {
			this.setEnabled(false);
			return;
		}
		
		if(screen == null) {		
			/**
			 * Added manually because latematt is bad irl and orl.
			 */
			
			HashMap<Mod, ModType> mods = new HashMap<Mod, ModType>();
			mods.put(XIV.getInstance().getModManager().find(AutoBlock.class), ModType.PLAYER);
			mods.put(XIV.getInstance().getModManager().find(ESP.class), ModType.RENDER);
			mods.put(XIV.getInstance().getModManager().find(HUD.class), ModType.RENDER);
			mods.put(XIV.getInstance().getModManager().find(KillAura.class), ModType.COMBAT);
			mods.put(XIV.getInstance().getModManager().find(NoSlowdown.class), ModType.MOVEMENT);
			mods.put(XIV.getInstance().getModManager().find(Step.class), ModType.MOVEMENT);
			mods.put(XIV.getInstance().getModManager().find(Velocity.class), ModType.COMBAT);
			
			this.screen = new GuiClick(mods);
		}
		
		XIV.getInstance().getListenerManager().add(this);
		
		this.mc.displayGuiScreen(screen);
	}

	@Override
	public void onDisabled() {
		XIV.getInstance().getListenerManager().remove(this);
	}

	@Override
	public void onEventCalled(GuiScreenEvent event) {
		if(!(event.getScreen() instanceof GuiClick)) {
			this.setEnabled(false);
		}
	}
}
