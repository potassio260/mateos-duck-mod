package net.mateo.robomod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.block.entity.EnergyBatteryBlockEntity;
import net.mateo.robomod.block.entity.EnergyBlockEntity;

import java.util.List;

public class EnergyBatteryBlock extends BaseEntityBlock implements WireConnectable {

    // Fabric: Properties.FACING  →  Forge: BlockStateProperties.FACING
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    // Fabric: IntProperty.of  →  Forge: IntegerProperty.create
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 4);

    protected static final VoxelShape Y_SHAPE = box(3.0F, 0.0F, 3.0F, 13.0F, 16.0F, 13.0F);
    protected static final VoxelShape Z_SHAPE = box(3.0F, 3.0F, 0.0F, 13.0F, 13.0F, 16.0F);
    protected static final VoxelShape X_SHAPE = box(0.0F, 3.0F, 3.0F, 16.0F, 13.0F, 13.0F);

    public EnergyBatteryBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LEVEL, 0)
                .setValue(FACING, Direction.UP));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING).getAxis()) {
            case X -> X_SHAPE;
            case Z -> Z_SHAPE;
            case Y -> Y_SHAPE;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // Fabric: ctx.getSide()  →  Forge: ctx.getClickedFace()
        return this.defaultBlockState().setValue(FACING, ctx.getClickedFace());
    }

    @Override
    public boolean canConnect(BlockState state, BlockPos pos, BlockState wireState, BlockPos wirePos, Direction direction) {
        return direction.equals(state.getValue(FACING)) || direction.equals(state.getValue(FACING).getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyBatteryBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.BATTERY_BLOCK.get(),
                (level1, pos, state1, blockEntity) -> {
                    EnergyBlockEntity.BatteryTick(level1, pos, state1, blockEntity);
                    if (level1.getGameTime() % 20 == 0) {
                        float p = (float) blockEntity.getEnergyStored() / (float) blockEntity.getCapacity();
                        int lvl = 0;
                        if (p >= 0.25f) lvl = 1;
                        if (p >= 0.50f) lvl = 2;
                        if (p >= 0.75f) lvl = 3;
                        if (p >= 0.90f) lvl = 4;

                        BlockState newState = state1.setValue(LEVEL, lvl);
                        if (!newState.equals(state1)) level1.setBlock(pos, newState, 3);
                    }
                });
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    // Fabric: canPathfindThrough  →  Forge: isPathfindable
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b256000 §7Energy Capacity")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));

        // Replace with NBT read when porting ModItems data components
        // int stored = stack.getOrCreateTag().getInt("StoredEnergy");
        // if (stored > 0) tooltip.add(...);
    }
}
