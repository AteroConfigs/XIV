package pw.latematt.xiv.ui.clickgui;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.gui.GuiScreen;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.file.XIVFile;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.panel.panels.*;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.ui.clickgui.theme.themes.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GuiClick extends GuiScreen {
    private List<Panel> panels;
    private List<ClickTheme> themes;
    private ClickTheme theme;
    private XIVFile guiConfig;
    private XIVFile themeConfig;

    public ClickTheme getTheme() {
        return theme;
    }

    public void setTheme(ClickTheme theme) {
        this.theme = theme;
    }

    public List<ClickTheme> getThemes() {
        return themes;
    }

    public List<Panel> getPanels() {
        return panels;
    }

    @Override
    public void initGui() {
        panels = new CopyOnWriteArrayList<>();
        themes = new ArrayList<>();
        themes.add(new AvidTheme(this));
        themes.add(theme = new DarculaTheme(this));
        themes.add(new IridiumTheme(this));
        themes.add(new IXTheme(this));
        themes.add(new NorthStarTheme(this));
        themes.add(new PringlesTheme(this));
        themes.add(new XenonTheme(this));

        panels.add(new ThemePanel(4, 4, 100, 14));
        panels.add(new AuraPanel(106, 4, 100, 14));
        panels.add(new ESPPanel(208, 4, 100, 14));
        panels.add(new StorageESPPanel(310, 4, 100, 14));

        panels.add(new WaypointsPanel(4, 19, 100, 14));
        panels.add(new FastUsePanel(106, 19, 100, 14));
        panels.add(new NameProtectPanel(208, 19, 100, 14));
        panels.add(new TriggerbotPanel(310, 19, 100, 14));

        if (guiConfig == null) {
            guiConfig = new XIVFile("gui", "json") {
                @Override
                public void load() throws IOException {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    List<PanelConfig> panelConfigs = gson.fromJson(reader, new TypeToken<List<PanelConfig>>() {
                    }.getType());
                    for (PanelConfig panelConfig : panelConfigs) {
                        panels.stream().filter(panel -> panelConfig.getName().equals(panel.getName())).forEach(panel -> {
                            panel.setX(panelConfig.getX());
                            panel.setY(panelConfig.getY());
                            panel.setOpen(panelConfig.isOpen());
                        });
                    }
                }

                @Override
                public void save() throws IOException {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    List<PanelConfig> panelConfigs = panels.stream().map(panel -> new PanelConfig(panel.getName(), panel.getX(), panel.getY(), panel.isOpen())).collect(Collectors.toList());
                    Files.write(gson.toJson(panelConfigs).getBytes("UTF-8"), file);
                }
            };
        }

        if (themeConfig == null) {
            themeConfig = new XIVFile("guiTheme", "cfg") {
                @Override
                public void load() throws IOException {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalizedLine = line;
                        Optional<ClickTheme> newTheme = themes.stream().filter(theme -> finalizedLine.equals(theme.getName())).findFirst();
                        if (newTheme.isPresent()) {
                            theme = newTheme.get();
                        }
                    }
                }

                @Override
                public void save() throws IOException {
                    Files.write(theme.getName().getBytes("UTF-8"), file);
                }
            };
        }

        XIV.getInstance().getFileManager().loadFile("gui");
        XIV.getInstance().getFileManager().loadFile("guiTheme");
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
        XIV.getInstance().getFileManager().saveFile("gui");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawRect(0, 0, width, height, 0x44000000);

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

    @Override
    public void onGuiClosed() {
        for (Panel panel : panels) {
            panel.onGuiClosed();
        }

        XIV.getInstance().getFileManager().saveFile("gui");
    }

    public class PanelConfig {
        public final String name;
        public final float x, y;
        public final boolean open;

        public PanelConfig(String name, float x, float y, boolean open) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.open = open;
        }

        public String getName() {
            return name;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public boolean isOpen() {
            return open;
        }
    }
}
