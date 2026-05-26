package com.dualchat.client;

import com.dualchat.DualChatMod;
import com.dualchat.client.FontConfig;
import com.dualchat.network.C2SChatMessagePacket;
import com.dualchat.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DualChatMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    /** Suppress all vanilla/server chat — our mod adds messages directly via getChat().addMessage(), bypassing this event. */
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        event.setCanceled(true);
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
