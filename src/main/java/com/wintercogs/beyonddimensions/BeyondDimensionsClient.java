package com.wintercogs.beyonddimensions;

import com.wintercogs.beyonddimensions.client.command.ClientCommands;
import com.wintercogs.beyonddimensions.client.event.listener.ShortKeysListener;
import com.wintercogs.beyonddimensions.client.init.BDBlockRenders;
import com.wintercogs.beyonddimensions.client.init.BDScreens;
import com.wintercogs.beyonddimensions.client.init.BDShortKeys;
import com.wintercogs.beyonddimensions.common.init.BDPackets;
import com.wintercogs.beyonddimensions.forgecompat.common.MinecraftForge;
import com.wintercogs.beyonddimensions.forgecompat.fml.event.lifecycle.FMLClientSetupEvent;
import com.wintercogs.beyonddimensions.forgecompat.fml.javafmlmod.FMLJavaModLoadingContext;
import com.wintercogs.beyonddimensions.util.TooltipHelper;
import net.fabricmc.api.ClientModInitializer;

public class BeyondDimensionsClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(BeyondDimensionsClient::onClientSetup);
        modBus.addListener(BDBlockRenders::onRegisterRenderers);
        BDShortKeys.registerKeys();
        BDScreens.registerScreens();
        com.wintercogs.beyonddimensions.common.init.BDFluids.ClientOnly.registerRenderLayers();

        MinecraftForge.EVENT_BUS.register(ShortKeysListener.class);
        MinecraftForge.EVENT_BUS.register(TooltipHelper.class);
        ClientCommands.register();

        ForgeEventBridge.initClient();
        BDPackets.INSTANCE.initClient();
    }

    public static void onClientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            BeyondDimensions.LOGGER.info("维度网络初始化完成(客户端)");
        });
    }
}
