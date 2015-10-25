package pw.latematt.xiv.ui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import pw.latematt.xiv.ui.alt.GuiAltManager;
import pw.latematt.xiv.ui.mod.GuiModManager;
import pw.latematt.xiv.ui.waypoint.GuiWaypointManager;

/**
 * @author Rederpz
 */
public class GuiManagers extends GuiScreen {

    private final GuiScreen parent;

    public GuiManagers(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + (height / 3), 200, 20, "Cancel"));

        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + (height / 3) - 22, 98, 20, "Alts"));
        this.buttonList.add(new GuiButton(2, width / 2 + 2, height / 4 + (height / 3) - 22, 98, 20, "Mods"));
        this.buttonList.add(new GuiButton(3, width / 2 - 100, height / 4 + (height / 3) - 44, 98, 20, "Waypoints"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if(button.enabled) {
            switch(button.id) {
                case 0:
                    mc.displayGuiScreen(parent);
                    break;
                case 1:
                    mc.displayGuiScreen(new GuiAltManager(parent));
                    break;
                case 2:
                    mc.displayGuiScreen(new GuiModManager(parent));
                    break;
                case 3:
                    mc.displayGuiScreen(new GuiWaypointManager(parent));
                    break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
