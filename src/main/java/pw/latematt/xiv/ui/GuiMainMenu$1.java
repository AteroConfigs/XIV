package pw.latematt.xiv.ui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import pw.latematt.xiv.ui.alt.GuiAltManager;

public class GuiMainMenu$1 extends GuiMainMenu {

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {        
		super.actionPerformed(button);
		
        if(button.id == 5) {
            this.mc.displayGuiScreen(new GuiAltManager(this));
        }
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
