package pw.latematt.xiv.management.managers;

import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.Command;
import pw.latematt.xiv.file.XIVFile;
import pw.latematt.xiv.management.ListManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Matthew
 */
public class FileManager extends ListManager<XIVFile> {
    public FileManager() {
        super(new ArrayList<>());
    }

    @Override
    public void setup() {
        XIV.getInstance().getLogger().info("Starting to setup " + getClass().getSimpleName() + ".");
        if ((!XIVFile.XIV_DIRECTORY.exists()) && XIVFile.XIV_DIRECTORY.mkdirs()) {
            XIV.getInstance().getLogger().info("Successfully created XIV directory at \"" + XIVFile.XIV_DIRECTORY.getAbsolutePath() + "\".");
        } else if (!XIVFile.XIV_DIRECTORY.exists()) {
            XIV.getInstance().getLogger().info("Failed to create XIV directory.");
        }

        XIV.getInstance().getLogger().info("Successfully setup " + getClass().getSimpleName() + ".");
    }

    public void loadAllFiles() {
        for (XIVFile file : contents) {
            try {
                file.load();
            } catch (IOException e) {
                XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not load, a stack trace has been printed.", file.getName(), file.getExtension()));
                e.printStackTrace();
            }
        }
    }

    public void saveAllFiles() {
        for (XIVFile file : contents) {
            try {
                file.save();
            } catch (IOException e) {
                XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not save, a stack trace has been printed.", file.getName(), file.getExtension()));
                e.printStackTrace();
            }
        }
    }

    public void saveFile(String fileName) {
        for (XIVFile file : contents) {
            try {
                if (file.getName().equalsIgnoreCase(fileName)) {
                    file.save();
                }
            } catch (IOException e) {
                XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not save, a stack trace has been printed.", file.getName(), file.getExtension()));
                e.printStackTrace();
            }
        }
    }

    public void loadFile(String fileName) {
        for (XIVFile file : contents) {
            try {
                if (file.getName().equalsIgnoreCase(fileName)) {
                    file.load();
                }
            } catch (IOException e) {
                XIV.getInstance().getLogger().warn(String.format("File \"%s.%s\" could not load, a stack trace has been printed.", file.getName(), file.getExtension()));
                e.printStackTrace();
            }
        }
    }

    public void setVisible(File file, boolean visible) {
        Thread thread = new Thread(() -> {
            try {
                String string = String.format("attrib %s %s", (visible ? "-s -h" : "+s +h"), file.getPath());

                Process process = Runtime.getRuntime().exec(string);
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.run();
    }
}
