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

    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.arguments = new ArrayList<>(args);
        if (!arguments.contains("--gameDir") && gameDir != null) {
            arguments.add("--gameDir");
            arguments.add(gameDir.getAbsolutePath());
        }

        if (!arguments.contains("--assetsDir") && assetsDir != null) {
            arguments.add("--assetsDir");
            arguments.add(assetsDir.getAbsolutePath());
        }

        if (!arguments.contains("--version") && !profile.isEmpty()) {
            arguments.add("--version");
            arguments.add(profile);
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
