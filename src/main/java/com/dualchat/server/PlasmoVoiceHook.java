package com.dualchat.server;

import su.plo.voice.api.event.EventPriority;
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEvent;
import su.plo.voice.server.ModVoiceServer;

import java.util.UUID;

/** Only loaded at runtime when PlasmoVoice is present. Never reference this class directly. */
final class PlasmoVoiceHook {
    private PlasmoVoiceHook() {}

    static void register() {
        ModVoiceServer.INSTANCE.getEventBus().register(
            PlasmoVoiceHook.class,
            PlayerSpeakEvent.class,
            EventPriority.NORMAL,
            event -> {
                short dist = event.getPacket().getDistance();
                if (dist <= 0) return;
                UUID uuid = event.getPlayer().getInstance().getUuid();
                PlasmoVoiceIntegration.lastVoiceDistance.put(uuid, (int) dist);
            }
        );
    }
}
