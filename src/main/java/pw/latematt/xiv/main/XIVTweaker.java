package pw.latematt.xiv.main;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthew
 */
public class XIVTweaker implements ITweaker {
    private List<String> arguments;

    public void acceptOptions(List<String> arguments, File gameDir, File assetsDir, String profile) {
        this.arguments = new ArrayList<>(arguments);
        if (!this.arguments.contains("--gameDir") && gameDir != null) {
            this.arguments.add("--gameDir");
            this.arguments.add(gameDir.getAbsolutePath());
        }

        if (!this.arguments.contains("--assetsDir") && assetsDir != null) {
            this.arguments.add("--assetsDir");
            this.arguments.add(assetsDir.getAbsolutePath());
        }

        if (!this.arguments.contains("--version") && !profile.isEmpty()) {
            this.arguments.add("--version");
            this.arguments.add(profile);
        }
    }

    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {

    }

    public String getLaunchTarget() {
        return "pw.latematt.xiv.main.XIVMain";
    }

    public String[] getLaunchArguments() {
        return arguments.toArray(new String[arguments.size()]);
    }
}
