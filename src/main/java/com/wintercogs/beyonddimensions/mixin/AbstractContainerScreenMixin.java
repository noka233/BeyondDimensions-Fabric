package com.wintercogs.beyonddimensions.mixin;

import com.wintercogs.beyonddimensions.client.gui.BDBaseGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin
{
    @Inject(method = "render", at = @At("TAIL"))
    private void beyonddimensions$renderStackSlots(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        if (screen instanceof BDBaseGUI<?> bdGui)
        {
            bdGui.renderStackSlots(guiGraphics);
        }
    }
}
