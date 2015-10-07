package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import pw.latematt.xiv.ui.clickgui.GuiClick;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.value.SliderValue;

/**
 * @author Rederpz
 */
public class ValueSlider extends Element {
    private static Minecraft mc = Minecraft.getMinecraft();
    private final SliderValue value;
    private final String valuePrettyName;

    public ValueSlider(SliderValue value, String valuePrettyName, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.value = value;
        this.valuePrettyName = valuePrettyName;

        this.value.setSliderX(96 / 2);
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        GuiClick.getTheme().renderSlider(getValuePrettyName(), toFloat(getValue()).getValue(), getX(), getY(), getWidth(), getHeight(), getValue().getSliderX(), isOverElement(mouseX, mouseY), this);

        SliderValue<Float> val = toFloat(value);

        if (value.getSliderX() > getWidth() - 1) {
            value.setSliderX(getWidth() - 1);
        } else if (value.getSliderX() < 2) {
            value.setSliderX(2);
        }

        if (isOverElement(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            value.setSliderX((mouseX) - getX());

            if (value.getSliderX() > getWidth() - 1) {
                value.setSliderX(getWidth() - 1);
            } else if (value.getSliderX() < 2) {
                value.setSliderX(2);
            }

            float frac = (getWidth() - 3) / (val.getMax() - val.getMin());

            toFloat(Float.parseFloat(val.getFormat().format(((value.getSliderX() - 2) / frac) + val.getMin())), value);
        }
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isOverElement(mouseX, mouseY)) {
            if (mouseButton == 0)
                mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            if (mouseButton == 1) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 0.7F));
                value.setValue(value.getDefaultValue());
            }
        }
    }

    public SliderValue<Boolean> getValue() {
        return value;
    }

    public String getValuePrettyName() {
        return valuePrettyName;
    }

    @Override
    public void onGuiClosed() {
    }

    public SliderValue<Float> toFloat(SliderValue value) {
        if (value.getValue() instanceof Integer) {
            SliderValue<Integer> val = (SliderValue<Integer>) value;
            SliderValue<Float> newValue = new SliderValue<>(val.getName(), val.getValue().floatValue(), val.getMin().floatValue(), val.getMax().floatValue(), val.getFormat());
            return newValue.setSliderX(value.getSliderX());
        } else if (value.getValue() instanceof Double) {
            SliderValue<Double> val = (SliderValue<Double>) value;
            SliderValue<Float> newValue = new SliderValue<>(val.getName(), val.getValue().floatValue(), val.getMin().floatValue(), val.getMax().floatValue(), val.getFormat());
            return newValue.setSliderX(value.getSliderX());
        } else if (value.getValue() instanceof Long) {
            SliderValue<Long> val = (SliderValue<Long>) value;
            SliderValue<Float> newValue = new SliderValue<>(val.getName(), val.getValue().floatValue(), val.getMin().floatValue(), val.getMax().floatValue(), val.getFormat());
            return newValue.setSliderX(value.getSliderX());
        } else if (value.getValue() instanceof Short) {
            SliderValue<Short> val = (SliderValue<Short>) value;
            SliderValue<Float> newValue = new SliderValue<>(val.getName(), val.getValue().floatValue(), val.getMin().floatValue(), val.getMax().floatValue(), val.getFormat());
            return newValue.setSliderX(value.getSliderX());
        } else if (value.getValue() instanceof Float) {
            return (SliderValue<Float>) value;
        }
        return null;
    }

    public SliderValue<Float> toFloat(Float newValue, SliderValue value) {
        if (value.getValue() instanceof Integer) {
            SliderValue<Integer> val = (SliderValue<Integer>) value;
            val.setValue(newValue.intValue());
        } else if (value.getValue() instanceof Double) {
            SliderValue<Double> val = (SliderValue<Double>) value;
            val.setValue(newValue.doubleValue());
        } else if (value.getValue() instanceof Long) {
            SliderValue<Long> val = (SliderValue<Long>) value;
            val.setValue(newValue.longValue());
        } else if (value.getValue() instanceof Short) {
            SliderValue<Short> val = (SliderValue<Short>) value;
            val.setValue(newValue.shortValue());
        } else if (value.getValue() instanceof Float) {
            SliderValue<Float> val = (SliderValue<Float>) value;
            val.setValue(newValue);
        }
        return null;
    }
}
