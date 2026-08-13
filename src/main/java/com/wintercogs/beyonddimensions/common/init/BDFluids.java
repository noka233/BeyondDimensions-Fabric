package com.wintercogs.beyonddimensions.common.init;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.common.fluid.XpFluid;
import com.wintercogs.beyonddimensions.forgecompat.client.extensions.common.IClientFluidTypeExtensions;
import com.wintercogs.beyonddimensions.forgecompat.fluids.FluidType;
import com.wintercogs.beyonddimensions.forgecompat.registries.DeferredRegister;
import com.wintercogs.beyonddimensions.forgecompat.registries.RegistryObject;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BDFluids
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, BDConstants.MODID);

    public static final List<FluidEntry<XpFluid.Source, XpFluid.Flowing>> ALL = new ArrayList<>();

    public static final FluidEntry<XpFluid.Source, XpFluid.Flowing> XP_FLUID = registerFluid(
            "xp_fluid",
            10,
            XpFluid.Source::new,
            XpFluid.Flowing::new
    );

    public static <S extends XpFluid, F extends XpFluid> FluidEntry<S, F> registerFluid(
            String name,
            int lightlevel,
            Supplier<S> sourceCtor,
            Supplier<F> flowingCtor
    )
    {
        RegistryObject<S> source = FLUIDS.register(name, sourceCtor);
        RegistryObject<F> flowing = FLUIDS.register("flowing_" + name, flowingCtor);

        RegistryObject<LiquidBlock> block = BDBlocks.BLOCKS.register(name, () ->
                new LiquidBlock(source.get(),
                        BlockBehaviour.Properties.copy(Blocks.WATER)
                                .lightLevel(s -> lightlevel)));

        RegistryObject<Item> bucket = BDItems.ITEMS.register(name + "_bucket", () ->
                new BucketItem(source.get(),
                        new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

        FluidEntry<S, F> entry = new FluidEntry<>(name, source, flowing, block, bucket);
        ALL.add((FluidEntry<XpFluid.Source, XpFluid.Flowing>) (FluidEntry<?, ?>) entry);
        return entry;
    }

    public static void register(com.wintercogs.beyonddimensions.forgecompat.eventbus.api.IEventBus modBus)
    {
        FLUIDS.register(modBus);
    }

    public static final class ClientOnly
    {
        public static void registerRenderLayers()
        {
            for (FluidEntry<XpFluid.Source, XpFluid.Flowing> e : BDFluids.ALL)
            {
                ResourceLocation still = new ResourceLocation(BDConstants.MODID, "block/" + e.name() + "_still");
                ResourceLocation flow = new ResourceLocation(BDConstants.MODID, "block/" + e.name() + "_flow");
                IClientFluidTypeExtensions.registerTextures(e.source().get(), still, flow);
                IClientFluidTypeExtensions.registerTextures(e.flowing().get(), still, flow);
                BlockRenderLayerMap.INSTANCE.putFluid(e.source().get(), RenderType.translucent());
                BlockRenderLayerMap.INSTANCE.putFluid(e.flowing().get(), RenderType.translucent());
                // 与 Kibe 一致：贴图本身已带颜色，渲染处理器只提供贴图与不额外染色。
                // FluidVariantRendering 默认 handler 会回退到 FluidRenderHandlerRegistry，
                // 因此这里只需注册世界渲染处理器，网络 GUI 图标也会自动获得正确的贴图与颜色。
                int color = 0xFFFFFF;
                FluidRenderHandlerRegistry.INSTANCE.register(e.source().get(), new SimpleFluidRenderHandler(still, flow, color));
                FluidRenderHandlerRegistry.INSTANCE.register(e.flowing().get(), new SimpleFluidRenderHandler(still, flow, color));
            }
        }
    }

    public record FluidEntry<S extends XpFluid, F extends XpFluid>(
            String name,
            RegistryObject<S> source,
            RegistryObject<F> flowing,
            RegistryObject<LiquidBlock> block,
            RegistryObject<Item> bucket
    )
    {
    }
}
