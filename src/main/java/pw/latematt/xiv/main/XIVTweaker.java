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
        arguments = new ArrayList<>(args);
        arguments.add("--gameDir");
        arguments.add(gameDir.getAbsolutePath());
        arguments.add("--assetsDir");
        arguments.add(assetsDir.getAbsolutePath());
        arguments.add("--version");
        arguments.add(profile);
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
