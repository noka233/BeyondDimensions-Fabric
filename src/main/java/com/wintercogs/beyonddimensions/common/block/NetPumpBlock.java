package com.wintercogs.beyonddimensions.common.block;

import com.wintercogs.beyonddimensions.common.block.entity.NetPumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.wintercogs.beyonddimensions.forgecompat.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;

public class NetPumpBlock extends BaseMachineBlock
{
    public NetPumpBlock(BlockBehaviour.Properties properties)
    {
        super(properties.noOcclusion());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new NetPumpBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        super.use(state, level, pos, player, hand, hitResult);
        if (!level.isClientSide() && !player.isShiftKeyDown())
        {
            NetPumpBlockEntity blockEntity = (NetPumpBlockEntity) level.getBlockEntity(pos);
            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved)
    {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
        if (level.getBlockEntity(pos) instanceof NetPumpBlockEntity blockEntity)
        {
            blockEntity.setNeedsCapabilityUpdate();
        }
    }
}
