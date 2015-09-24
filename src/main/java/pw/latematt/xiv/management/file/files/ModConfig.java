package pw.latematt.xiv.management.file.files;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.file.XIVFile;
import pw.latematt.xiv.mod.Mod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * KILL YOURSELF RUDY
 *
 * @author Matthew
 */
public class ModConfig extends XIVFile {
    public ModConfig() {
        super("modconfig", "json");
    }

    @Override
    public void load() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        HashMap<String, ModOptions> modOptions = gson.fromJson(reader, new TypeToken<HashMap<String, ModOptions>>() {}.getType());
        for (Mod mod : XIV.getInstance().getModManager().getContents()) {
            for (String modName : modOptions.keySet()) {
                if (mod.getName().equals(modName)) {
                    ModOptions options = modOptions.get(modName);
                    mod.setKeybind(Keyboard.getKeyIndex(options.getKeybind()));
                    mod.setColor(options.getColor());
                    mod.setVisible(options.isVisible());
                }
            }
        }
    }

    @Override
    public void save() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<String, ModOptions> modOptions = new HashMap<String, ModOptions>();
        for (Mod mod : XIV.getInstance().getModManager().getContents()) {
            modOptions.put(mod.getName(), new ModOptions(Keyboard.getKeyName(mod.getKeybind()), mod.getColor(), mod.isVisible()));
        }
        Files.write(gson.toJson(modOptions).getBytes("UTF-8"), file);
    }

    public class ModOptions {
        private final String keybind;
        private final int color;
        private final boolean visible;

        public ModOptions(String keybind, int color, boolean visible) {
            this.keybind = keybind;
            this.color = color;
            this.visible = visible;
        }

        public String getKeybind() {
            return keybind;
        }

        public int getColor() {
            return color;
        }

        public boolean isVisible() {
            return visible;
        }
    }
}
