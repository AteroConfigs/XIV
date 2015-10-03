package pw.latematt.xiv.ui.clickgui;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.gui.GuiScreen;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.file.XIVFile;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.panel.panels.*;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.ui.clickgui.theme.themes.DarculaTheme;
import pw.latematt.xiv.ui.clickgui.theme.themes.IXTheme;
import pw.latematt.xiv.ui.clickgui.theme.themes.NorthStarTheme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GuiClick extends GuiScreen {
    public final List<Panel> panels;
    public static List<ClickTheme> themes;
    private static ClickTheme theme;
    public static XIVFile guiConfig;
    public static XIVFile themeConfig;

    public GuiClick() {
        panels = new CopyOnWriteArrayList<>();
        themes = new ArrayList<>();
        themes.add(theme = new DarculaTheme(this));
        themes.add(new IXTheme(this));
        themes.add(new NorthStarTheme(this));

        panels.add(new ThemePanel(4, 4, 100, 14));
        panels.add(new AuraPanel(106, 4, 100, 14));
        panels.add(new ESPPanel(208, 4, 100, 14));
        panels.add(new StorageESPPanel(312, 4, 100, 14));
        panels.add(new WaypointsPanel(4, 19, 100, 14));

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
        try {
            guiConfig.load();
        } catch (IOException e) {
            XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not load, a stack trace has been printed.", guiConfig.getName(), guiConfig.getExtension()));
            e.printStackTrace();
        }

        if (themeConfig == null) {
            themeConfig = new XIVFile("guiTheme", "cfg") {
                @Override
                public void load() throws IOException {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalizedLine = line;
                        GuiClick.themes.stream().filter(theme -> finalizedLine.equals(theme.getName())).forEach(GuiClick::setTheme);
                    }
                }

                @Override
                public void save() throws IOException {
                    Files.write(GuiClick.getTheme().getName().getBytes("UTF-8"), file);
                }
            };
        }
        try {
            themeConfig.load();
        } catch (IOException e) {
            XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not load, a stack trace has been printed.", themeConfig.getName(), themeConfig.getExtension()));
            e.printStackTrace();
        }
    }

    public static ClickTheme getTheme() {
        return theme;
    }

    public static void setTheme(ClickTheme theme) {
        GuiClick.theme = theme;
    }

    public static List<ClickTheme> getThemes() {
        return themes;
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
        drawDefaultBackground();

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
