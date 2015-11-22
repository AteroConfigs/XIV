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
public class TitanTheme extends ClickTheme {
    protected NahrFont titleFont, buttonFont;

    protected GuiClick gui;

    public TitanTheme(GuiClick gui) {
        super("Titan", 115, 14, gui, true);
        this.gui = gui;
    }

    @Override
    public void renderPanel(Panel panel) {
        if (Objects.isNull(buttonFont)) {
            this.buttonFont = new NahrFont(new Font("Trebuchet MS", Font.PLAIN, 18), 18);
        }

        if (Objects.isNull(titleFont)) {
            this.titleFont = new NahrFont(new Font("Trebuchet MS", Font.PLAIN, 20), 20);
        }

        panel.setOpenHeight(18);
        panel.setButtonOffset(2.0F);
        panel.setWidth(this.getElementWidth());

        RenderUtils.drawGradientRect(panel.getX(), panel.getY(), panel.getX() + panel.getWidth(), panel.getY() + panel.getHeight(), 0xFF323233, 0xFF3B3B3C);

        RenderUtils.drawGradientRect(panel.getX() + 2, panel.getY() + 2, panel.getX() + panel.getWidth() - 2, panel.getY() + panel.getOpenHeight() - 2, 0xFF525050, 0xFF4A4A4A);
        RenderUtils.drawGradientRect(panel.getX() + 2, panel.getY() + panel.getOpenHeight() - 2, panel.getX() + panel.getWidth() - 2, panel.getY() + panel.getOpenHeight(), 0x44000000, 0x11000000);

        if (panel.isOpen()) {
            RenderUtils.drawGradientRect(panel.getX() + 2, panel.getY() + panel.getOpenHeight() - 1, panel.getX() + panel.getWidth() - 2, panel.getY() + panel.getHeight() - 2, 0xFF3D3C3C, 0xFF414143);
//            RenderUtils.drawGradientRect(panel.getX() + 2, panel.getY() + 2, panel.getX() + panel.getWidth() - 2, panel.getY() + panel.getOpenHeight() - 1, 0xFF525050, 0xFF4A4A4A);
//            RenderUtils.drawGradientRect(panel.getX() + 2, panel.getY() + 2, panel.getX() + panel.getWidth() - 2, panel.getY() + panel.getHeight(), 0xFF525050, 0xFF4A4A4A);
        }

        titleFont.drawString(panel.getName(), panel.getX() + 5, panel.getY(), NahrFont.FontType.NORMAL, 0xFFC4D5D7);
    }

    @Override
    public void renderButton(String name, boolean enabled, float x, float y, float width, float height, boolean overElement, Element element) {
        if (Objects.isNull(buttonFont)) {
            this.buttonFont = new NahrFont(new Font("Trebuchet MS", Font.PLAIN, 18), 18);
        }

        if (Objects.isNull(titleFont)) {
            this.titleFont = new NahrFont(new Font("Trebuchet MS", Font.PLAIN, 20), 20);
        }

        element.setWidth(this.getElementWidth() - 4);
        element.setHeight(this.getElementHeight());

        if (enabled) {
            RenderUtils.drawGradientRect(x + 2, y, x + width - 2, y + height, 0xFF525050, 0xFF4A4A4A);
            RenderUtils.drawGradientRect(x + 3, y + height, x + width - 2, y + height + 1.5F, 0x55000000, 0x11000000);
        }

        buttonFont.drawString(name, x + 6, y - 1, NahrFont.FontType.NORMAL, enabled ? 0xFFDDDDDD : 0xFFA0A0A0);

        if (element instanceof ModButton) {
            ModButton butt = (ModButton) element;

            if (butt.elements.size() > 0) {
                buttonFont.drawString(butt.open ? "-" : "+", x + element.getWidth() - 12, y, NahrFont.FontType.NORMAL, 0xFFFFFFFF);

                if (butt.open) {
                    float elementHeight = element.getHeight();

                    for (Element elem : butt.elements) {
                        elementHeight += elem.getHeight() + 2;
                    }

                    float elemY = y + 1;
                    for (Element elem : butt.elements) {
                        elem.setX(x);
                        if (elem instanceof ValueSlider) {
                            elem.setY(elemY += elem.getHeight());
                            elemY += 3;
                        } else {
                            elem.setY(elemY += elem.getHeight());
                            elemY += 2;
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
        if (Objects.isNull(buttonFont)) {
            this.buttonFont = new NahrFont(new Font("Trebuchet MS", Font.PLAIN, 18), 18);
        }

        if (Objects.isNull(titleFont)) {
            this.titleFont = new NahrFont(new Font("Trebuchet MS", Font.PLAIN, 20), 20);
        }

        element.setWidth(this.getElementWidth() - 4);
        element.setHeight(this.getElementHeight());

        RenderUtils.drawBorderedRect(x, y + 1, x + element.getWidth(), y + height, 0x801E1E1E, 0xFF212121);
        RenderUtils.drawBorderedRect(x, y + 1, x + sliderX, y + height, 0xFF525050, 0xFF4A4A4A);
        RenderUtils.drawBorderedRect(x + 1, y + height, x + sliderX - 1, y + height + 1, 0x44000000, 0x11000000);

        buttonFont.drawString(name, x + 6, y - 1, NahrFont.FontType.NORMAL, 0xFFFFF0F0);
        buttonFont.drawString(value + "", x + element.getWidth() - buttonFont.getStringWidth(value + "") - 6, y - 1, NahrFont.FontType.NORMAL, 0xFFFFF0F0);
    }
}
