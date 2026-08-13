package com.wintercogs.beyonddimensions;

import com.mojang.logging.LogUtils;
import com.wintercogs.beyonddimensions.api.capability.helper.CapabilityHelper;
import com.wintercogs.beyonddimensions.api.capability.helper.ordered.EnergyStackTypedHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.ordered.FluidStackTypedHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.ordered.ItemStackTypedHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.unordered.EnergyUnifiedStorageHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.unordered.FluidUnifiedStorageHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.unordered.ItemUnifiedStorageHandler;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.EnergyHandlerWrapper;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.FluidHandlerWrapper;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.ItemHandlerWrapper;
import com.wintercogs.beyonddimensions.api.capability.helper.wrapper.StackHandlerWrapperHelper;
import com.wintercogs.beyonddimensions.api.dimensionnet.NetRegistryIndex;
import com.wintercogs.beyonddimensions.api.dimensionnet.PlayerNetIndex;
import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.api.storage.key.StackKeyRegistry;
import com.wintercogs.beyonddimensions.api.storage.key.impl.EmptyStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.impl.EnergyStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.impl.FluidStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import com.wintercogs.beyonddimensions.common.command.ServerCommands;
import com.wintercogs.beyonddimensions.common.init.BDBlockEntities;
import com.wintercogs.beyonddimensions.common.init.BDBlocks;
import com.wintercogs.beyonddimensions.common.init.BDCreativeModeTabs;
import com.wintercogs.beyonddimensions.common.init.BDFluids;
import com.wintercogs.beyonddimensions.common.init.BDItems;
import com.wintercogs.beyonddimensions.common.init.BDMenus;
import com.wintercogs.beyonddimensions.common.init.BDPackets;
import com.wintercogs.beyonddimensions.integration.CompatRegistry;
import com.wintercogs.beyonddimensions.forgecompat.common.MinecraftForge;
import com.wintercogs.beyonddimensions.forgecompat.common.capabilities.ForgeCapabilities;
import com.wintercogs.beyonddimensions.forgecompat.event.server.ServerStartingEvent;
import com.wintercogs.beyonddimensions.forgecompat.eventbus.api.IEventBus;
import com.wintercogs.beyonddimensions.forgecompat.eventbus.api.SubscribeEvent;
import com.wintercogs.beyonddimensions.forgecompat.fml.javafmlmod.FMLJavaModLoadingContext;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class BeyondDimensions implements ModInitializer
{
    public static IEventBus MOD_EVENT_BUS;

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS = modEventBus;

        Config.register();
        MinecraftForge.EVENT_BUS.register(this);

        // 注册Menu
        BDMenus.register(modEventBus);
        // 注册创造模式菜单
        BDCreativeModeTabs.register(modEventBus);
        // 注册流体（必须先于物品/方块注册，确保桶与流体方块在注册阶段创建）
        BDFluids.register(modEventBus);
        // 注册物品
        BDItems.register(modEventBus);
        // 注册方块
        BDBlocks.register(modEventBus);
        // 注册方块实体
        BDBlockEntities.register(modEventBus);
        // 第三方联动内容（条件注册，检测到对应模组才注册）
        CompatRegistry.register();
        // 注册网络
        BDPackets.INSTANCE.init();

        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(ServerCommands.class);
        MinecraftForge.EVENT_BUS.register(NetRegistryIndex.class);
        MinecraftForge.EVENT_BUS.register(PlayerNetIndex.class);

        // Fabric 事件桥接
        ForgeEventBridge.init();

        // 注册堆叠类型，使得网络能够存储相关堆叠
        StackKeyRegistry.registerType(EmptyStackKey.INSTANCE);
        StackKeyRegistry.registerType(ItemStackKey.EMPTY);
        StackKeyRegistry.registerType(FluidStackKey.EMPTY);
        StackKeyRegistry.registerType(EnergyStackKey.INSTANCE);

        // 注册方块能力类型，用于动态为方块注册能力
        CapabilityHelper.BlockCapabilityMap.put(ItemStackKey.ID, ForgeCapabilities.ITEM_HANDLER);
        CapabilityHelper.BlockCapabilityMap.put(FluidStackKey.ID, ForgeCapabilities.FLUID_HANDLER);
        CapabilityHelper.BlockCapabilityMap.put(EnergyStackKey.ID, ForgeCapabilities.ENERGY);

        // 注册物品能力类型
        CapabilityHelper.ItemCapabilityMap.put(ItemStackKey.ID, ForgeCapabilities.ITEM_HANDLER);
        CapabilityHelper.ItemCapabilityMap.put(FluidStackKey.ID, ForgeCapabilities.FLUID_HANDLER_ITEM);
        CapabilityHelper.ItemCapabilityMap.put(EnergyStackKey.ID, ForgeCapabilities.ENERGY);

        // 注册网络能力，使得网络通道能暴露对应存储能力
        CapabilityHelper.registerUSHandler(ItemStackKey.EMPTY, ItemUnifiedStorageHandler::new);
        CapabilityHelper.registerUSHandler(FluidStackKey.EMPTY, FluidUnifiedStorageHandler::new);
        CapabilityHelper.registerUSHandler(EnergyStackKey.INSTANCE, EnergyUnifiedStorageHandler::new);

        // 注册存储分化包装
        CapabilityHelper.registerStackTypedHandler(ItemStackKey.EMPTY, ItemStackTypedHandler::new);
        CapabilityHelper.registerStackTypedHandler(FluidStackKey.EMPTY, FluidStackTypedHandler::new);
        CapabilityHelper.registerStackTypedHandler(EnergyStackKey.INSTANCE, EnergyStackTypedHandler::new);

        // 注册堆叠处理包装，用于动态包装来自其他模组的handler
        StackHandlerWrapperHelper.stackWrappers.put(ItemStackKey.ID, ItemHandlerWrapper::new);
        StackHandlerWrapperHelper.stackWrappers.put(FluidStackKey.ID, FluidHandlerWrapper::new);
        StackHandlerWrapperHelper.stackWrappers.put(EnergyStackKey.ID, EnergyHandlerWrapper::new);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("维度网络初始化完成(服务端)");
    }

    public static ResourceLocation makeId(String path)
    {
        return new ResourceLocation(BDConstants.MODID, path);
    }
}
