package pw.latematt.xiv.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.StringUtils;
import org.apache.commons.codec.binary.Base64;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.command.CommandHandler;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.IngameHUDRenderEvent;
import pw.latematt.xiv.event.events.MotionUpdateEvent;
import pw.latematt.xiv.utils.ChatLogger;
import pw.latematt.xiv.utils.RenderUtils;
import pw.latematt.xiv.utils.Timer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File screenshots = new File("screenshots");

                File[] files = screenshots.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isFile();
                    }
                });

                long timeModified = -9223372036854775808L;
                File lastModified = null;
                for (File file : files) {
                    if (file.lastModified() > timeModified) {
                        lastModified = file;
                        timeModified = file.lastModified();
                    }
                }

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

                    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                    String url = "http://i.imgur.com/" + stringBuilder.toString().substring(15, 22) + ".png";

                    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(new URI(url));
                    }
                    ChatLogger.print("Screenshot uploaded to " + url);
                } catch (IOException e) {
                    ChatLogger.print("Unable to upload screenshot.");
                } catch (URISyntaxException e) {
                    ChatLogger.print("Unable to open screenshot.");
                }
            }
        });
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
            mc.fontRendererObj.drawStringWithShadow(time + "", RenderUtils.newScaledResolution().getScaledWidth() / 2 + 7, RenderUtils.newScaledResolution().getScaledHeight() / 2 - 2, 0xFFFFFFFF);
        }
    }
}
