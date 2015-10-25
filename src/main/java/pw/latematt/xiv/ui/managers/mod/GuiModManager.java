package pw.latematt.xiv.ui.managers.mod;

import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.mod.Mod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiModManager extends GuiScreen {
    private GuiScreen parent;
    private ModSlot slot;
    public GuiTextField search;

    public GuiModManager(GuiScreen parent) {
        this.parent = parent;

        XIV.getInstance().getFileManager().loadFile("modconfig");
    }

    public List<Mod> getMods() {
        if (search != null && search.getText().length() > 0) {
            if (search.getText().startsWith("!")) {
                String text = search.getText().substring(1);

                // Search for opposite of text given
                return XIV.getInstance().getModManager().getContents().stream().filter(mod -> !mod.getName().toLowerCase().contains(text.toLowerCase())).collect(Collectors.toCollection(ArrayList::new));
            }
            return XIV.getInstance().getModManager().getContents().stream().filter(mod -> mod.getName().toLowerCase().contains(search.getText().toLowerCase())).collect(Collectors.toCollection(ArrayList::new));
        } else {
            return XIV.getInstance().getModManager().getContents();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.slot = new ModSlot(this, mc, width, height, 25, height - 98, 47);
        this.slot.registerScrollButtons(7, 8);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height - 26, 200, 20, "Cancel"));
        this.buttonList.add(new GuiButton(1, width / 2 - 48, height - 48, 98, 20, "Toggle"));

        this.search = new GuiTextField(3, mc.fontRendererObj, width - 182, height - 52, 150, 20);
        this.search.setVisible(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.enabled) {
            if (button.id == 0) {
                mc.displayGuiScreen(parent);
            }else if(button.id == 1) {
                this.slot.getMod().setEnabled(!slot.getMod().isEnabled());
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        XIV.getInstance().getFileManager().saveFile("modconfig");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.slot.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if(this.slot.getMod() != null) {
            Mod mod = this.slot.getMod();
            mc.fontRendererObj.drawStringWithShadow(mod.getName(), 2, 2, mod.getColor());

            ((GuiButton) this.buttonList.get(1)).enabled = true;
        }else{
            ((GuiButton) this.buttonList.get(1)).enabled = false;
        }

        String filters = "Custom Filters: '!'";
        mc.fontRendererObj.drawStringWithShadow(filters, width - 105 - (mc.fontRendererObj.getStringWidth(filters) / 2), height - 28, 0xFFFFFFFF);

        mc.fontRendererObj.drawStringWithShadow("Search:", width - 182, height - 62, 0xFFFFFFFF);
        search.drawTextBox();

        drawCenteredString(mc.fontRendererObj, String.format("Mods: §a%s§f/§c%s§f/§e%s§f", getMods().size(), XIV.getInstance().getModManager().getContents().size() - getMods().size(), XIV.getInstance().getModManager().getContents().size()), width / 2, 2, 0xFFFFFFFF);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (!search.isFocused()) {
            search.setFocused(!search.isFocused());
        }

        search.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        search.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        search.updateCursorCounter();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        this.slot.func_178039_p();
    }
}
