package pw.latematt.xiv.ui.clickgui.panel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.mod.mods.ClickGUI;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.utils.NahrFont;

import java.util.ArrayList;

public class Panel {

    private static Minecraft mc = Minecraft.getMinecraft();

    private float x, y, width, height, openheight;
    private boolean dragging, open;
    private float dragX, dragY;

    private ModType type;
    private ArrayList<Element> elements;

    private NahrFont font;

    public Panel(ModType type, ArrayList<Element> elements, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.openheight = this.height = height;

        this.type = type;
        this.elements = elements;

        this.font = new NahrFont("Verdana", 18);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getOpenHeight() {
        return openheight;
    }

    public void setOpenHeight(float openheight) {
        this.openheight = openheight;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public ModType getType() {
        return type;
    }

    public void setType(ModType type) {
        this.type = type;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public void drawPanel(int mouseX, int mouseY) {
        Gui.drawRect((int) getX(), (int) getY(), (int) getX() + (int) getWidth(), (int) getY() + (int) getHeight(), 0x55000000);

        String extra = elements.size() > 0 ? (" [\247b" + elements.size() + "\247r]") : "";

        mc.fontRendererObj.drawStringWithShadow(type.getName() + extra, getX() + (getWidth() / 2) - (mc.fontRendererObj.getStringWidth(type.getName() + extra) / 2), getY() + 3, 0xFFFFFFFF);

        if (isOpen()) {
            Gui.drawRect((int) getX() + 2, (int) getY() + (int) getOpenHeight() - 1, (int) getX() + (int) getWidth() - 2, (int) getY() + (int) getOpenHeight(), 0x55000000);
        }

        if (isDragging()) {
            this.x = mouseX + dragX;
            this.y = mouseY + dragY;

            if (!Mouse.isButtonDown(0)) {
                setDragging(false);
            }
        }

        if (isOpen()) {
            float y = getOpenHeight();

            for (Element element : elements) {
                element.drawElement(mouseX, mouseY);
                element.setX(getX() + 2);
                element.setY(getY() + y + 2);

                if (element.getWidth() + 4 > getWidth()) {
                    this.setWidth(element.getWidth() + 4);
                }

                y += 13;
            }

            this.setHeight(y + 3);
        } else {
            this.setHeight(getOpenHeight());
        }
    }

    public void keyPressed(int key) {
        if (isOpen()) {
            for (Element element : elements) {
                element.keyPressed(key);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isOverPanel(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            if (mouseButton == 0) {
                dragX = (getX() - mouseX);
                dragY = (getY() - mouseY);

                dragging = true;
                ClickGUI clickGUI = (ClickGUI) XIV.getInstance().getModManager().find(ClickGUI.class);
                for (Panel pan : clickGUI.screen.panels) {
                    if (pan.equals(this)) continue;
                    pan.dragging = false;
                }
                clickGUI.screen.panels.remove(this);
                clickGUI.screen.panels.add(this);
            } else if (mouseButton == 1) {
                open = !open;
            }
        }

        if (isOpen()) {
            for (Element element : elements) {
                element.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public boolean isOverPanel(int mouseX, int mouseY) {
        return mouseX > getX() && mouseY > getY() && mouseX < getX() + getWidth() && mouseY < getY() + getOpenHeight();
    }

    public void onGuiClosed() {
        this.dragging = false;
    }
}
