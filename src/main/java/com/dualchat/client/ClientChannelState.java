package com.dualchat.client;

import com.dualchat.Channel;
import com.dualchat.network.C2SSetChannelPacket;
import com.dualchat.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientChannelState {
    private static Channel current = Channel.HRP;

    private ClientChannelState() {}

    public static Channel get() { return current; }

    /** Switch local channel, swap the displayed chat history, and notify the server. */
    public static void set(Channel channel) {
        if (channel == current) return;
        current = channel;
        ClientChannelHistory.clearUnread(channel);
        NetworkHandler.sendToServer(new C2SSetChannelPacket(channel));
        swapHistory(channel);
    }

    /** Re-render the current channel with the current font (called on font change). */
    public static void refreshCurrentChannel() {
        swapHistory(current);
    }

    private static void swapHistory(Channel channel) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;
        mc.gui.getChat().clearMessages(false);
        for (ClientChannelHistory.RawMessage msg : ClientChannelHistory.getHistory(channel)) {
            mc.gui.getChat().addMessage(ClientChatReceiver.render(msg));
        }
    }

    /** Reset to HRP on world-join (history already cleared by caller), clear visible chat, notify server. */
    public static void reset() {
        current = Channel.HRP;
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui != null) mc.gui.getChat().clearMessages(false);
        NetworkHandler.sendToServer(new C2SSetChannelPacket(current));
    }

    /** Resend the current channel to the server without changing local state. */
    public static void resync() {
        NetworkHandler.sendToServer(new C2SSetChannelPacket(current));
    }
}
