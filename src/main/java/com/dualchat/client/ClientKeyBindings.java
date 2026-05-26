package com.dualchat.client;

import com.dualchat.DualChatMod;
import com.dualchat.client.gui.FontPickerScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DualChatMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientKeyBindings {
    private ClientKeyBindings() {}

    public static final KeyMapping OPEN_FONT_PICKER = new KeyMapping(
        "key.dualchat.font_picker",
        InputConstants.UNKNOWN.getValue(),
        "key.categories.dualchat"
    );

    @SubscribeEvent
    public static void onRegister(RegisterKeyMappingsEvent event) {
        event.register(OPEN_FONT_PICKER);
    }

    public static void tick() {
        while (OPEN_FONT_PICKER.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null) {
                mc.setScreen(new FontPickerScreen(null));
            }
        }
    }
}
