package com.dualchat.client;

import com.dualchat.Channel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public final class ClientChannelHistory {
    private static final int MAX_HISTORY = 100;

    public record RawMessage(Channel channel, String senderName, String body) {}

    private static final Map<Channel, Deque<RawMessage>> histories = new EnumMap<>(Channel.class);
    private static final Set<Channel> unread = EnumSet.noneOf(Channel.class);

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

    public static void markUnread(Channel channel) {
        unread.add(channel);
    }

    public static void clearUnread(Channel channel) {
        unread.remove(channel);
    }

    public static boolean hasUnread(Channel channel) {
        return unread.contains(channel);
    }

    public static int getHistorySize(Channel channel) {
        return histories.get(channel).size();
    }

    public static void clear() {
        for (Deque<RawMessage> d : histories.values()) d.clear();
        unread.clear();
    }
}
