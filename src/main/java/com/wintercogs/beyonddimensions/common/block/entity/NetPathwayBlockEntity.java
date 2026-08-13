package com.wintercogs.beyonddimensions.common.block.entity;

import com.wintercogs.beyonddimensions.api.capability.helper.CapabilityHelper;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.util.CapCtx;
import com.wintercogs.beyonddimensions.api.util.USHandler;
import com.wintercogs.beyonddimensions.common.init.BDBlockEntities;
import com.wintercogs.beyonddimensions.util.SidedCapId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import com.wintercogs.beyonddimensions.forgecompat.common.capabilities.Capability;
import com.wintercogs.beyonddimensions.forgecompat.common.capabilities.ForgeCapabilities;
import com.wintercogs.beyonddimensions.forgecompat.items.IItemHandler;
import com.wintercogs.beyonddimensions.forgecompat.common.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetPathwayBlockEntity extends NetedBlockEntity implements WorldlyContainer
{
    private final Map<SidedCapId, LazyOptional<?>> caps = new HashMap<>();

    public NetPathwayBlockEntity(BlockPos pos, BlockState blockState)
    {
        super(BDBlockEntities.NET_PATHWAY_BLOCK_ENTITY.get(), pos, blockState);
        addNetChangeTask(this::clearCapCache);
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        DimensionsNet net = this.getNet();
        if (net != null)
        {
            // 遍历注册的能力映射表
            for (Map.Entry<ResourceLocation, Capability<?>> entry : CapabilityHelper.BlockCapabilityMap.entrySet())
            {
                // 检查当前请求的能力是否匹配注册的能力
                if (entry.getValue() == cap)
                {

                    final SidedCapId capId = new SidedCapId(cap, null); //此处无需面信息
                    if (caps.containsKey(capId) && caps.get(capId).isPresent())
                    {
                        return caps.get(capId).cast();
                    }
                    else
                    {
                        // 从类型映射表中获取对应的处理器构造函数
                        USHandler handler = CapabilityHelper.USHandlerMap.get(entry.getKey());
                        if (handler != null)
                        {
                            Object result;
                            if (handler.isContextual())
                                result = handler.apply(net.getUnifiedStorage(), new CapCtx(level, getBlockPos(), this));
                            else
                                result = handler.apply(net.getUnifiedStorage(), null);

                            if (result != null)
                            {
                                LazyOptional<?> opt = LazyOptional.of(() -> result);
                                // 如果opt存在，则放入缓存
                                caps.put(capId, opt);
                                // opt被无效化时，主动移除引用（此处同时判断值，确保移除的是同一个引用下的内容，至少是完全一致的内容）
                                opt.addListener(lo -> caps.remove(capId, lo));
                                return opt.cast();
                            }
                        }
                        return LazyOptional.empty(); // 无对应handler的回调
                    }
                }
            }
        }

        // 未找到匹配能力则调用父类实现
        return com.wintercogs.beyonddimensions.forgecompat.common.util.LazyOptional.empty();
    }

    public void invalidateCaps()
    {
        
        clearCapCache();
    }

    private void clearCapCache()
    {
        // 无效化能力并清空map
        var snapshot = new ArrayList<>(caps.values());
        for (var opt : snapshot)
        { // invalidate时也会尝试移除一次，最后以clear保底
            try
            {
                opt.invalidate();
            }
            catch (Throwable ignored)
            {
            }
        }
        caps.clear();
    }

    private IItemHandler getItemHandler()
    {
        LazyOptional<IItemHandler> opt = getCapability(ForgeCapabilities.ITEM_HANDLER, null);
        return opt.resolve().orElse(null);
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        IItemHandler handler = getItemHandler();
        if (handler == null)
        {
            return new int[0];
        }
        int[] slots = new int[handler.getSlots()];
        for (int i = 0; i < slots.length; i++)
        {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        IItemHandler handler = getItemHandler();
        return handler != null && handler.isItemValid(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        IItemHandler handler = getItemHandler();
        return handler != null && !handler.getStackInSlot(index).isEmpty();
    }

    @Override
    public int getContainerSize()
    {
        IItemHandler handler = getItemHandler();
        return handler != null ? handler.getSlots() : 0;
    }

    @Override
    public boolean isEmpty()
    {
        IItemHandler handler = getItemHandler();
        if (handler == null)
        {
            return true;
        }
        for (int i = 0; i < handler.getSlots(); i++)
        {
            if (!handler.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index)
    {
        IItemHandler handler = getItemHandler();
        return handler != null ? handler.getStackInSlot(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        IItemHandler handler = getItemHandler();
        return handler != null ? handler.extractItem(index, count, false) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        IItemHandler handler = getItemHandler();
        return handler != null ? handler.extractItem(index, 64, false) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        IItemHandler handler = getItemHandler();
        if (handler instanceof com.wintercogs.beyonddimensions.forgecompat.items.IItemHandlerModifiable modifiable)
        {
            modifiable.setStackInSlot(index, stack);
        }
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
    }
}
