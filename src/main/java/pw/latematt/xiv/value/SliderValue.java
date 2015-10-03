package pw.latematt.xiv.value;

import pw.latematt.xiv.XIV;

import java.text.DecimalFormat;

/**
 * @author Matthew
 */
public class SliderValue<T> extends Value<T> {
    private T min, max;
    private DecimalFormat format;
    private float sliderX;

    public SliderValue(String name, T value, T min, T max, DecimalFormat format) {
        super(name, value);

        this.min = min;
        this.max = max;
        this.format = format;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public float getSliderX() {
        return sliderX;
    }

    public SliderValue<T> setSliderX(float sliderX) {
        this.sliderX = sliderX;

        return this;
    }
}
