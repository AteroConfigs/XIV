package pw.latematt.xiv.ui.clickgui;

import net.minecraft.client.gui.GuiScreen;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.element.elements.ThemeButton;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.ui.clickgui.theme.themes.TestingTheme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiClick extends GuiScreen {
    public final List<Panel> panels;
    public final List<ClickTheme> themes;
    private static ClickTheme theme = new TestingTheme();

    public GuiClick() {
        float x = 4;
        float y = 4;

        this.panels = new CopyOnWriteArrayList<>();
        this.themes = new ArrayList<>();
        themes.add(new TestingTheme());

        ArrayList<Element> themesElements = new ArrayList<>();
        for (ClickTheme theme : themes) {
            themesElements.add(new ThemeButton(theme, x + 2, y + 2, 96, 12));
            y += 13;
        }
        this.panels.add(new Panel("Themes", themesElements, x, 4, 100, 14));
        x += 102;
    }

    public static ClickTheme getTheme() {
        return theme;
    }

    public static void setTheme(ClickTheme theme) {
        GuiClick.theme = theme;
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
