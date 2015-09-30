package pw.latematt.xiv.ui.tabgui;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.KeyPressEvent;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.tabgui.tab.GuiItem;
import pw.latematt.xiv.ui.tabgui.tab.GuiTab;
import pw.latematt.xiv.utils.RenderUtils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Jack
 */
public class GuiTabHandler implements Listener<KeyPressEvent> {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int colourBody = 0x95000400;
    private int colourBox = 0x804DB3FF;
    private int guiHeight = 0;
    private int guiWidth = 76;
    private boolean mainMenu = true;
    private int selectedItem = 0;
    private int selectedTab = 0;
    private int tabHeight = 12;
    private final ArrayList<GuiTab> tabs = new ArrayList<>();

    public GuiTabHandler() {
        for (ModType type : ModType.values()) {
            if (type == ModType.NONE)
                continue;
            final GuiTab tab = new GuiTab(this, type.getName());
            XIV.getInstance().getModManager().getContents().stream().filter(mod -> mod.getModType() == type).forEach(mod -> {
                tab.getMods().add(new GuiItem(mod));
            });

            tabs.add(tab);
        }

        guiHeight = this.tabs.size() * tabHeight;
        XIV.getInstance().getListenerManager().add(this);
    }

    public void drawGui(int x, int y) {
        RenderUtils.drawBorderedRect(x, y, x + this.guiWidth - 2, y + guiHeight, 1, 0x80000000, this.colourBody);

        int yOff = y + 2;
        for (int i = 0; i < tabs.size(); i++) {
            GuiTab tab = this.tabs.get(i);
            if (Objects.equals(this.selectedTab, i)) {
                RenderUtils.drawBorderedRect(x, i * 12 + y, x + this.guiWidth - 2, i + (y + 12 + (i * 11)), 1, 0x80000000, this.colourBox);
            }

            mc.fontRendererObj.drawStringWithShadow(tab.getTabName(), x + 2, yOff, 0xFFFFFFFF);

            if (Objects.equals(this.selectedTab, i) && !mainMenu) {
                tab.drawTabMenu(this.mc, x + this.guiWidth, yOff - 2);
            }

            yOff += tabHeight;
        }
    }

    @Override
    public void onEventCalled(KeyPressEvent event) {
        switch (event.getKeyCode()) {
            case Keyboard.KEY_UP:
                if (this.mainMenu) {
                    this.selectedTab--;
                    if (this.selectedTab < 0) {
                        this.selectedTab = this.tabs.size() - 1;
                    }
                } else {
                    this.selectedItem--;
                    if (this.selectedItem < 0) {
                        this.selectedItem = (this.tabs.get(this.selectedTab)).getMods().size() - 1;
                    }
                }
                break;
            case Keyboard.KEY_DOWN:
                if (this.mainMenu) {
                    this.selectedTab++;
                    if (this.selectedTab > this.tabs.size() - 1) {
                        this.selectedTab = 0;
                    }
                } else {
                    this.selectedItem++;
                    if (this.selectedItem > (this.tabs.get(this.selectedTab)).getMods().size() - 1) {
                        this.selectedItem = 0;
                    }
                }
                break;
            case Keyboard.KEY_LEFT:
                if (!this.mainMenu) {
                    this.mainMenu = true;
                }
                break;
            case Keyboard.KEY_RIGHT:
                if (this.mainMenu) {
                    this.mainMenu = false;
                    this.selectedItem = 0;
                } else {
                    ((this.tabs.get(this.selectedTab)).getMods().get(this.selectedItem)).getMod().toggle();
                }
                break;
            case Keyboard.KEY_RETURN:
                if (!this.mainMenu) {
                    ((this.tabs.get(this.selectedTab)).getMods().get(this.selectedItem)).getMod().toggle();
                }
                break;
        }
    }

    public int getColourBody() {
        return colourBody;
    }

    public int getColourBox() {
        return colourBox;
    }

    public String getColourHightlight() {
        return "\247f";
    }

    public String getColourNormal() {
        return "\2477";
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public int getTabHeight() {
        return tabHeight;
    }
}
