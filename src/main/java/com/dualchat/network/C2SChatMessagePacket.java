package com.dualchat.network;

import com.dualchat.server.ChatRouter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client sends a chat message; the channel used is whatever the player currently has on the server. */
public class C2SChatMessagePacket {

    private static final int MAX_LEN = 512;

    private final String message;

    public C2SChatMessagePacket(String message) {
        this.message = message;
    }

    public static void encode(C2SChatMessagePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.message, MAX_LEN);
    }

    public static C2SChatMessagePacket decode(FriendlyByteBuf buf) {
        return new C2SChatMessagePacket(buf.readUtf(MAX_LEN));
    }

    public static void handle(C2SChatMessagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            String trimmed = msg.message == null ? "" : msg.message.trim();
            if (trimmed.isEmpty()) return;
            ChatRouter.broadcast(player, trimmed);
        });
        ctx.get().setPacketHandled(true);
    }
}
