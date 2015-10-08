package pw.latematt.xiv.ui.clickgui.panel;

import org.lwjgl.input.Mouse;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.mod.mods.ClickGUI;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueButton;
import pw.latematt.xiv.value.Value;

import java.util.ArrayList;
import java.util.Objects;

public class Panel {
    private final String name;
    private float x, y, width, height, openheight;
    private boolean dragging, open;
    private float dragX, dragY;
    private ArrayList<Element> elements;

    public Panel(String name, ArrayList<Element> elements, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.openheight = this.height = height;
        this.name = name;
        this.elements = elements;
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

    public String getName() {
        return name;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public void drawPanel(int mouseX, int mouseY) {
        if (isDragging()) {
            this.x = mouseX + dragX;
            this.y = mouseY + dragY;

            if (!Mouse.isButtonDown(0)) {
                setDragging(false);
            }
        }

        XIV.getInstance().getGuiClick().getTheme().renderPanel(this);

        if (isOpen()) {
            float y = getOpenHeight();

            for (Element element : elements) {
                element.setX(getX() + 2);
                element.setY(getY() + y + 2);
                element.drawElement(mouseX, mouseY);

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
            XIV.getInstance().getFileManager().saveFile("gui");
            if (mouseButton == 0) {
                dragX = (getX() - mouseX);
                dragY = (getY() - mouseY);

                dragging = true;
                ClickGUI clickGUI = (ClickGUI) XIV.getInstance().getModManager().find(ClickGUI.class);
                if (Objects.nonNull(clickGUI)) {
                    for (Panel panel : XIV.getInstance().getGuiClick().getPanels()) {
                        if (panel.equals(this)) continue;
                        panel.dragging = false;
                    }
                    XIV.getInstance().getGuiClick().getPanels().remove(this);
                    XIV.getInstance().getGuiClick().getPanels().add(this);
                }
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

    public void addValueElements(String prefix) {
        float elementY = 4;
        for (Value value : XIV.getInstance().getValueManager().getContents()) {
            if (!value.getName().startsWith(prefix))
                continue;
            String actualName = value.getName().replaceAll(prefix, "");
            String prettyName = "";
            String[] actualNameSplit = actualName.split("_");
            if (actualNameSplit.length > 0) {
                for (String arg : actualNameSplit) {
                    arg = arg.substring(0, 1).toUpperCase() + arg.substring(1, arg.length());
                    prettyName += arg + " ";
                }
            } else {
                prettyName = actualNameSplit[0].substring(0, 1).toUpperCase() + actualNameSplit[0].substring(1, actualNameSplit[0].length());
            }

            if (value.getValue() instanceof Boolean) {
                getElements().add(new ValueButton(value, prettyName, x + 2, elementY + 2, XIV.getInstance().getGuiClick().getTheme().getElementWidth(), XIV.getInstance().getGuiClick().getTheme().getElementHeight()));
            }
            elementY += XIV.getInstance().getGuiClick().getTheme().getElementHeight() + 1;
        }
    }
}
