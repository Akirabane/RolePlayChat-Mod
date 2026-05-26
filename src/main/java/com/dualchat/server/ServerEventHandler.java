package com.dualchat.server;

import com.dualchat.Channel;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEventHandler {

    /** Vanilla chat is fully suppressed — all real traffic goes through our packets. */
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        event.setCanceled(true);
    }

    /** Default everyone to RP on join. Cleared on disconnect to avoid leaking memory across sessions. */
    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerChannelState.set(event.getEntity().getUUID(), Channel.RP);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerChannelState.remove(event.getEntity().getUUID());
    }
}
