package pw.latematt.xiv.ui.alt;
//package pw.latematt.xiv.ui.alt;
//
//import java.io.IOException;
//import java.util.Map.Entry;
//
//import org.lwjgl.input.Keyboard;
//import org.lwjgl.input.Mouse;
//
//import net.minecraft.client.audio.PositionedSoundRecord;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.util.ResourceLocation;
//import pw.latematt.xiv.XIV;
//
//public class GuiAltManager extends GuiScreen {
//
//	private GuiScreen parent;
//	private int scroll = 0, selected = -1;
//	
//	public GuiAltManager(GuiScreen parent) {
//		this.parent = parent;
//		
//		this.selected = 0;
//		
//		Keyboard.enableRepeatEvents(true);
//	}
//	
//	@Override
//	public void onGuiClosed() {
//		super.onGuiClosed();
//		
//		Keyboard.enableRepeatEvents(false);
//	}
//	
//	@Override
//	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//		super.drawDefaultBackground();
//		
//		super.drawScreen(mouseX, mouseY, partialTicks);
//
//		Gui.drawRect(0, 40, width, height - 40, 0xFF232323);
//		Gui.drawRect(0, 0, width, height, 0xFF232323);
//		
//		int slot = 0;
//		float y = scroll;
//		for(Entry<String, String> alt: XIV.getInstance().getAltManager().getContents().entrySet()) {
//			Gui.drawRect(2, 42 + (int) y, width - 2, 42 + 20 + (int) y, (slot == selected ? isOverSlot(mouseX, mouseY, y) ? 0x99FFFFFF : 0x88FFFFFF : isOverSlot(mouseX, mouseY, y) ? 0x66FFFFFF : 0x55FFFFFF));
//			
//			if(y > -14) {
//				mc.fontRendererObj.drawStringWithShadow(alt.getKey(), width / 2 - (mc.fontRendererObj.getStringWidth(alt.getKey()) / 2), 48 + y, 0xFFFFFFFF);
//			}
//			
//			y += 20;
//			slot++;
//		}
//		
//		Gui.drawRect(0, 0, width, 42, 0xFF232323);
//		Gui.drawRect(0, height - 40, width, height, 0xFF232323);
//	}
//	
//	@Override
//	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//		super.mouseClicked(mouseX, mouseY, mouseButton);
//		
//		int slot = 0;
//		float y = scroll;
//		for(Entry<String, String> alt: XIV.getInstance().getAltManager().getContents().entrySet()) {
//			if(isOverSlot(mouseX, mouseY, y) && mouseButton == 0) {
//	            mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
//				selected = slot;
//			}
//			
//			y += 20;
//			slot++;
//		}
//	}
//	
//	public boolean isOverSlot(int mouseX, int mouseY, float slotHeight) {
//		return mouseX > 2 && mouseY > 42 + slotHeight && mouseX < width - 2 && mouseY < 42 + 20 + slotHeight;
//	}
//	
//	@Override
//	protected void keyTyped(char typedChar, int keyCode) throws IOException {
//		super.keyTyped(typedChar, keyCode);
//	}
//	
//	@Override
//	public void handleMouseInput() throws IOException {
//		super.handleMouseInput();
//		
//		int wheel = Mouse.getEventDWheel();
//		
//		if(wheel > 0) {
//			for(int i = 0; i < 7; i++) {
//				if(scroll < 0) {
//					scroll++;
//				}
//			}
//		}else if(wheel < 0) {
//			for(int i = 0; i < 7; i++) {
//				if(scroll > -(99999 /* max alts (height of each slot is 20) */)) {
//					scroll--;
//				}
//			}
//		}
//	}
//	
//	@Override
//	public void handleKeyboardInput() throws IOException {
//		super.handleKeyboardInput();
//		
//		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
//			for(int i = 0; i < 7; i++) {
//				if(scroll > -(99999 /* max alts (height of each slot is 20) */)) {
//					scroll--;
//				}
//			}
//		}
//		
//		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
//			for(int i = 0; i < 7; i++) {
//				if(scroll < 0) {
//					scroll++;
//				}
//			}
//		}
//	}
//}
