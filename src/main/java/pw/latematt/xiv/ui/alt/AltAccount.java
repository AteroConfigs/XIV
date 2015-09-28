package pw.latematt.xiv.ui.alt;

/**
 * Created by TehNeon on 9/27/2015.
 */
public class AltAccount {

    private String username;
    private String password;
    private String keyword;

    public AltAccount(String username, String password, String keyword) {
        this.username = username;
        this.password = password;
        this.keyword = keyword;
    }

    public AltAccount(String username, String password) {
        this(username, password, "");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
