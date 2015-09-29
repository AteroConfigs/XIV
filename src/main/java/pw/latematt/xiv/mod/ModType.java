package pw.latematt.xiv.mod;

public enum ModType {

    COMBAT("Combat"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    NONE("None");

    private String name;

    ModType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
