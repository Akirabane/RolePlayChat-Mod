package com.dualchat.server;

import net.minecraftforge.fml.ModList;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlasmoVoiceIntegration {
    private PlasmoVoiceIntegration() {}

    static final Map<UUID, Integer> lastVoiceDistance = new ConcurrentHashMap<>();
    private static boolean enabled = false;

    /** Called on server start, after all mods are initialized. */
    public static void init() {
        if (!ModList.get().isLoaded("plasmovoice")) return;
        try {
            PlasmoVoiceHook.register();
            enabled = true;
        } catch (Exception ignored) {}
    }

    /**
     * Returns the last voice proximity distance (blocks) for this player,
     * or -1 if PlasmoVoice is absent or the player hasn't spoken yet.
     */
    public static int getLastDistance(UUID playerUuid) {
        if (!enabled) return -1;
        Integer d = lastVoiceDistance.get(playerUuid);
        return d != null ? d : -1;
    }
}
