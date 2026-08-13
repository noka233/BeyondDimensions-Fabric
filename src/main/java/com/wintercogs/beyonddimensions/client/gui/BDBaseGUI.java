package com.wintercogs.beyonddimensions.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.wintercogs.beyonddimensions.api.storage.key.KeyAmount;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import com.wintercogs.beyonddimensions.common.init.BDPackets;
import com.wintercogs.beyonddimensions.common.menu.BDBaseMenu;
import com.wintercogs.beyonddimensions.common.menu.widget.slot.AbstractStackTypedSlot;
import com.wintercogs.beyonddimensions.network.packet.c2s.BatchTransferPacket;
import com.wintercogs.beyonddimensions.network.packet.c2s.CallSeverClickPacket;
import com.wintercogs.beyonddimensions.client.gui.widget.shared.IconButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.jetbrains.annotations.NotNull;


// 更改渲染以及点击事件，以适配StoredStackSlot
public abstract class BDBaseGUI<T extends BDBaseMenu> extends AbstractContainerScreen<T>
{

    // 用于 shift双击加左键的效果
    ItemStack lastInvClickedStack = ItemStack.EMPTY;
    ItemStackKey lastStorageClickedStack = ItemStackKey.EMPTY;
    int lastInvClickedSlot = -1;
    int cleanHold = 10; // 给予半秒时间

    public BDBaseGUI(T menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init()
    {
        super.init();
        registerTypingKeySuppressor();
    }

    /**
     * Fabric 上 Balm/TrashSlot 等模组通过 ScreenKeyboardEvents.afterKeyPress 监听按键，
     * 该事件在 Screen.keyPressed 之后无条件触发（与 Forge 的 ScreenEvent.KeyPressed.Post 语义不同），
     * 导致在本模组输入框内打字时可能误触发第三方键位（例如 Trash Slot 的 T）。
     * 这里在输入框聚焦时拦截“无 Ctrl/Alt/Super 修饰的纯字符键”，使 afterKeyPress 不会触发；
     * 字符输入仍由独立的 charTyped 回调完成，退格/方向键/Ctrl+C/V 等编辑操作不受影响。
     */
    private void registerTypingKeySuppressor()
    {
        ScreenKeyboardEvents.allowKeyPress(this).register((screen, keyCode, scanCode, modifiers) ->
        {
            if (!(getFocused() instanceof EditBox editBox) || !editBox.canConsumeInput())
            {
                return true;
            }

            // 带 Ctrl/Alt/Super 的组合键放行，保留复制/粘贴/全选等编辑快捷键
            if ((modifiers & (GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SUPER)) != 0)
            {
                return true;
            }

            return !isTypingKey(keyCode);
        });
    }

    private static boolean isTypingKey(int keyCode)
    {
        return switch (keyCode)
        {
            case GLFW.GLFW_KEY_SPACE,
                 GLFW.GLFW_KEY_APOSTROPHE,
                 GLFW.GLFW_KEY_COMMA,
                 GLFW.GLFW_KEY_MINUS,
                 GLFW.GLFW_KEY_PERIOD,
                 GLFW.GLFW_KEY_SLASH,
                 GLFW.GLFW_KEY_0,
                 GLFW.GLFW_KEY_1,
                 GLFW.GLFW_KEY_2,
                 GLFW.GLFW_KEY_3,
                 GLFW.GLFW_KEY_4,
                 GLFW.GLFW_KEY_5,
                 GLFW.GLFW_KEY_6,
                 GLFW.GLFW_KEY_7,
                 GLFW.GLFW_KEY_8,
                 GLFW.GLFW_KEY_9,
                 GLFW.GLFW_KEY_SEMICOLON,
                 GLFW.GLFW_KEY_EQUAL,
                 GLFW.GLFW_KEY_A,
                 GLFW.GLFW_KEY_B,
                 GLFW.GLFW_KEY_C,
                 GLFW.GLFW_KEY_D,
                 GLFW.GLFW_KEY_E,
                 GLFW.GLFW_KEY_F,
                 GLFW.GLFW_KEY_G,
                 GLFW.GLFW_KEY_H,
                 GLFW.GLFW_KEY_I,
                 GLFW.GLFW_KEY_J,
                 GLFW.GLFW_KEY_K,
                 GLFW.GLFW_KEY_L,
                 GLFW.GLFW_KEY_M,
                 GLFW.GLFW_KEY_N,
                 GLFW.GLFW_KEY_O,
                 GLFW.GLFW_KEY_P,
                 GLFW.GLFW_KEY_Q,
                 GLFW.GLFW_KEY_R,
                 GLFW.GLFW_KEY_S,
                 GLFW.GLFW_KEY_T,
                 GLFW.GLFW_KEY_U,
                 GLFW.GLFW_KEY_V,
                 GLFW.GLFW_KEY_W,
                 GLFW.GLFW_KEY_X,
                 GLFW.GLFW_KEY_Y,
                 GLFW.GLFW_KEY_Z,
                 GLFW.GLFW_KEY_LEFT_BRACKET,
                 GLFW.GLFW_KEY_BACKSLASH,
                 GLFW.GLFW_KEY_RIGHT_BRACKET,
                 GLFW.GLFW_KEY_GRAVE_ACCENT,
                 GLFW.GLFW_KEY_KP_0,
                 GLFW.GLFW_KEY_KP_1,
                 GLFW.GLFW_KEY_KP_2,
                 GLFW.GLFW_KEY_KP_3,
                 GLFW.GLFW_KEY_KP_4,
                 GLFW.GLFW_KEY_KP_5,
                 GLFW.GLFW_KEY_KP_6,
                 GLFW.GLFW_KEY_KP_7,
                 GLFW.GLFW_KEY_KP_8,
                 GLFW.GLFW_KEY_KP_9,
                 GLFW.GLFW_KEY_KP_DECIMAL,
                 GLFW.GLFW_KEY_KP_DIVIDE,
                 GLFW.GLFW_KEY_KP_MULTIPLY,
                 GLFW.GLFW_KEY_KP_SUBTRACT,
                 GLFW.GLFW_KEY_KP_ADD,
                 GLFW.GLFW_KEY_KP_EQUAL -> true;
            default -> false;
        };
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem())
        {
            if (this.hoveredSlot instanceof AbstractStackTypedSlot sSlot)
            {
                KeyAmount stack = sSlot.getStack();
                stack.key().getRender().renderTooltip(guiGraphics, minecraft.font, stack.key(), stack.amount(), mouseX, mouseY);
            }
            else
            {
                ItemStack itemstack = this.hoveredSlot.getItem();
                guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), java.util.Optional.empty(), mouseX, mouseY);
            }
        }
    }



    @Override
    protected void containerTick()
    {
        super.containerTick();

        if (cleanHold > 0)
        {
            cleanHold--;
        }
        else
        {
            lastInvClickedStack = ItemStack.EMPTY;
            lastStorageClickedStack = ItemStackKey.EMPTY;
            lastInvClickedSlot = -1;
            cleanHold = 10;
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        // 先把事件交给当前 focused 控件
        GuiEventListener focused = this.getFocused();
        if (focused != null && this.isDragging())
        {
            if (focused.mouseDragged(mouseX, mouseY, button, dragX, dragY))
            {
                return true;
            }
            // 返回 false，继续往下走，让容器/槽逻辑按需处理
        }

        // 命中自定义槽位：拦截容器 quick-craft，不让容器接管
        Slot slot = this.findSlot(mouseX, mouseY);
        if (slot instanceof AbstractStackTypedSlot) return true;

        // 其它情况：让容器逻辑处理
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        // AbstractContainerScreen的drag没有调用组件drag，但是Release却调用了，不需要手动重复处理
        // 在此注释，防止我某一天忘记了
        boolean result = super.mouseReleased(mouseX, mouseY, button);

        // 1.20.1 原版 AbstractContainerScreen.mouseReleased 只处理槽位/拖拽等容器逻辑，
        // 并不调用 super.mouseReleased，因此原版 Screen 中"把释放事件转发给聚焦组件"的
        // 路径不会执行——组件的 onRelease 永远不会被触发。结果：点击按钮后焦点一直保留
        // 在按钮上，按钮持续显示高亮背景（点击状态不还原），只有重开界面重建组件才恢复。
        // 这里在父类逻辑完成后补做转发，并主动清除按钮焦点。
        GuiEventListener focused = this.getFocused();
        if (focused instanceof IconButton)
        {
            focused.mouseReleased(mouseX, mouseY, button);
            focused.setFocused(false);
            this.setFocused(null);
        }

        return result;
    }

    @Override
    protected void slotClicked(@NotNull Slot slot, int slotIndex, int mouseButton, @NotNull ClickType type)
    {
        if (!(slot instanceof AbstractStackTypedSlot))
            super.slotClicked(slot, slotIndex, mouseButton, type);


        if (slot == null) return; // slot绝对可能为null，不可移除此行

        int slotId = slot.index;
        KeyAmount clickItem;
        if (hasShiftDown())
        {
            if (slot instanceof AbstractStackTypedSlot sSlot)
            {
                clickItem = sSlot.getVanillaActualStack();
                if (!lastStorageClickedStack.isEmpty() && lastStorageClickedStack.equals(clickItem.key()))
                {
                    // TODO 相当一部分人不喜欢存储物品双击后全量进入背包的实现，所以我们先禁用这条线，回头有空再整体更改点击处理
                    // BDPackets.INSTANCE.sendToServer(new BatchTransferPacket(clickItem, false));
                }
                else if (!clickItem.isEmpty() && clickItem.key() instanceof ItemStackKey itemStackKey)
                {
                    this.lastStorageClickedStack = itemStackKey;
                }
            }
            else
            {
                clickItem = new KeyAmount(new ItemStackKey(slot.getItem()), slot.getItem().getCount());

                // 快速移动仓库物品
                // 原版会处理一部分快速移动 此处处理原版未能正常处理的部分
                // 理论上说，这俩者即使同时操作一个槽位也不会导致物品复制等bug
                // 因为操作基本全由服务端处理
                if (lastInvClickedSlot == slotId && !lastInvClickedStack.isEmpty())
                {
                    BDPackets.INSTANCE.sendToServer(new BatchTransferPacket(new KeyAmount(new ItemStackKey(lastInvClickedStack), lastInvClickedStack.getCount()), true));
                }
                else if (menu.inventoryStartIndex <= slotId && slotId < menu.inventoryEndIndex)
                {
                    lastInvClickedStack = slot.getItem();
                    lastInvClickedSlot = slotId;
                }

            }
            BDPackets.INSTANCE.sendToServer(new CallSeverClickPacket(slotId, clickItem, mouseButton, true));
        }
        else
        {
            if (slot instanceof AbstractStackTypedSlot sSlot)
            {
                if (sSlot.isFake())
                {
                    // 对于标记槽位
                    clickItem = sSlot.getVanillaActualStack();
                    BDPackets.INSTANCE.sendToServer(new CallSeverClickPacket(slotId, clickItem, mouseButton, false));
                }
                else
                {
                    clickItem = sSlot.getVanillaActualStack();
                    BDPackets.INSTANCE.sendToServer(new CallSeverClickPacket(slotId, clickItem, mouseButton, false));
                }
            }
        }

    }


    @Override
    protected boolean checkHotbarKeyPressed(int keyCode, int scanCode)
    {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null)
        {

            if (hoveredSlot instanceof AbstractStackTypedSlot sSlot)
            {

            }
            else
            {
                // 副手交换仅对于非存储槽才生效
                if (this.minecraft.options.keySwapOffhand.matches(keyCode, scanCode))
                {
                    this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
                    return true;
                }
                for (int i = 0; i < 9; ++i)
                {
                    if (this.minecraft.options.keyHotbarSlots[i].matches(keyCode, scanCode))
                    {
                        this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public Font getFont()
    {
        return font;
    }

    /**
     * 由 Mixin 在 AbstractContainerScreen.render 之后调用，
     * 补绘流体/能量等非物品类型的槽位图标（物品由原版渲染）。
     */
    public void renderStackSlots(GuiGraphics guiGraphics)
    {
        for (Slot slot : menu.slots)
        {
            if (!slot.isActive() || !(slot instanceof AbstractStackTypedSlot sSlot))
            {
                continue;
            }
            KeyAmount stack = sSlot.getStack();
            if (stack == null || stack.key().isEmpty())
            {
                continue;
            }
            if (stack.key() instanceof ItemStackKey)
            {
                continue; // 物品图标已由原版渲染
            }
            int x = leftPos + slot.x;
            int y = topPos + slot.y;
            stack.key().getRender().render(guiGraphics, stack.key(), x, y);
            stack.key().getRender().renderAmount(guiGraphics, stack.amount(), x, y);
        }
    }

}
