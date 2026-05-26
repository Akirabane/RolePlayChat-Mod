package com.dualchat.client;

import com.dualchat.Channel;
import com.dualchat.DualChatMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DualChatMod.MODID, value = Dist.CLIENT)
public final class ChannelToggleButtons {

    private static final int BTN_W      = 36;
    private static final int BTN_H      = 14;
    private static final int BTN_GAP    = 4;
    private static final int LEFT_PAD   = 2;
    private static final int BADGE_SIZE = 7;

    private ChannelToggleButtons() {}

    /**
     * Y position of the button row — anchored just above the chat messages area.
     * Adapts to the player's chat height and scale settings.
     */
    private static int btnY(int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        double scale = Math.max(0.1, mc.options.chatScale().get());
        int lines = mc.gui.getChat().getLinesPerPage();
        // Chat messages bottom is at screenHeight - 40 (vanilla anchor point).
        // Each line is 9 unscaled px; scale compresses/expands those pixels.
        int chatHeightPx = (int)(lines * 9.0 * scale);
        return Math.max(screenHeight - 40 - chatHeightPx - BTN_H - 3, 4);
    }

    private static int btnX(int index) { return LEFT_PAD + index * (BTN_W + BTN_GAP); }

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof ChatScreen screen)) return;

        GuiGraphics g = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;
        int y = btnY(screen.height);
        Channel[] all = Channel.values();
        for (int i = 0; i < all.length; i++) {
            drawButton(g, font, btnX(i), y, all[i], event.getMouseX(), event.getMouseY());
        }
    }

    @SubscribeEvent
    public static void onClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof ChatScreen screen)) return;
        if (event.getButton() != 0) return;

        int y = btnY(screen.height);
        double mx = event.getMouseX();
        double my = event.getMouseY();
        Channel[] all = Channel.values();
        for (int i = 0; i < all.length; i++) {
            int x = btnX(i);
            if (mx >= x && mx < x + BTN_W && my >= y && my < y + BTN_H) {
                ClientChannelState.set(all[i]);
                event.setCanceled(true);
                return;
            }
        }
    }

    private static void drawButton(GuiGraphics g, Font font, int x, int y, Channel ch, double mouseX, double mouseY) {
        boolean active = ClientChannelState.get() == ch;
        boolean hover  = mouseX >= x && mouseX < x + BTN_W && mouseY >= y && mouseY < y + BTN_H;

        int fillColor, borderColor;
        if (active) {
            fillColor   = (ch.displayColor() & 0x00FFFFFF) | 0xCC000000;
            borderColor = 0xFFFFFFFF;
        } else if (hover) {
            fillColor   = 0xCC202020;
            borderColor = 0xFFAAAAAA;
        } else {
            fillColor   = 0xAA101010;
            borderColor = 0xFF555555;
        }

        RenderSystem.enableBlend();
        int x1 = x + BTN_W, y1 = y + BTN_H;
        g.fill(x,      y,      x1,     y1,     fillColor);
        g.fill(x,      y,      x1,     y + 1,  borderColor);
        g.fill(x,      y1 - 1, x1,     y1,     borderColor);
        g.fill(x,      y,      x + 1,  y1,     borderColor);
        g.fill(x1 - 1, y,      x1,     y1,     borderColor);

        int textColor = active ? 0xFFFFFFFF : 0xFFCCCCCC;
        String label = ch.displayName();
        int tx = x + (BTN_W - font.width(label)) / 2;
        int ty = y + (BTN_H - 8) / 2;
        g.drawString(font, label, tx, ty, textColor, false);

        // Unread badge — small red square with "!" in the top-right corner
        if (!active && ClientChannelHistory.hasUnread(ch)) {
            int bx = x + BTN_W - BADGE_SIZE;
            int by = y - BADGE_SIZE / 2;
            g.fill(bx,     by,                bx + BADGE_SIZE, by + BADGE_SIZE, 0xFFCC0000);
            g.fill(bx,     by,                bx + BADGE_SIZE, by + 1,          0xFF880000);
            g.fill(bx,     by + BADGE_SIZE-1, bx + BADGE_SIZE, by + BADGE_SIZE, 0xFF880000);
            g.drawString(font, "!", bx + (BADGE_SIZE - font.width("!")) / 2, by, 0xFFFFFFFF, false);
        }
    }
}
