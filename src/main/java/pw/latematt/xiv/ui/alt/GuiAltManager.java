package pw.latematt.xiv.ui.alt;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiAltManager extends GuiScreen {

    private GuiScreen parent;
    private AltSlot slot;

    private GuiTextField username, password, search;

    public GuiAltManager(GuiScreen parent) {
        this.parent = parent;
        XIV.getInstance().getFileManager().loadFile("alts");
    }

    public List<AltAccount> getAccounts() {
        if(search.getText().length() > 0) {
            ArrayList<AltAccount> accounts = new ArrayList<>();
            for(AltAccount account: XIV.getInstance().getAltManager().getContents()) {
                if(account.getUsername().toLowerCase().startsWith(search.getText().toLowerCase())) {
                    accounts.add(account);
                }
            }

            return accounts;
        } else {
            return XIV.getInstance().getAltManager().getContents();
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.slot = new AltSlot(this, mc, width, height, 25, height - 98, 24);
        this.slot.registerScrollButtons(7, 8);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height - 26, 200, 20, "Cancel"));

        this.buttonList.add(new GuiButton(1, width / 2 - 100, height - 70, 200, 20, "Login"));

        this.buttonList.add(new GuiButton(2, width / 2 - 100, height - 92, 98, 20, "Add"));
        this.buttonList.add(new GuiButton(3, width / 2 + 2, height - 92, 98, 20, "Remove"));

        this.buttonList.add(new GuiButton(4, width / 2 - 51, height - 48, 98, 20, "Random"));

        this.username = new GuiTextField(0, mc.fontRendererObj, 8, height - 76, 150, 20);
        this.username.setVisible(true);

        this.password = new GuiTextField(1, mc.fontRendererObj, 8, height - 32, 150, 20);
        this.password.setVisible(true);

        this.search = new GuiTextField(2, mc.fontRendererObj, width - 152, height - 54, 150, 20);
        this.search.setVisible(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.enabled) {
            if (button.id == 0) {
                mc.displayGuiScreen(parent);
            } else if (button.id == 1) {
                AuthThread thread = new AuthThread(this.slot.getAlt());
                thread.start();
            } else if (button.id == 2) {
                XIV.getInstance().getAltManager().add(username.getText(), password.getText());
                username.setText("");
                password.setText("");
            } else if (button.id == 3) {
                XIV.getInstance().getAltManager().remove(slot.getAlt().getUsername());
            } else if (button.id == 4) {
                Random random = new Random();

                if (this.slot.getSize() > 1) {
                    int next = random.nextInt(this.slot.getSize());
                    if (this.slot.getSelected() != next) {
                        this.slot.setSelected(next);
                    } else {
                        this.actionPerformed(button);
                    }
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.slot.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (slot.getAlt() == null) {
            ((GuiButton) buttonList.get(4)).enabled = false;
            ((GuiButton) buttonList.get(3)).enabled = false;
            ((GuiButton) buttonList.get(1)).enabled = false;
        } else {
            ((GuiButton) buttonList.get(4)).enabled = true;
            ((GuiButton) buttonList.get(3)).enabled = true;
            ((GuiButton) buttonList.get(1)).enabled = true;
        }

        if (!username.getText().equals("") && !password.getText().equals("")) {
            ((GuiButton) buttonList.get(2)).enabled = true;
        } else {
            ((GuiButton) buttonList.get(2)).enabled = false;
        }

        mc.fontRendererObj.drawStringWithShadow("Username:", 8, height - 88, 0xFFFFFFFF);
        username.drawTextBox();

        mc.fontRendererObj.drawStringWithShadow("Password:", 8, height - 44, 0xFFFFFFFF);
        password.drawTextBox();

        mc.fontRendererObj.drawStringWithShadow("Search:", width - 152, height - 66, 0xFFFFFFFF);
        search.drawTextBox();

        drawCenteredString(mc.fontRendererObj, String.format("Accounts: %s", XIV.getInstance().getAltManager().getContents().size()), width / 2, 2, 0xFFFFFFFF);
        drawCenteredString(mc.fontRendererObj, String.format("Logged in as %s", mc.getSession().getUsername()), width / 2, 12, 0xFFFFFFFF);

        if (username.isFocused() && password.isFocused()) {
            password.setFocused(false);
            search.setFocused(false);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_TAB) {
            username.setFocused(!username.isFocused());
            password.setFocused(!password.isFocused());
        }

        username.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
        search.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN) {
            if (!username.getText().equals("") && !password.getText().equals("")) {
                this.actionPerformed(((GuiButton) buttonList.get(2)));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        username.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        search.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        username.updateCursorCounter();
        password.updateCursorCounter();
        search.updateCursorCounter();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        this.slot.func_178039_p();
    }
}
