package com.dualchat.client;

import com.dualchat.Channel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class ClientChannelHistory {
    private static final int MAX_HISTORY = 100;

    public record RawMessage(Channel channel, String senderName, String body) {}

    private static final Map<Channel, Deque<RawMessage>> histories = new EnumMap<>(Channel.class);

    static {
        for (Channel ch : Channel.values()) histories.put(ch, new ArrayDeque<>());
    }

    private ClientChannelHistory() {}

    public static void addMessage(Channel channel, String senderName, String body) {
        Deque<RawMessage> history = histories.get(channel);
        history.addLast(new RawMessage(channel, senderName, body));
        if (history.size() > MAX_HISTORY) history.removeFirst();
    }

    public static Iterable<RawMessage> getHistory(Channel channel) {
        return histories.get(channel);
    }

    public static void clear() {
        for (Deque<RawMessage> d : histories.values()) d.clear();
    }
}
