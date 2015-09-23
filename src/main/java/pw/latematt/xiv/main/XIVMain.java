package pw.latematt.xiv.main;

import net.minecraft.client.main.Main;
import pw.latematt.xiv.XIV;

/**
 * @author Matthew
 */
public class XIVMain {
    public static void main(String[] arguments) {
        XIV.getInstance().setup();
        Main.main(arguments);
    }
}
