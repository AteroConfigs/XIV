package pw.latematt.xiv.ui.alt;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiAltManager extends GuiScreen {

	private GuiScreen parent;
	private AltSlot slot;
	
	public GuiAltManager(GuiScreen parent) {
		this.parent = parent;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		this.slot = new AltSlot(this, mc, width, height, 25, height - 98, 16);
		this.slot.registerScrollButtons(7, 8);
		
		this.buttonList.add(new GuiButton(0, 2, height - 4, "Login"));
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		if(button.enabled) {
			if(button.id == 0) {
				/** LOGIN HERE */
			}
		}
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		this.slot.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
	
		this.slot.func_178039_p();
	}
}
