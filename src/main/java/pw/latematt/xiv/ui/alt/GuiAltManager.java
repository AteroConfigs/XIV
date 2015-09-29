package pw.latematt.xiv.ui.alt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

import pw.latematt.xiv.XIV;

public class GuiAltManager extends GuiScreen {

    private GuiScreen parent;
    private AltSlot slot;

    public GuiTextField username, keyword, search;
    public GuiPasswordField password;
    
    public GuiAltManager(GuiScreen parent) {
        this.parent = parent;
        XIV.getInstance().getFileManager().loadFile("alts");
    }

    public List<AltAccount> getAccounts() {
        if (search != null && search.getText().length() > 0) {
            ArrayList<AltAccount> accounts = new ArrayList<>();
            for (AltAccount account : XIV.getInstance().getAltManager().getContents()) {
                if (account.getUsername().toLowerCase().contains(search.getText().toLowerCase()) || account.getKeyword().toLowerCase().contains(search.getText().toLowerCase())) {
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

        this.slot = new AltSlot(this, mc, width, height, 25, height - 98, 36);
        this.slot.registerScrollButtons(7, 8);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height - 26, 200, 20, "Cancel"));

        this.buttonList.add(new GuiButton(1, width / 2 + 2, height - 70, 98, 20, "Login"));

        this.buttonList.add(new GuiButton(2, width / 2 - 100, height - 92, 98, 20, "Add"));
        this.buttonList.add(new GuiButton(3, width / 2 + 2, height - 92, 98, 20, "Remove"));

        this.buttonList.add(new GuiButton(4, width / 2 - 51, height - 48, 98, 20, "Random"));

        this.buttonList.add(new GuiButton(5, width - 28, height - 86, 20, 20, "+"));

        this.buttonList.add(new GuiButton(6, 7, height - 26, (153 / 2) - 2, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(7, 7 + (153 / 2), height - 26, (153 / 2) - 2, 20, "Set Current"));
        
        this.buttonList.add(new GuiButton(8, width / 2 - 100, height - 70, 98, 20, "Edit"));

        this.username = new GuiTextField(0, mc.fontRendererObj, 8, height - 86, 150, 20);
        this.username.setVisible(true);

        this.password = new GuiPasswordField(1, mc.fontRendererObj, 8, height - 52, 150, 20);
        this.password.setVisible(true);

        this.keyword = new GuiTextField(3, mc.fontRendererObj, width - 182, height - 86, 150, 20);
        this.keyword.setVisible(true);
        if(slot.getAlt() != null)
            this.keyword.setText(slot.getAlt().getKeyword());

        this.search = new GuiTextField(3, mc.fontRendererObj, width - 182, height - 52, 150, 20);
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
                        
                        this.actionPerformed(((GuiButton) buttonList.get(1)));
                    } else {
                        this.actionPerformed(button);
                    }
                }
            } else if (button.id == 5) {
                slot.getAlt().setKeyword(keyword.getText());
            } else if (button.id == 6) {
            	if(password.getText().equals("")) {
            		/* TODO OFFLINE LOGIN */
            	} else {
            		AuthThread thread = new AuthThread(new AltAccount(username.getText(), password.getText()));
            		thread.start();

                    username.setText("");
                    password.setText("");
            	}
            } else if (button.id == 7) {
            	AltAccount current = this.slot.getAlt();
            	
            	current.setUsername(username.getText());
            	current.setPassword(password.getText());

                username.setText("");
                password.setText("");
            } else if (button.id == 8) {
            	AltAccount current = this.slot.getAlt();

            	username.setText(current.getUsername());
            	password.setText(current.getPassword());
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        XIV.getInstance().getFileManager().saveFile("alts");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.slot.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (slot.getAlt() == null) {
            ((GuiButton) buttonList.get(8)).enabled = false;
            ((GuiButton) buttonList.get(4)).enabled = false;
            ((GuiButton) buttonList.get(3)).enabled = false;
            ((GuiButton) buttonList.get(1)).enabled = false;
        } else {
            ((GuiButton) buttonList.get(8)).enabled = true;
            ((GuiButton) buttonList.get(4)).enabled = true;
            ((GuiButton) buttonList.get(3)).enabled = true;
            ((GuiButton) buttonList.get(1)).enabled = true;
        }

        if (slot.getAlt() == null) {
            ((GuiButton) buttonList.get(5)).enabled = false;
        } else {
        	if (keyword.getText().equals("")) {
    			((GuiButton) buttonList.get(5)).displayString = "x";
        		if(slot.getAlt().getKeyword().equals("")) {
                    ((GuiButton) buttonList.get(5)).enabled = false;
        		} else {
                    ((GuiButton) buttonList.get(5)).enabled = true;
        		}
        	} else {
                ((GuiButton) buttonList.get(5)).displayString = "+";
                ((GuiButton) buttonList.get(5)).enabled = true;
        	}
        }
        
        if (!username.getText().equals("")) {
            ((GuiButton) buttonList.get(6)).enabled = true;
        } else {
            ((GuiButton) buttonList.get(6)).enabled = false;
        }
        
        if (!username.getText().equals("") && !password.getText().equals("")) {
            ((GuiButton) buttonList.get(2)).enabled = true;
        } else {
            ((GuiButton) buttonList.get(2)).enabled = false;
        }

        if (!username.getText().equals("") && !password.getText().equals("") && this.slot.getAlt() != null) {
            ((GuiButton) buttonList.get(7)).enabled = true;
        }else{
            ((GuiButton) buttonList.get(7)).enabled = false;
        }

        mc.fontRendererObj.drawStringWithShadow("Username:", 8, height - 96, 0xFFFFFFFF);
        username.drawTextBox();

        mc.fontRendererObj.drawStringWithShadow("Password:", 8, height - 62, 0xFFFFFFFF);
        password.drawTextBox();

        mc.fontRendererObj.drawStringWithShadow("Keyword:", width - 182, height - 96, 0xFFFFFFFF);
        keyword.drawTextBox();

        mc.fontRendererObj.drawStringWithShadow("Search:", width - 182, height - 62, 0xFFFFFFFF);
        search.drawTextBox();

        drawCenteredString(mc.fontRendererObj, String.format("Accounts: %s/%s/%s", getAccounts().size(), XIV.getInstance().getAltManager().getContents().size() - getAccounts().size(), XIV.getInstance().getAltManager().getContents().size()), width / 2, 2, 0xFFFFFFFF);
        drawCenteredString(mc.fontRendererObj, String.format("Logged in as %s", mc.getSession().getUsername()), width / 2, 12, 0xFFFFFFFF);

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_TAB) {
            if (username.isFocused()) {
                username.setFocused(!username.isFocused());
                password.setFocused(true);
            } else if (password.isFocused()) {
                password.setFocused(!password.isFocused());
                keyword.setFocused(true);
            } else if (keyword.isFocused()) {
                keyword.setFocused(!keyword.isFocused());
                search.setFocused(true);
            } else if (search.isFocused()) {
                search.setFocused(!search.isFocused());
                username.setFocused(true);
            }
        }

        username.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
        keyword.textboxKeyTyped(typedChar, keyCode);
        search.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN) {
            if (keyword.isFocused()) {
                this.actionPerformed(((GuiButton) buttonList.get(5)));
            } else if (!username.getText().equals("") && !password.getText().equals("")) {
                this.actionPerformed(((GuiButton) buttonList.get(2)));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        username.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        keyword.mouseClicked(mouseX, mouseY, mouseButton);
        search.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        username.updateCursorCounter();
        password.updateCursorCounter();
        keyword.updateCursorCounter();
        search.updateCursorCounter();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        this.slot.func_178039_p();
    }
}
