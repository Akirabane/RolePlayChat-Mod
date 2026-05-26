package com.dualchat.client;

import com.dualchat.Channel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientChatReceiver {
    private ClientChatReceiver() {}

    public static void receive(Channel channel, String senderName, String body) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui == null) return;

        TextColor tagColor = TextColor.fromRgb(channel.displayColor() & 0xFFFFFF);

        MutableComponent line = Component.empty()
            .append(Component.literal("[" + channel.displayName() + "] ")
                .setStyle(Style.EMPTY.withColor(tagColor).withBold(true)))
            .append(Component.literal("<" + senderName + "> ")
                .withStyle(ChatFormatting.GRAY))
            .append(Component.literal(body)
                .withStyle(ChatFormatting.WHITE));

        ClientChannelHistory.addMessage(channel, line);
        if (channel == ClientChannelState.get()) {
            mc.gui.getChat().addMessage(line);
        }
    }
}
