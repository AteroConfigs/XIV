package pw.latematt.xiv.management.file.files;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.file.XIVFile;
import pw.latematt.xiv.ui.alt.AltAccount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author Rederpz
 */
public class AltConfig extends XIVFile {
    public AltConfig() {
        super("alts", "txt");
    }

    @Override
    public void load() throws IOException {
        if(!file.exists()) file.createNewFile();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = reader.readLine()) != null) {
            String[] account = line.split(":");
            XIV.getInstance().getAltManager().add(account[0], account[1]);
        }
    }

    @Override
    public void save() throws IOException {
        StringBuilder jewBuilder = new StringBuilder();

        for (AltAccount account : XIV.getInstance().getAltManager().getContents()) {
            jewBuilder.append(account.getUsername() + ":" + account.getPassword() + "\n");
        }

        Files.write(jewBuilder.toString().getBytes("UTF-8"), file);
    }
}
