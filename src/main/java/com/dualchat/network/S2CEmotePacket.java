package com.dualchat.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class S2CEmotePacket {

    private final UUID   senderUUID;
    private final String text;

    public S2CEmotePacket(UUID senderUUID, String text) {
        this.senderUUID = senderUUID;
        this.text       = text;
    }

    public static void encode(S2CEmotePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.senderUUID);
        buf.writeUtf(msg.text, 256);
    }

    public static S2CEmotePacket decode(FriendlyByteBuf buf) {
        return new S2CEmotePacket(buf.readUUID(), buf.readUtf(256));
    }

    public static void handle(S2CEmotePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> com.dualchat.client.EmoteRenderer.receive(msg.senderUUID, msg.text));
        ctx.get().setPacketHandled(true);
    }
}
