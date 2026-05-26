package com.dualchat.client;

import com.dualchat.Channel;
import com.dualchat.network.C2SSetChannelPacket;
import com.dualchat.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientChannelState {
    private static Channel current = Channel.RP;

    private ClientChannelState() {}

    public static Channel get() { return current; }

    /** Switch local channel, swap the displayed chat history, and notify the server. */
    public static void set(Channel channel) {
        if (channel == current) return;
        current = channel;
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
            mc.gui.getChat().addMessage(ClientChatReceiver.buildLine(msg.channel(), msg.senderName(), msg.body()));
        }
    }

    /** Resend the current channel — used right after world-join so the server's default (RP) matches our UI. */
    public static void resync() {
        NetworkHandler.sendToServer(new C2SSetChannelPacket(current));
    }
}
