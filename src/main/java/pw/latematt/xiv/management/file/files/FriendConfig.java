package pw.latematt.xiv.management.file.files;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.file.XIVFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Matthew
 */
public class FriendConfig extends XIVFile {
    public FriendConfig() {
        super("friends", "json");
    }

    @Override
    public void load() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        HashMap<String, String> friends = gson.fromJson(reader, new TypeToken<HashMap<String, String>>() {}.getType());
        for (String mcname : friends.keySet()) {
            String alias = friends.get(mcname);
            XIV.getInstance().getFriendManager().add(mcname, alias);
        }
    }

    @Override
    public void save() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.write(gson.toJson(XIV.getInstance().getFriendManager().getContents()).getBytes("UTF-8"), file);
    }
}
