package com.dualchat.client;

import com.dualchat.Channel;
import com.dualchat.DualChatMod;
import com.dualchat.network.C2SChatMessagePacket;
import com.dualchat.network.NetworkHandler;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DualChatMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    /**
     * Routes incoming server/vanilla/other-mod chat into a channel:
     * - RP-style messages (Crow letters, Hygiene, etc.) are italic (§o) — they go to RP.
     * - Everything else (vanilla, system, other mods) goes to HRP, like vanilla chat.
     * The original Component is always kept, so clickable/hoverable text stays functional.
     * If the message's channel is the active one, it displays through the vanilla pipeline;
     * otherwise it's stored and flagged unread.
     */
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        Component msg = event.getMessage();
        if (msg == null) return;

        Channel target = isRoleplayMessage(msg) ? Channel.RP : Channel.HRP;
        ClientChannelHistory.addVanilla(target, msg);

        if (ClientChannelState.get() == target) {
            return; // display via vanilla pipeline (preserves interactivity)
        }
        ClientChannelHistory.markUnread(target);
        event.setCanceled(true);
    }

    /**
     * RP mods (Crow, Hygiene…) style every line in italic via the legacy §o code,
     * either embedded in the text or applied through the Component's style.
     */
    private static boolean isRoleplayMessage(Component msg) {
        if (msg.getString().contains("§o")) return true; // §o in raw text
        return hasItalicStyle(msg);
    }

    private static boolean hasItalicStyle(Component c) {
        if (c.getStyle() != null && c.getStyle().isItalic()) return true;
        for (Component sibling : c.getSiblings()) {
            if (hasItalicStyle(sibling)) return true;
        }
        return false;
    }

    /** Intercept every chat message typed by the player. Commands ("/...") are NOT routed through this event, so they keep working. */
    @SubscribeEvent
    public static void onClientChat(ClientChatEvent event) {
        String msg = event.getMessage();
        if (msg == null || msg.isBlank()) return;
        NetworkHandler.sendToServer(new C2SChatMessagePacket(msg));
        event.setCanceled(true);
    }

    /** When joining a world, clear stale history, reset to HRP, clear visible chat, and push our channel to the server. */
    @SubscribeEvent
    public static void onLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientChannelHistory.clear();
        ClientChannelState.reset();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            FontConfig.ensureFontRedirect();
            ClientKeyBindings.tick();
        }
    }
}
