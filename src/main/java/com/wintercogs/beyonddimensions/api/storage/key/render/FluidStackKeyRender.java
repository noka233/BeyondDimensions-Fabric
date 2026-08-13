package com.wintercogs.beyonddimensions.api.storage.key.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import com.wintercogs.beyonddimensions.api.storage.key.IStackKey;
import com.wintercogs.beyonddimensions.api.storage.key.IStackRender;
import com.wintercogs.beyonddimensions.api.storage.key.impl.FluidStackKey;
import com.wintercogs.beyonddimensions.util.IngredientRenderer;
import com.wintercogs.beyonddimensions.util.StringFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import com.wintercogs.beyonddimensions.forgecompat.client.extensions.common.IClientFluidTypeExtensions;
import com.wintercogs.beyonddimensions.forgecompat.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidStackKeyRender implements IStackRender
{
    public static final FluidStackKeyRender INSTANCE = new FluidStackKeyRender();

    @Override
    public void render(GuiGraphics gui, IStackKey<?> key, int x, int y)
    {
        if (key instanceof FluidStackKey fluidKey)
        {
            // 渲染流体图标（16×16）
            var pose = gui.pose();
            pose.pushPose();

            FluidStack stack = fluidKey.getRenderStack();
            if (!stack.isEmpty())
            {
                var fluid = stack.getFluid();
                FluidVariant variant = FluidVariant.of(fluid, stack.getTag());
                TextureAtlasSprite sprite = FluidVariantRendering.getSprite(variant);
                if (sprite != null && sprite.atlasLocation() != MissingTextureAtlasSprite.getLocation())
                {
                    int color = FluidVariantRendering.getColor(variant);
                    drawTintedSprite(gui.pose(), sprite, color, x, y, 16, 16);
                }
            }

            pose.popPose();
        }

    }

    @Override
    public void renderAmount(GuiGraphics gui, long amount, int x, int y)
    {
        // 渲染数量文本（右下角）
        String text = getCountText(amount);
        if (text.isEmpty()) return;

        float scale = 0.666f;

        var pose = gui.pose();
        pose.pushPose();
        pose.translate(0, 0, 200);
        pose.scale(scale, scale, scale);
        RenderSystem.disableBlend();

        int w = Minecraft.getInstance().font.width(text);
        final int X = (int) ((x - 1 + 16.0f + 2.0f - w * 0.666f) / 0.666f);
        final int Y = (int) ((y - 1 + 16.0f - 5.0f * 0.666f) / 0.666f);

        gui.drawString(Minecraft.getInstance().font, text, X, Y, 0xFFFFFF);
        pose.popPose();
    }

    @Override
    public String getCountText(long count)
    {
        if (count < 0) return "";
        return StringFormat.formatBucket(count);
    }

    @Override
    public Component getDisplayName(IStackKey<?> key)
    {
        if (key instanceof FluidStackKey fluidKey)
        {
            FluidStack stack = fluidKey.getRenderStack();
            if (stack.isEmpty())
            {
                return Component.empty();
            }
            var tooltip = FluidVariantRendering.getTooltip(FluidVariant.of(stack.getFluid(), stack.getTag()));
            if (tooltip != null && !tooltip.isEmpty())
            {
                return tooltip.get(0);
            }
            return stack.getDisplayName();
        }
        return Component.empty();
    }

    private static void drawTintedSprite(PoseStack poseStack, TextureAtlasSprite sprite, int color, int x, int y, int width, int height)
    {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.enableBlend();

        float r = ((color >> 16) & 255) / 256F;
        float g = ((color >> 8) & 255) / 256F;
        float b = (color & 255) / 256F;
        float a = ((color >> 24) & 255) / 256F;

        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float x1 = x + width;
        float y1 = y + height;
        Matrix4f matrix = poseStack.last().pose();
        buffer.vertex(matrix, x, y1, 1F).color(r, g, b, a).uv(u0, v1).endVertex();
        buffer.vertex(matrix, x1, y1, 1F).color(r, g, b, a).uv(u1, v1).endVertex();
        buffer.vertex(matrix, x1, y, 1F).color(r, g, b, a).uv(u1, v0).endVertex();
        buffer.vertex(matrix, x, y, 1F).color(r, g, b, a).uv(u0, v0).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    @Override
    public List<Component> getTooltipLines(IStackKey<?> key, long amount,
                                           @Nullable Player player,
                                           TooltipFlag tooltipFlag)
    {
        List<Component> lines = new ArrayList<>();
        lines.add(getDisplayName(key));
        lines.add(Component.translatable("istack.beyonddimensions.storage_num.fluid", amount));
        return lines;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(IStackKey<?> key)
    {
        // 流体默认无额外 TooltipComponent
        return Optional.empty();
    }

    @Override
    public void renderTooltip(GuiGraphics gui, Font font, IStackKey<?> key, long amount, int mouseX, int mouseY)
    {
        var mc = Minecraft.getInstance();
        gui.renderTooltip(mc.font, getTooltipLines(key, amount, mc.player, mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL), getTooltipImage(key), mouseX, mouseY);
    }
}
