package com.dualchat.client;

import com.dualchat.Channel;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class ClientChannelHistory {
    private static final int MAX_HISTORY = 100;

    private static final Map<Channel, Deque<Component>> histories = new EnumMap<>(Channel.class);

    static {
        for (Channel ch : Channel.values()) histories.put(ch, new ArrayDeque<>());
    }

    private ClientChannelHistory() {}

    public static void addMessage(Channel channel, Component msg) {
        Deque<Component> history = histories.get(channel);
        history.addLast(msg);
        if (history.size() > MAX_HISTORY) history.removeFirst();
    }

    public static Iterable<Component> getHistory(Channel channel) {
        return histories.get(channel);
    }

    public static void clear() {
        for (Deque<Component> d : histories.values()) d.clear();
    }
}
