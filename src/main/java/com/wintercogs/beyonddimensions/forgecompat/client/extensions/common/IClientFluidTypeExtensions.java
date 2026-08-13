package com.wintercogs.beyonddimensions.forgecompat.client.extensions.common;

import com.wintercogs.beyonddimensions.forgecompat.fluids.FluidStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public interface IClientFluidTypeExtensions
{
    Map<Fluid, ResourceLocation[]> TEXTURES = new HashMap<>();

    ResourceLocation[] DEFAULT_TEXTURES = {
            new ResourceLocation("minecraft", "block/water_still"),
            new ResourceLocation("minecraft", "block/water_flow")
    };

    static void registerTextures(Fluid fluid, ResourceLocation still, ResourceLocation flowing)
    {
        TEXTURES.put(fluid, new ResourceLocation[]{still, flowing});
    }

    static IClientFluidTypeExtensions of(Fluid fluid)
    {
        return new IClientFluidTypeExtensions()
        {
            private final ResourceLocation[] textures = TEXTURES.getOrDefault(fluid, DEFAULT_TEXTURES);

            @Override
            public ResourceLocation getStillTexture()
            {
                return textures[0];
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return textures[1];
            }
        };
    }

    default ResourceLocation getStillTexture()
    {
        return null;
    }

    default ResourceLocation getFlowingTexture()
    {
        return null;
    }

    default int getTintColor()
    {
        return 0xFFFFFFFF;
    }

    default int getTintColor(FluidStack stack)
    {
        return getTintColor();
    }

    default ResourceLocation getStillTexture(FluidStack stack)
    {
        return getStillTexture();
    }
}
