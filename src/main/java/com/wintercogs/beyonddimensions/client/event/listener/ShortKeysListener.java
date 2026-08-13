package com.wintercogs.beyonddimensions.client.event.listener;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.client.init.BDShortKeys;
import com.wintercogs.beyonddimensions.forgecompat.api.distmarker.Dist;
import com.wintercogs.beyonddimensions.forgecompat.event.TickEvent;
import com.wintercogs.beyonddimensions.forgecompat.eventbus.api.SubscribeEvent;
import com.wintercogs.beyonddimensions.forgecompat.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BDConstants.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ShortKeysListener
{
    @SubscribeEvent
    public static void onKeyInput(TickEvent.ClientTickEvent event)
    {
        BDShortKeys.processKeyInput();
    }

}
