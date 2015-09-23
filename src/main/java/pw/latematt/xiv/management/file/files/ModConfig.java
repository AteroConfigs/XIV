package pw.latematt.xiv.management.file.files;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.file.XIVFile;
import pw.latematt.xiv.mod.Mod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * KILL YOURSELF RUDY
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
        HashMap<String, ModOptions> modOptions = gson.fromJson(reader, new TypeToken<HashMap<String, ModOptions>>(){}.getType());
        for (Mod mod : XIV.getInstance().getModManager().getContents()) {
            for (String modName : modOptions.keySet()) {
                if (mod.getName().equals(modName)) {
                    ModOptions options = modOptions.get(modName);
                    mod.setKeybind(Keyboard.getKeyIndex(options.getKeybind()));
                    mod.setColor(options.getColor());
                }
            }
        }
    }

    @Override
    public void save() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<String, ModOptions> modOptions = new HashMap<String, ModOptions>();
        for (Mod mod : XIV.getInstance().getModManager().getContents()) {
            modOptions.put(mod.getName(), new ModOptions(Keyboard.getKeyName(mod.getKeybind()), mod.getColor()));
        }
        Files.write(gson.toJson(modOptions).getBytes("UTF-8"), file);
    }

    public class ModOptions {
        private String keybind;
        private int color;

        public ModOptions(String keybind, int color) {
            this.keybind = keybind;
            this.color = color;
        }

        public String getKeybind() {
            return keybind;
        }

        public void setKeybind(String keybind) {
            this.keybind = keybind;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
