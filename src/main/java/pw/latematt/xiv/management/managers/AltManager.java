package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.management.ListManager;
import pw.latematt.xiv.ui.alt.AltAccount;

import java.util.ArrayList;

/**
 * @author Rederpz
 */
public class AltManager extends ListManager<AltAccount> {
    public AltManager() {
        super(new ArrayList<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + ".");

        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public void add(String username, String password) {
        contents.add(new AltAccount(username, password));
    }

    public void remove(String username) {
        contents.remove(find(username));
    }

    public AltAccount find(String username) {
        for (AltAccount account : getContents()) {
            if (account.getUsername().equals(username)) {
                return account;
            }
        }

        return null;
    }
}
