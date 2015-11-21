package pw.latematt.xiv.testing;

import org.junit.Assert;
import org.junit.Test;

/**
 * This is a template on how to perform tests with Maven, which adds integrity to Gitlab CI (which I added)
 *
 * @author Matthew
 */
public class ExampleTest {
    @Test
    public void performTest() {
        String shouldBeNull = null;
        Assert.assertNull(shouldBeNull);
    }
}
