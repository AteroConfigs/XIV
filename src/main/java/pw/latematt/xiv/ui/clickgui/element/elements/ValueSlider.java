package pw.latematt.xiv.ui.clickgui.element.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.ui.clickgui.element.Element;
import pw.latematt.xiv.value.ClampedValue;

/**
 * @author Rederpz
 */
public class ValueSlider extends Element {

    private final ClampedValue<Float> sliderValue;
    private double amountScrolled = 0.0D;
    private boolean dragging = false;

    private static Minecraft mc = Minecraft.getMinecraft();
    private final String valuePrettyName;

    public ValueSlider(ClampedValue<Float> value, String valuePrettyName, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.sliderValue = value;
        this.valuePrettyName = valuePrettyName;

        amountScrolled = value.getDefault() / value.getMax();
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        XIV.getInstance().getGuiClick().getTheme().renderSlider(getValuePrettyName(), sliderValue.getValue(), getX(), getY(), getWidth(), getHeight(), getValue().getSliderX(), isOverElement(mouseX, mouseY), this);

        if (isOverElement(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            dragging = true;

            if (mouseX > getX()) {
                double diff = mouseX - getX();
                amountScrolled = diff / getWidth();
                amountScrolled = amountScrolled < 0 ? 0 : amountScrolled > 1 ? 1 : amountScrolled;
                setValue();
            }

        } else {
            dragging = false;
        }
    }

    public void setValue() {
        if (!dragging) {
            return;
        }
        /*
        TODO: figure out how I should do this
        final float incrementValue = sliderValue.getIncrementValue();
        final float calculatedValue = ((float) amountScrolled * (sliderValue.getMax() - sliderValue.getMin()));

        sliderValue.setValue(calculatedValue + sliderValue.getMin());*/
    }

    @Override
    public void keyPressed(int key) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isOverElement(mouseX, mouseY) && mouseButton == 0) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
        }
    }

    public ClampedValue<Float> getValue() {
        return sliderValue;
    }

    public String getValuePrettyName() {
        return valuePrettyName;
    }

    @Override
    public void onGuiClosed() {
    }
}