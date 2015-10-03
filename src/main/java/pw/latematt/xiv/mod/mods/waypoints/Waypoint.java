package pw.latematt.xiv.mod.mods.waypoints;

/**
 * @author Matthew
 */
public final class Waypoint {
    private final String name, server;
    private final double x, y, z;
    private final boolean temporary;

    public Waypoint(String name, String server, double x, double y, double z, boolean temporary) {
        this.name = name;
        this.server = server;
        this.x = x;
        this.y = y;
        this.z = z;
        this.temporary = temporary;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean isTemporary() {
        return temporary;
    }
}
