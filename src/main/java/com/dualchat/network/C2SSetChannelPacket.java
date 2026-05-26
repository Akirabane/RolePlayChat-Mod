package com.dualchat.network;

import com.dualchat.Channel;
import com.dualchat.server.PlayerChannelState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SSetChannelPacket {

    private final Channel channel;

    public C2SSetChannelPacket(Channel channel) {
        this.channel = channel;
    }

    public static void encode(C2SSetChannelPacket msg, FriendlyByteBuf buf) {
        buf.writeByte(msg.channel.ordinal());
    }

    public static C2SSetChannelPacket decode(FriendlyByteBuf buf) {
        return new C2SSetChannelPacket(Channel.byOrdinal(buf.readByte()));
    }

    public static void handle(C2SSetChannelPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            PlayerChannelState.set(player.getUUID(), msg.channel);
        });
        ctx.get().setPacketHandled(true);
    }
}
