package com.dualchat.server;

import com.dualchat.Channel;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEventHandler {

    /** Vanilla chat is fully suppressed — all real traffic goes through our packets. */
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        event.setCanceled(true);
    }

    /** Override /me: cancel vanilla broadcast and route text as an above-head emote. */
    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        String input = event.getParseResults().getReader().getString();
        if (!input.equals("me") && !input.startsWith("me ")) return;

        String text = input.length() > 3 ? input.substring(3).strip() : "";
        if (text.isBlank()) return;

        CommandSourceStack source = event.getParseResults().getContext().getSource();
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (CommandSyntaxException e) {
            return;
        }

        event.setCanceled(true);
        ChatRouter.broadcastEmote(player, text);
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
