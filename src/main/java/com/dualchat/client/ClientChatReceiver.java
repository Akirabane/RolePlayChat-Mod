package com.dualchat.client;

import com.dualchat.Channel;
import com.dualchat.client.FontConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientChatReceiver {
    private ClientChatReceiver() {}

    private static final ResourceLocation HRP_FONT = new ResourceLocation("minecraft", "uniform");

    /** Builds a fully-styled Component for a message, using the current RP font (or HRP font). */
    public static MutableComponent buildLine(Channel channel, String senderName, String body) {
        TextColor tagColor = TextColor.fromRgb(channel.displayColor() & 0xFFFFFF);
        if (channel == Channel.RP) {
            Style rp = Style.EMPTY.withFont(FontConfig.getCurrent());
            return Component.empty()
                .append(Component.literal("[" + channel.displayName() + "] ")
                    .setStyle(rp.withColor(tagColor).withBold(true)))
                .append(Component.literal("<" + senderName + "> ")
                    .setStyle(rp.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))))
                .append(Component.literal(body)
                    .setStyle(rp.withColor(TextColor.fromLegacyFormat(ChatFormatting.WHITE))));
        } else {
            Style hrp = Style.EMPTY.withFont(HRP_FONT);
            return Component.empty()
                .append(Component.literal("[" + channel.displayName() + "] ")
                    .setStyle(hrp.withColor(tagColor).withBold(true)))
                .append(Component.literal("<" + senderName + "> ")
                    .setStyle(hrp.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY))))
                .append(Component.literal(body)
                    .setStyle(hrp.withColor(TextColor.fromLegacyFormat(ChatFormatting.WHITE))));
        }
    }

    public static void receive(Channel channel, String senderName, String body) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        ClientChannelHistory.addMessage(channel, senderName, body);
        if (channel == ClientChannelState.get()) {
            mc.gui.getChat().addMessage(buildLine(channel, senderName, body));
        } else {
            ClientChannelHistory.markUnread(channel);
        }
    }

    /** Renders a stored line: raw Component as-is, or a re-styled mod message. */
    public static Component render(ClientChannelHistory.RawMessage msg) {
        return msg.raw() != null ? msg.raw() : buildLine(msg.channel(), msg.senderName(), msg.body());
    }
}
