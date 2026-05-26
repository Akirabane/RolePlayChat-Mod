package com.dualchat.network;

import com.dualchat.DualChatMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkHandler {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(DualChatMod.MODID, "main"),
        () -> PROTOCOL,
        PROTOCOL::equals,
        PROTOCOL::equals
    );

    private static int id = 0;

    public static void init() {
        // Client → Server
        CHANNEL.registerMessage(id++, C2SSetChannelPacket.class,
            C2SSetChannelPacket::encode, C2SSetChannelPacket::decode, C2SSetChannelPacket::handle,
            Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, C2SChatMessagePacket.class,
            C2SChatMessagePacket::encode, C2SChatMessagePacket::decode, C2SChatMessagePacket::handle,
            Optional.of(NetworkDirection.PLAY_TO_SERVER));

        // Server → Client
        CHANNEL.registerMessage(id++, S2CChatMessagePacket.class,
            S2CChatMessagePacket::encode, S2CChatMessagePacket::decode, S2CChatMessagePacket::handle,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
