package pw.latematt.xiv.ui.clickgui;

import net.minecraft.client.gui.GuiScreen;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.element.elements.ElementButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;

import java.io.IOException;
import java.util.ArrayList;

public class GuiClick extends GuiScreen {

    private final ArrayList<Panel> panels;

    public GuiClick() {
        float x = 4;
        float y = 4;

        this.panels = new ArrayList<Panel>();
        for (ModType type : ModType.values()) {
            y = 18;
            ArrayList<Element> elements = new ArrayList<Element>();

            for (Mod mod : XIV.getInstance().getModManager().getContents()) {
                if (mod.getModType() == type) {
                    elements.add(new ElementButton(mod, x + 2, y + 2, 96, 12));
                    y += 13;
                }
            }

            /*for (Entry<Mod, ModType> mod : this.mods.entrySet()) {
                if (mod.getValue() == type) {
                    elements.add(new ElementButton(mod.getKey(), x + 2, y + 2, 96, 12));
                    y += 13;
                }
            }*/

            this.panels.add(new Panel(type, elements, x, 4, 100, 14));

            x += 102;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (Panel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (Panel panel : panels) {
            panel.drawPanel(mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (Panel panel : panels) {
            panel.keyPressed(keyCode);
        }
    }
}
