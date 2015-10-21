package pw.latematt.xiv.value;

import java.text.DecimalFormat;

/**
 * @author Matthew
 */
public class SliderValue<T> extends Value<T> {
    private T start, min, max;
    private DecimalFormat format;
    private float sliderX;

    public SliderValue(String name, T value, T min, T max, DecimalFormat format, boolean autoAdd) {
        super(name, value, autoAdd);

        this.start = value;
        this.min = min;
        this.max = max;
        this.format = format;
    }

    public SliderValue(String name, T value, T min, T max, DecimalFormat format) {
        this(name, value, min, max, format, true);
    }

    public T getDefaultValue() {
        return start;
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
