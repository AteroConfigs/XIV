package pw.latematt.xiv.ui.alt;

import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import pw.latematt.xiv.XIV;

public class AltSlot extends GuiSlot {

	private GuiAltManager screen;
	private int selected;
	
	public AltSlot(GuiAltManager screen, Minecraft mcIn, int width, int height, int p_i1052_4_, int p_i1052_5_, int p_i1052_6_) {
		super(mcIn, width, height, p_i1052_4_, p_i1052_5_, p_i1052_6_);
		
		this.screen = screen;
	}

	@Override
	protected int getSize() {
		return XIV.getInstance().getAltManager().getContents().size() * 16;
	}
	
	@Override
	public int getSlotHeight() {
		return super.getSlotHeight();
	}

	@Override
	protected void elementClicked(int slot, boolean var2, int var3, int var4) {
		this.selected = slot;
	}

	@Override
	protected boolean isSelected(int slot) {
		return selected == slot;
	}

	@Override
	protected void drawBackground() {
		
	}

	@Override
	protected void drawSlot(int slot, int x, int y, int var4, int var5, int var6) {
		Entry<String, String> alt = getAlt(slot);
		
		if(alt != null) {
			mc.fontRendererObj.drawStringWithShadow(alt.getKey() + "", x + 1, y + 1, 0xFFFFFFFF);
		}
	}
	
	public Entry<String, String> getAlt(int slot) {
		int count = 0;
		for(Entry<String, String> alt: XIV.getInstance().getAltManager().getContents().entrySet()) {
			if(count == slot) {
				return alt;
			}
			count++;
		}
		
		return null;
	}
}
