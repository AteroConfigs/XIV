package pw.latematt.xiv.ui.tabgui;

import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.KeyPressEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.tabgui.tab.GuiItem;
import pw.latematt.xiv.ui.tabgui.tab.GuiTab;
import pw.latematt.xiv.utils.RenderUtils;

/**
 * @author Ddong
 */
public class GuiTabHandler implements Listener<KeyPressEvent> {
    private List<GuiTab> tabs;
    private int selected;
    private boolean opened;
    private int width;

    public void setup() {
	this.tabs = Lists.newArrayList();
	this.selected = 0;
	this.width = 0;
	for (ModType type : ModType.values()) {
	    String name = type.getName();
	    GuiTab tab = new GuiTab(name);
	    for (Mod mod : XIV.getInstance().getModManager().getContents()) {
		if (mod.getModType().equals(type)) { 
		    tab.addButton(new GuiItem(mod));
		}
	    }

	    this.tabs.add(tab);
	}

	this.tabs.sort(new Comparator<GuiTab>() {

	    @Override
	    public int compare(GuiTab o1, GuiTab o2) {
		return o1.getTabName().compareTo(o2.getTabName());
	    }
	});
    }

    public void drawGui(int startX, int startY) {
	int y = startY;
	FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
	for (GuiTab tab : this.tabs) {
	    y += 12;
	    if (font.getStringWidth(tab.getTabName()) < this.width) {
		continue;
	    }

	    this.width = font.getStringWidth(tab.getTabName());
	}

	RenderUtils.drawHollowRect(startX, startY, this.width + 6, y, 1.0f, -16777216);
	int height = startY;
	for (GuiTab tab : this.tabs) {
	    tab.drawTab(startX + 2, height + 2, this.width, this.tabs.get(this.selected) == tab && this.opened, this.tabs.get(this.selected) == tab);
	    tab.drawButtons(startY, this.width, this.tabs.get(this.selected) == tab && this.opened, this.tabs.get(this.selected) == tab);
	    height += 12;
	}
    }

    @Override
    public void onEventCalled(KeyPressEvent event) {
	if (event.getKeyCode() == Keyboard.KEY_LEFT) {
            this.opened = false;
        }
	
        for (GuiTab tab : this.tabs) {
            if (this.opened && this.tabs.get(this.selected) == tab) {
                tab.keyboard(event.getKeyCode());
            }
        }
        
        if (this.opened) {
            return;
        }
        
        if (event.getKeyCode() == Keyboard.KEY_DOWN) {
            ++this.selected;
            if (this.selected > this.tabs.size() - 1) {
                this.selected = 0;
            }
        }
        
        if (event.getKeyCode() == Keyboard.KEY_UP) {
            --this.selected;
            if (this.selected < 0) {
                this.selected = this.tabs.size() - 1;
            }
        }
        
        if (event.getKeyCode() == Keyboard.KEY_RIGHT || event.getKeyCode() == Keyboard.KEY_RETURN) {
            this.opened = true;
        }
    }

    public int getTabHeight() {
	return 12;
    }
}
