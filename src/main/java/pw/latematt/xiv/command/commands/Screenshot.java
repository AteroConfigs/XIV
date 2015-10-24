package pw.latematt.xiv.command.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;
import org.apache.commons.codec.binary.Base64;
import pw.latematt.timer.Timer;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.IngameHUDRenderEvent;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.RenderUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Rederpz
 */
public class Screenshot implements CommandHandler, Listener<IngameHUDRenderEvent> {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Timer timer = new Timer();

    @Override
    public void onCommandRan(String message) {
        timer.reset();
        XIV.getInstance().getListenerManager().add(this);
    }

    public void takeScreenshot() {
        ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer());

        Thread thread = new Thread(() -> {
            File screenshots = new File("screenshots");

            File[] files = screenshots.listFiles(File::isFile);

            long timeModified = -9223372036854775808L;
            File lastModified = null;
            for (File file : files) {
                if (file.lastModified() > timeModified) {
                    lastModified = file;
                    timeModified = file.lastModified();
                }
            }

            if (lastModified != null) {
                try {
                    URL imgurApi = new URL("https://api.imgur.com/3/image");
                    HttpURLConnection connection = (HttpURLConnection) imgurApi.openConnection();
                    BufferedImage image = null;
                    File file = new File(lastModified.getAbsolutePath());
                    image = ImageIO.read(file);
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", byteArray);
                    byte[] byteImage = byteArray.toByteArray();
                    String dataImage = Base64.encodeBase64String(byteImage);
                    String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    String clientKey = "57e0280fe5e3a5e";
                    connection.setRequestProperty("Authorization", "Client-ID " + clientKey);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.connect();
                    StringBuilder stringBuilder = new StringBuilder();
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        stringBuilder.append(line).append(System.lineSeparator());
                    }
                    wr.close();
                    rd.close();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject json = gson.fromJson(stringBuilder.toString(), JsonObject.class);
                    String url = "http://i.imgur.com/" + json.get("data").getAsJsonObject().get("id").getAsString() + ".png";

                    StringSelection contents = new StringSelection(url);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(contents, null);
                    ChatLogger.print("Screenshot URL copied to clipboard.");
                } catch (IOException e) {
                    ChatLogger.print("Unable to upload screenshot.");
                }
            } else {
                ChatLogger.print("Unable to find screenshot.");
            }
        });
        thread.start();
    }

    @Override
    public void onEventCalled(IngameHUDRenderEvent event) {
        int time = -1;

        if (timer.hasReached(3000L)) {
            takeScreenshot();
            XIV.getInstance().getListenerManager().remove(this);
        } else if (timer.hasReached(2000L)) {
            time = 1;
        } else if (timer.hasReached(1000L)) {
            time = 2;
        } else {
            time = 3;
        }

        if (time > 0) {
            mc.fontRendererObj.drawStringWithShadow("" + time, RenderUtils.newScaledResolution().getScaledWidth() / 2 + 7, RenderUtils.newScaledResolution().getScaledHeight() / 2 - 2, 0xFFFFFFFF);
        }
    }
}
