package com.dualchat.server;

import com.dualchat.Channel;
import com.dualchat.DualChatMod;
import com.dualchat.network.NetworkHandler;
import com.dualchat.network.S2CChatMessagePacket;
import com.dualchat.network.S2CEmotePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Routes a chat message to recipients.
 * RP: all players within range regardless of their active channel — the client stores it in its RP history.
 * HRP: all players on the server regardless of their active channel — the client stores it in its HRP history.
 * The client only displays incoming messages when the matching channel is active; the rest accumulates as history.
 */
public final class ChatRouter {
    private ChatRouter() {}

    public static void broadcast(ServerPlayer sender, String body) {
        Channel ch = PlayerChannelState.get(sender.getUUID());
        if (!(sender.level() instanceof ServerLevel sl)) return;

        S2CChatMessagePacket packet = new S2CChatMessagePacket(ch, sender.getName().getString(), body);

        if (ch == Channel.RP) {
            int voiceDist = PlasmoVoiceIntegration.getLastDistance(sender.getUUID());
            double range = voiceDist > 0 ? voiceDist : DualChatMod.RP_RANGE;
            double rangeSq = range * range;
            for (ServerPlayer p : sl.players()) {
                if (p.distanceToSqr(sender) > rangeSq) continue;
                NetworkHandler.sendToClient(packet, p);
            }
        } else { // HRP — global, all players on the server.
            for (ServerPlayer p : sl.getServer().getPlayerList().getPlayers()) {
                NetworkHandler.sendToClient(packet, p);
            }
        }
    }

    public static void broadcastEmote(ServerPlayer sender, String text) {
        if (!(sender.level() instanceof ServerLevel sl)) return;
        double rangeSq = DualChatMod.RP_RANGE * DualChatMod.RP_RANGE;
        S2CEmotePacket packet = new S2CEmotePacket(sender.getUUID(), text);
        for (ServerPlayer p : sl.players()) {
            if (p.distanceToSqr(sender) <= rangeSq) {
                NetworkHandler.sendToClient(packet, p);
            }
        }
    }
}
