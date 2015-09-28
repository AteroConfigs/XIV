package pw.latematt.xiv.management.file.files;

import com.google.common.io.Files;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.file.XIVFile;
import pw.latematt.xiv.ui.alt.AltAccount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Rederpz
 */
public class AltConfig extends XIVFile {
    public AltConfig() {
        super("alts", "txt");
    }

    @Override
    public void load() throws IOException {
        if (!file.exists()) file.createNewFile();

        XIV.getInstance().getAltManager().getContents().clear();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = reader.readLine()) != null) {
            String[] account = line.split(":");
            if(account.length > 2) {
                XIV.getInstance().getAltManager().add(account[0], account[1], account[2]);
            } else {
                XIV.getInstance().getAltManager().add(account[0], account[1]);
            }

        }
    }

    @Override
    public void save() throws IOException {
        StringBuilder jewBuilder = new StringBuilder();

        for (AltAccount account : XIV.getInstance().getAltManager().getContents()) {
            jewBuilder.append(account.getUsername() + ":" + account.getPassword() + ":" + account.getKeyword() + "\n");
        }

        Files.write(jewBuilder.toString().getBytes("UTF-8"), file);
    }
}
