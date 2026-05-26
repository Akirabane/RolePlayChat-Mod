package com.dualchat.server;

import com.dualchat.Channel;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Server-side in-memory store of each player's active channel. Defaults to RP. */
public final class PlayerChannelState {
    private static final java.util.Map<UUID, Channel> CHANNELS = new ConcurrentHashMap<>();

    private PlayerChannelState() {}

    public static Channel get(UUID uuid) {
        return CHANNELS.getOrDefault(uuid, Channel.RP);
    }

    public static void set(UUID uuid, Channel channel) {
        CHANNELS.put(uuid, channel);
    }

    public static void remove(UUID uuid) {
        CHANNELS.remove(uuid);
    }
}
