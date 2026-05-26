package com.dualchat.client;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import virtuoel.pehkui.util.ScaleUtils;

@OnlyIn(Dist.CLIENT)
final class PehkuiHook {
    private PehkuiHook() {}

    static float getModelHeightScale(Entity entity, float partialTick) {
        return ScaleUtils.getModelHeightScale(entity, partialTick);
    }
}
