package pw.latematt.xiv.value;

import java.text.DecimalFormat;

/**
 * Created by TehNeon on 10/21/2015.
 */
public class ClampedValue<T> extends Value<T> {

    private T min;
    private T max;
    private float sliderX;
    private DecimalFormat format;

    public ClampedValue(String name, T value, T min, T max, DecimalFormat format) {
        super(name, value);

        this.min = min;
        this.max = max;
        this.format = format;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public float getSliderX() {
        return sliderX;
    }

    public ClampedValue<T> setSliderX(float sliderX) {
        this.sliderX = sliderX;
        return this;
    }
}
