package pw.latematt.xiv.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import pw.latematt.xiv.XIV;
import pw.latematt.xiv.event.events.RenderStringEvent;
import pw.latematt.xiv.utils.NahrFont;
import pw.latematt.xiv.utils.RenderUtils;

public class GuiXIVChat extends GuiNewChat {
    private final NahrFont font;
    public GuiXIVChat(Minecraft mcIn) {
        super(mcIn);
        font = new NahrFont("Verdana", 18);
    }

    @Override
    public void drawChat(int p_146230_1_) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int var2 = this.getLineCount();
            boolean var3 = false;
            int var4 = 0;
            int var5 = this.field_146253_i.size();
            float var6 = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (var5 > 0) {
                if (this.getChatOpen()) {
                    var3 = true;
                }

                float var7 = this.getChatScale();
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(var7, var7, 1.0F);
                int var9;
                int var11;
                int var14;

                for (var9 = 0; var9 + this.scrollPos < this.field_146253_i.size() && var9 < var2; ++var9) {
                    ChatLine var10 = (ChatLine) this.field_146253_i.get(var9 + this.scrollPos);

                    if (var10 != null) {
                        var11 = p_146230_1_ - var10.getUpdatedCounter();

                        if (var11 < 200 || var3) {
                            ++var4;
                        }
                    }
                }

                GlStateManager.translate(0, -8, 0);
                int height = -1;
                if (!getChatOpen()) {
                    if (var4 > 0) {
                        height = -var4 * 9;
                    }
                } else if (getChatOpen()) {
                    height = -var2 * 9;
                    RenderUtils.drawBorderedRect(0.0F, height - 6, getChatWidth() + 3.0F, height + 5.5, 0x40000000, 0x60000000);
                    font.drawString("Chat", 1, height - 8, NahrFont.FontType.NORMAL, 0xFFFFFFFF);
                }

                if (height != -1) {
                    RenderUtils.drawBorderedRect(0.0F, height + 6, getChatWidth() + 3.0F, 8.0F, 0x40000000, 0x60000000);
                }
                GlStateManager.translate(1, 8, 0);

                for (var9 = 0; var9 + this.scrollPos < this.field_146253_i.size() && var9 < var2; ++var9) {
                    ChatLine var10 = (ChatLine) this.field_146253_i.get(var9 + this.scrollPos);

                    if (var10 != null) {
                        var11 = p_146230_1_ - var10.getUpdatedCounter();

                        if (var11 < 200 || var3) {
                            double var12 = (double) var11 / 200.0D;
                            var12 = 1.0D - var12;
                            var12 *= 10.0D;
                            var12 = MathHelper.clamp_double(var12, 0.0D, 1.0D);
                            var12 *= var12;
                            var14 = (int) (255.0D * var12);

                            if (var3) {
                                var14 = 255;
                            }

                            var14 = (int) ((float) var14 * var6);

                            byte var15 = 0;
                            int var16 = -var9 * 9;
                            String var17 = var10.getChatComponent().getFormattedText();
                            RenderStringEvent event = new RenderStringEvent(var17, RenderStringEvent.State.CHAT);
                            XIV.getInstance().getListenerManager().call(event);
                            var17 = event.getString();

                            GlStateManager.enableBlend();
                            font.drawString(var17, (float) var15, (float) (var16 - 14), NahrFont.FontType.SHADOW_THIN, 16777215 + (var14 << 24), (var14 << 24));
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                    }
                }

                if (var3) {
                    var9 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int var18 = var5 * var9 + var5;
                    var11 = var4 * var9 + var4;
                    int var19 = this.scrollPos * var11 / var5;
                    int var13 = var11 * var11 / var18;

                    if (var18 != var11) {
                        var14 = var19 > 0 ? 170 : 96;
                        int var20 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -var19, 2, -var19 - var13, var20 + (var14 << 24));
                        drawRect(2, -var19, 1, -var19 - var13, 13421772 + (var14 << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }
}
