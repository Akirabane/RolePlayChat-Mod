package com.dualchat.client;

import com.dualchat.Channel;
import net.minecraft.network.chat.Component;
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

    /**
     * A stored chat line. Mod messages (RP/HRP) carry senderName + body and are
     * re-styled on display. Vanilla / system / other-mod messages carry a pre-built
     * {@code raw} Component instead, so their clickable/hoverable text is preserved.
     */
    public record RawMessage(Channel channel, String senderName, String body, Component raw) {}

    private static final Map<Channel, Deque<RawMessage>> histories = new EnumMap<>(Channel.class);
    private static final Set<Channel> unread = EnumSet.noneOf(Channel.class);

    static {
        for (Channel ch : Channel.values()) histories.put(ch, new ArrayDeque<>());
    }

    private ClientChannelHistory() {}

    public static void addMessage(Channel channel, String senderName, String body) {
        push(channel, new RawMessage(channel, senderName, body, null));
    }

    /** Stores a vanilla/system/other-mod Component in the given channel, keeping it intact. */
    public static void addVanilla(Channel channel, Component raw) {
        push(channel, new RawMessage(channel, "", "", raw));
    }

    private static void push(Channel channel, RawMessage msg) {
        Deque<RawMessage> history = histories.get(channel);
        history.addLast(msg);
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
