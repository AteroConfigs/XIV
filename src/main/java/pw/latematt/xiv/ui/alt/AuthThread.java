package pw.latematt.xiv.ui.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.UUID;

public class AuthThread extends Thread {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final AltAccount account;

    public AuthThread(AltAccount account) {
        this.account = account;
    }

    @Override
    public void run() {
        YggdrasilUserAuthentication authentication = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString()), Agent.MINECRAFT);
        authentication.setUsername(account.getUsername());
        authentication.setPassword(account.getPassword());

        Field session = null;
        for (Field field : Minecraft.class.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("session") || field.getType() == Session.class) {
                session = field;
            }
        }

        if (session == null) {
            session = Minecraft.class.getDeclaredFields()[28];
        }

        session.setAccessible(true);

        try {
            if(account.getPassword().equals("")) {
                /* TODO: Offline Mode Login */
            }else{
                authentication.logIn();
                session.set(mc, new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), Session.Type.MOJANG.toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
