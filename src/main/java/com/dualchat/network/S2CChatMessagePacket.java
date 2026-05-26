package com.dualchat.network;

import com.dualchat.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CChatMessagePacket {

    private final Channel channel;
    private final String  senderName;
    private final String  body;

    public S2CChatMessagePacket(Channel channel, String senderName, String body) {
        this.channel    = channel;
        this.senderName = senderName;
        this.body       = body;
    }

    public static void encode(S2CChatMessagePacket msg, FriendlyByteBuf buf) {
        buf.writeByte(msg.channel.ordinal());
        buf.writeUtf(msg.senderName, 64);
        buf.writeUtf(msg.body, 512);
    }

    public static S2CChatMessagePacket decode(FriendlyByteBuf buf) {
        return new S2CChatMessagePacket(
            Channel.byOrdinal(buf.readByte()),
            buf.readUtf(64),
            buf.readUtf(512));
    }

    public static void handle(S2CChatMessagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> com.dualchat.client.ClientChatReceiver.receive(msg.channel, msg.senderName, msg.body));
        ctx.get().setPacketHandled(true);
    }
}
