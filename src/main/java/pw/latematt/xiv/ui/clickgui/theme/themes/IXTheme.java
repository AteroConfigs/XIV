package pw.latematt.xiv.ui.clickgui.theme.themes;

import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.ui.clickgui.element.elements.ModButton;
import pw.latematt.xiv.ui.clickgui.element.elements.ValueSlider;
import pw.latematt.xiv.ui.clickgui.panel.Panel;
import pw.latematt.xiv.ui.clickgui.theme.ClickTheme;
import pw.latematt.xiv.utils.MathUtils;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

import java.awt.*;
import java.util.Objects;

/**
 * @author Rederpz
 */
public class IXTheme extends ClickTheme {
    protected NahrFont titleFont;
    protected NahrFont modFont;

    protected GuiClick gui;

    public IXTheme(GuiClick gui) {
        super("IX", 96, 12, gui, true);
        this.gui = gui;
    }

    @Override
    public void renderPanel(Panel panel) {
        if (Objects.isNull(titleFont) || Objects.isNull(modFont)) {
            this.titleFont = new NahrFont(new Font("Verdana", Font.BOLD, 19), 19);
            this.modFont = new NahrFont("Tahoma", 17);
        }

        panel.setOpenHeight(13);
        panel.setButtonOffset(1.0F);
        panel.setWidth(100);

        RenderUtils.drawBorderedRect(panel.getX(), panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + (panel.isOpen() ? panel.getHeight() : panel.getOpenHeight()), 0.5F, 0xBB656565, 0x88121212);
        titleFont.drawString(panel.getName(), panel.getX() + 2, panel.getY() - 2, NahrFont.FontType.NORMAL, 0xFFFFFFFF);

        if (panel.isOpen()) {
            RenderUtils.drawRect(panel.getX() + 1, panel.getY() + panel.getOpenHeight(), panel.getX() + panel.getWidth() - 1, panel.getY() + panel.getOpenHeight() + 1, 0xBB545454);
        }

        RenderUtils.drawRect(panel.getX() + panel.getWidth() - panel.getOpenHeight() + 0.5F, panel.getY() + 1, panel.getX() + panel.getWidth(), panel.getY() + panel.getOpenHeight() - 0.5F, 0xBB5A5A5A);
    }

    @Override
    public void renderButton(String name, boolean enabled, float x, float y, float width, float height, boolean overElement, Element element) {
        if (Objects.isNull(titleFont) || Objects.isNull(modFont)) {
            this.titleFont = new NahrFont(new Font("Verdana", Font.BOLD, 20), 20);
            this.modFont = new NahrFont("Tahoma", 17);
        }

        element.setWidth(this.getElementWidth() - 5);
        element.setHeight(this.getElementHeight());

        RenderUtils.drawBorderedRect(x, y, x + 12, y + getElementHeight(), 0.75F, enabled ? 0xFFFF9900 : 0xBB5A5A5A, enabled ? 0xFFB18716 : 0xBB5A5A5A);
        RenderUtils.drawBorderedRect(x, y, x + 96, y + getElementHeight(), 0.75F, enabled ? 0xFFDD9900 : 0xBB5A5A5A, 0x00000000);

        modFont.drawString(name, x + 14.5F, y - 2, NahrFont.FontType.EMBOSS_BOTTOM, enabled ? 0xFFFFAA00 : 0xFFFFFFFF);

        if (element instanceof ModButton) {
            ModButton butt = (ModButton) element;

            if (butt.elements.size() > 0) {
                modFont.drawString(butt.open ? "-" : "+", x + element.getWidth() - 4, y - 2, NahrFont.FontType.NORMAL, 0xFFFFFFFF);

                if (butt.open) {
                    float elementHeight = element.getHeight();

                    for (Element elem : butt.elements) {
                        elementHeight += elem.getHeight();
                    }

                    float elemY = y + 1;
                    for (Element elem : butt.elements) {
                        elem.setX(x);
                        if (elem instanceof ValueSlider) {
                            elem.setY(elemY += elem.getHeight());
                            elemY += 1;
                        } else {
                            elem.setY(elemY += elem.getHeight());
                        }

                        elem.drawElement(MathUtils.getMouseX(), MathUtils.getMouseY());
                    }

                    element.setHeight(elementHeight + 1);
                }
            }
        }
    }

    @Override
    public void renderSlider(String name, float value, float x, float y, float width, float height, float sliderX, boolean overElement, Element element) {
        if (Objects.isNull(titleFont) || Objects.isNull(modFont)) {
            this.titleFont = new NahrFont(new Font("Verdana", Font.BOLD, 20), 20);
            this.modFont = new NahrFont("Tahoma", 17);
        }

        element.setWidth(96);
        element.setHeight(this.getElementHeight());

        RenderUtils.drawBorderedRect(x, y + 1, x + element.getWidth(), y + height, 0x801E1E1E, 0xBB212121);
        RenderUtils.drawBorderedRect(x, y + 1, x + sliderX, y + height, 0xFFFF9900, 0xFFB18716);

        modFont.drawString(name, x + 2, y - 1, NahrFont.FontType.EMBOSS_BOTTOM, 0xFFFFFFFF);
        modFont.drawString(value + "", x + element.getWidth() - modFont.getStringWidth(value + "") - 2, y - 1, NahrFont.FontType.EMBOSS_BOTTOM, 0xFFFFFFFF);
    }
}
