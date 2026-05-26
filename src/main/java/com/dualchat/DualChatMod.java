package com.dualchat;

import com.dualchat.client.FontConfig;
import com.dualchat.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DualChatMod.MODID)
public class DualChatMod {
    public static final String MODID = "dualchat";

    /** RP messages are visible within this many blocks of the speaker. */
    public static final double RP_RANGE = 16.0;

    public DualChatMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modBus.addListener(this::clientSetup));

        MinecraftForge.EVENT_BUS.register(new com.dualchat.server.ServerEventHandler());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::init);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FontConfig.load();
            FontConfig.installFontRedirect();
        });
    }
}
