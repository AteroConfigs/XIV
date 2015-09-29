package pw.latematt.xiv.ui.tabgui;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.KeyPressEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.tabgui.tab.GuiItem;
import pw.latematt.xiv.ui.tabgui.tab.GuiTab;
import pw.latematt.xiv.utils.RenderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Jack
 */

public class GuiTabHandler implements Listener<KeyPressEvent> {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int colourBody = 0x95000400;
    private int colourBox = 0xFF4DB3FF;
    private String colourHightlight = "\247f";
    private String colourNormal = "\2477";
    private int guiHeight = 0;
    private int guiWidth = 76;
    private boolean mainMenu = true;
    private int selectedItem = 0;
    private int selectedTab = 0;
    private int tabHeight = 12;
    private final ArrayList<GuiTab> tabs = new ArrayList<>();

    public GuiTabHandler() {
        final GuiTab tabCombat = new GuiTab(this, "Combat");
        final GuiTab tabMovement = new GuiTab(this, "Movement");
        final GuiTab tabPlayer = new GuiTab(this, "Player");
        final GuiTab tabRender = new GuiTab(this, "Render");

        for (final Mod mod : XIV.getInstance().getModManager().getContents()) {
            if (Objects.equals(mod.getModType(), ModType.COMBAT)) {
                tabCombat.getMods().add(new GuiItem(mod));
            } else if (Objects.equals(mod.getModType(), ModType.MOVEMENT)) {
                tabMovement.getMods().add(new GuiItem(mod));
            } else if (Objects.equals(mod.getModType(), ModType.PLAYER)) {
                tabPlayer.getMods().add(new GuiItem(mod));
            } else if (Objects.equals(mod.getModType(), ModType.RENDER)) {
                tabRender.getMods().add(new GuiItem(mod));
            }
        }

        Arrays.asList(tabCombat, tabMovement, tabPlayer, tabRender).forEach(this.tabs::add);
        this.guiHeight = tabHeight + this.tabs.size() * tabHeight;
        XIV.getInstance().getListenerManager().add(this);
    }

    public void drawGui() {
        int x = 2;
        int y = 22;

        RenderUtils.drawBorderedRect(2, 22, this.guiWidth - 2, 22 + (10 * this.tabs.size()) + 10, 1, 0xFF000000, this.colourBody);

        int yOff = y + 2;

        for (int i = 0; i < tabs.size(); i++)
        {
            if (Objects.equals(this.selectedTab, i)) {
                RenderUtils.drawBorderedRect(x, i * 12 + 22, this.guiWidth - 2, i + (36 + (i * 11)), 1, 0xFF000000, this.colourBox);
            }

            mc.fontRendererObj.drawStringWithShadow((this.tabs.get(i)).getTabName(), x + 2, yOff + 1, 0xFFFFFFFF);

            if (Objects.equals(this.selectedTab, i) && !mainMenu) {
                ((this.tabs.get(i))).drawTabMenu(this.mc, this.guiWidth, yOff - 2);
            }

            yOff += tabHeight;
        }
    }

    @Override
    public void onEventCalled(KeyPressEvent event) {
        if (Objects.equals(event.getKeyCode(), Keyboard.KEY_UP)) {
            if (this.mainMenu) {
                this.selectedTab--;
                if (this.selectedTab < 0) {
                    this.selectedTab = this.tabs.size()-1;
                }
            } else {
                this.selectedItem--;
                if (this.selectedItem < 0) {
                    this.selectedItem = (this.tabs.get(this.selectedTab)).getMods().size() - 1;
                }
            }
        } else if (Objects.equals(event.getKeyCode(), Keyboard.KEY_DOWN)) {
            if (this.mainMenu) {
                this.selectedTab++;
                if (this.selectedTab > this.tabs.size()-1) {
                    this.selectedTab = 0;
                }
            } else {
                this.selectedItem++;
                if (this.selectedItem>(this.tabs.get(this.selectedTab)).getMods().size() - 1) {
                    this.selectedItem = 0;
                }
            }
        } else if (Objects.equals(event.getKeyCode(), Keyboard.KEY_LEFT)) {
            if (!this.mainMenu) {
                this.mainMenu = true;
            }
        } else if (Objects.equals(event.getKeyCode(), Keyboard.KEY_RIGHT)) {
            if (this.mainMenu) {
                this.mainMenu = false;
                this.selectedItem = 0;
            }
        } else if (Objects.equals(event.getKeyCode(), Keyboard.KEY_RETURN)) {
            if (!this.mainMenu) {
                ((this.tabs.get(this.selectedTab)).getMods().get(this.selectedItem)).getMod().toggle();
            }
        }
    }

    public int getColourBody() {
        return colourBody;
    }

    public int getColourBox() {
        return colourBox;
    }

    public String getColourHightlight() {
        return colourHightlight;
    }

    public String getColourNormal() {
        return colourNormal;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public int getTabHeight() {
        return tabHeight;
    }
}
