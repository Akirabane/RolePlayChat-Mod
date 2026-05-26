package com.dualchat.client;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

@OnlyIn(Dist.CLIENT)
public final class PehkuiCompat {
    private static boolean checked = false;
    private static boolean available = false;

    private PehkuiCompat() {}

    public static float getModelHeightScale(Entity entity, float partialTick) {
        if (!checked) {
            available = ModList.get().isLoaded("pehkui");
            checked = true;
        }
        if (!available) return 1.0f;
        try {
            return PehkuiHook.getModelHeightScale(entity, partialTick);
        } catch (Exception e) {
            return 1.0f;
        }
    }
}
