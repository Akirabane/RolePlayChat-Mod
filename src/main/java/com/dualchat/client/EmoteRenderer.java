package com.dualchat.client;

import com.dualchat.DualChatMod;
import com.dualchat.client.FontConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = DualChatMod.MODID, value = Dist.CLIENT)
public final class EmoteRenderer {
    private EmoteRenderer() {}

    private static final int   MAX_LINE_PX      = 200;
    private static final int   MAX_LINES        = 4;
    private static final float CHARS_PER_SECOND = 25f;
    private static final long  LINGER_MILLIS    = 5000L;

    private static final Map<UUID, EmoteState> activeEmotes = new HashMap<>();

    public static void receive(UUID senderUUID, String text) {
        Font font = Minecraft.getInstance().font;
        List<String> lines = wrapText(font, text, MAX_LINE_PX);
        activeEmotes.put(senderUUID, new EmoteState(lines, System.currentTimeMillis()));
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        if (!(event.getEntity() instanceof Player player)) return;

        EmoteState state = activeEmotes.get(player.getUUID());
        if (state == null) return;

        long now = System.currentTimeMillis();
        if (state.isExpired(now)) {
            activeEmotes.remove(player.getUUID());
            return;
        }

        List<String> visible = state.getVisibleLines(now);
        if (visible.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        Camera camera = mc.gameRenderer.getMainCamera();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();

        float pehkuiScale = PehkuiCompat.getModelHeightScale(player, event.getPartialTick());

        poseStack.pushPose();
        // Divide by pehkuiScale: the PoseStack is already in Pehkui-scaled space, so we
        // un-scale the offset to keep the text a constant 0.6 world-units above the head.
        poseStack.translate(0.0, (player.getBbHeight() + 0.6) / pehkuiScale, 0.0);
        poseStack.mulPose(camera.rotation());
        float scale = 0.025f;
        poseStack.scale(-scale, -scale, scale);

        float lineH  = font.lineHeight + 3f;
        float totalH = visible.size() * lineH;

        for (int i = 0; i < visible.size(); i++) {
            Component line = Component.literal(visible.get(i))
                .withStyle(Style.EMPTY.withFont(FontConfig.getCurrent()));
            float x = -font.width(line) / 2f;
            float y = i * lineH - totalH / 2f;
            font.drawInBatch(line, x, y, 0xFFFFFF, false,
                poseStack.last().pose(), bufferSource,
                Font.DisplayMode.NORMAL, 0x40000000, packedLight);
        }

        poseStack.popPose();
    }

    private static List<String> wrapText(Font font, String text, int maxPx) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            int spaceIdx = text.indexOf(' ', i);
            String word = spaceIdx == -1 ? text.substring(i) : text.substring(i, spaceIdx);
            i = spaceIdx == -1 ? text.length() : spaceIdx + 1;
            if (word.isEmpty()) continue;

            // Hard-wrap if the word alone exceeds maxPx
            while (measureWidth(font, word) > maxPx) {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current = new StringBuilder();
                }
                int fit = fitChars(font, word, maxPx);
                lines.add(word.substring(0, fit));
                word = word.substring(fit);
            }
            if (word.isEmpty()) continue;

            String candidate = current.length() == 0 ? word : current + " " + word;
            if (measureWidth(font, candidate) <= maxPx) {
                if (current.length() > 0) current.append(' ');
                current.append(word);
            } else {
                if (current.length() > 0) lines.add(current.toString());
                current = new StringBuilder(word);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        if (lines.isEmpty()) lines.add(text.substring(0, Math.min(text.length(), 20)));

        if (lines.size() > MAX_LINES) {
            lines = new ArrayList<>(lines.subList(0, MAX_LINES));
            String last = lines.get(MAX_LINES - 1);
            String truncated = last;
            while (measureWidth(font, truncated + "…") > maxPx && truncated.length() > 0)
                truncated = truncated.substring(0, truncated.length() - 1);
            lines.set(MAX_LINES - 1, truncated + "…");
        }
        return lines;
    }

    private static int measureWidth(Font font, String text) {
        return font.width(Component.literal(text).withStyle(Style.EMPTY.withFont(FontConfig.getCurrent())));
    }

    private static int fitChars(Font font, String text, int maxPx) {
        int lo = 1, hi = text.length();
        while (lo < hi) {
            int mid = (lo + hi + 1) / 2;
            if (measureWidth(font, text.substring(0, mid)) <= maxPx) lo = mid;
            else hi = mid - 1;
        }
        return Math.max(1, lo);
    }

    private static final class EmoteState {
        final List<String> lines;
        final long         startMillis;
        final int          totalChars;

        EmoteState(List<String> lines, long startMillis) {
            this.lines       = lines;
            this.startMillis = startMillis;
            this.totalChars  = lines.stream().mapToInt(String::length).sum();
        }

        int getRevealedChars(long now) {
            float elapsed = (now - startMillis) / 1000f;
            return Math.min((int)(elapsed * CHARS_PER_SECOND), totalChars);
        }

        boolean isExpired(long now) {
            if (getRevealedChars(now) < totalChars) return false;
            long doneAt = startMillis + (long)(totalChars * 1000f / CHARS_PER_SECOND);
            return (now - doneAt) > LINGER_MILLIS;
        }

        List<String> getVisibleLines(long now) {
            int remaining = getRevealedChars(now);
            List<String> result = new ArrayList<>();
            for (String line : lines) {
                if (remaining <= 0) break;
                int show = Math.min(remaining, line.length());
                result.add(line.substring(0, show));
                remaining -= show;
            }
            return result;
        }
    }
}
