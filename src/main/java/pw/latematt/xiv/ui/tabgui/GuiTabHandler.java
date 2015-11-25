package pw.latematt.xiv.ui.tabgui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.KeyPressEvent;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.tabgui.tab.GuiItem;
import pw.latematt.xiv.ui.tabgui.tab.GuiTab;
import pw.latematt.xiv.utils.RenderUtils;

import java.util.List;

/**
 * @author Ddong
 *         <p>
 *         private int colourBody = 0x950C1A26;
 *         private int colourBox = 0x804DB3FF;
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
            XIV.getInstance().getModManager().getContents().stream()
                    .filter(mod -> mod.getModType().equals(type))
                    .filter(mod -> !mod.getName().equals("ClickGUI"))
                    .forEach(mod -> tab.addButton(new GuiItem(mod)));
            this.tabs.add(tab);
        }

        this.tabs.sort((o1, o2) -> o1.getTabName().compareTo(o2.getTabName()));
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

        RenderUtils.drawHollowRect(startX, startY, this.width + 6, y, 1.0f, 0xCC000000);
        int height = startY;
        for (GuiTab tab : this.tabs) {
            tab.drawTab(startX + 2, height + 2, this.width, this.tabs.get(this.selected) == tab && this.opened, this.tabs.get(this.selected) == tab);
            tab.drawButtons(height, this.width, this.tabs.get(this.selected) == tab && this.opened, this.tabs.get(this.selected) == tab);
            height += 12;
        }
    }

    @Override
    public void onEventCalled(KeyPressEvent event) {
        if (event.getKeyCode() == Keyboard.KEY_LEFT) {
            this.opened = false;
        }

        this.tabs.stream().filter(tab -> this.opened && this.tabs.get(this.selected) == tab).forEach(tab -> tab.keyboard(event.getKeyCode()));

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
