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

/**
 * Renders the RP/HRP toggle buttons above the chat input field, and routes clicks.
 * Uses render + mouse events directly (rather than ScreenEvent.Init#addListener) so the widget
 * is guaranteed to render — that helper only registers click listeners, not renderables.
 */
@Mod.EventBusSubscriber(modid = DualChatMod.MODID, value = Dist.CLIENT)
public final class ChannelToggleButtons {

    private static final int BTN_W   = 36;
    private static final int BTN_H   = 14;
    private static final int BTN_GAP = 4;
    private static final int LEFT_PADDING = 2;
    /** Distance between the bottom of the chat screen and the top of the toggle row. */
    private static final int BOTTOM_OFFSET = 32;

    private ChannelToggleButtons() {}

    private static int btnX(int index) { return LEFT_PADDING + index * (BTN_W + BTN_GAP); }
    private static int btnY(int screenHeight) { return screenHeight - BOTTOM_OFFSET; }

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
        if (event.getButton() != 0) return; // left click only

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
        g.fill(x,        y,        x1,        y1,        fillColor);
        g.fill(x,        y,        x1,        y + 1,     borderColor);
        g.fill(x,        y1 - 1,   x1,        y1,        borderColor);
        g.fill(x,        y,        x + 1,     y1,        borderColor);
        g.fill(x1 - 1,   y,        x1,        y1,        borderColor);

        int textColor = active ? 0xFFFFFFFF : 0xFFCCCCCC;
        String label = ch.displayName();
        int tx = x + (BTN_W - font.width(label)) / 2;
        int ty = y + (BTN_H - 8) / 2;
        g.drawString(font, label, tx, ty, textColor, false);
    }
}
