package pw.latematt.xiv.mod.mods.player;

import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.Listener;
import pw.latematt.xiv.event.events.AttackEntityEvent;
import pw.latematt.xiv.event.events.ClickBlockEvent;
import pw.latematt.xiv.event.events.MouseClickEvent;
import pw.latematt.xiv.mod.Mod;
import pw.latematt.xiv.mod.ModType;
import pw.latematt.xiv.utils.BlockUtils;
import pw.latematt.xiv.utils.EntityUtils;

/**
 * @author Matthew
 */
public class AutoTool extends Mod {
    private final Listener attackEntityListener, clickBlockListener, mouseClickListener;

    public AutoTool() {
        super("AutoTool", ModType.PLAYER, Keyboard.KEY_NONE);
        attackEntityListener = new Listener<AttackEntityEvent>() {
            @Override
            public void onEventCalled(AttackEntityEvent event) {
                if (event.getEntity() instanceof EntityLivingBase) {
                    EntityLivingBase living = (EntityLivingBase) event.getEntity();
                    mc.thePlayer.inventory.currentItem = EntityUtils.getBestWeapon(living);
                    mc.playerController.updateController();
                }
            }
        };

        clickBlockListener = new Listener<ClickBlockEvent>() {
            @Override
            public void onEventCalled(ClickBlockEvent event) {
                mc.thePlayer.inventory.currentItem = BlockUtils.getBestTool(event.getPos());
                mc.playerController.updateController();
            }
        };

        mouseClickListener = new Listener<MouseClickEvent>() {
            @Override
            public void onEventCalled(MouseClickEvent event) {
                if (event.getButton() == 0) {
                    mc.thePlayer.inventory.currentItem = EntityUtils.getBestWeapon(mc.thePlayer);
                    mc.playerController.updateController();
                }
            }
        };
    }

    @Override
    public void onEnabled() {
        XIV.getInstance().getListenerManager().add(attackEntityListener, clickBlockListener, mouseClickListener);
    }

    @Override
    public void onDisabled() {
        XIV.getInstance().getListenerManager().remove(attackEntityListener, clickBlockListener);
    }
}
