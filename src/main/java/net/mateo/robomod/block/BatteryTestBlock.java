package net.mateo.robomod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.block.entity.BatteryTestBlockEntity;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.block.entity.EnergyBlockEntity;

public class BatteryTestBlock extends BaseEntityBlock {

    public BatteryTestBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryTestBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Server-side only tick — matches Fabric original
        return level.isClientSide
                ? null
                : createTickerHelper(type, ModBlockEntities.BATTERY_TEST.get(), EnergyBlockEntity::BatteryTick);
    }
}
