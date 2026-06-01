package net.mateo.robomod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.block.entity.FurnaceGeneratorBlockEntity;

import java.util.List;

public class FurnaceGeneratorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public FurnaceGeneratorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    // Fabric: hasComparatorOutput  →  Forge: hasAnalogOutputSignal
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    // Fabric: getComparatorOutput  →  Forge: getAnalogOutputSignal
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            var menuProvider = state.getMenuProvider(level, pos);
            if (menuProvider != null) {
                player.openMenu(menuProvider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    // Fabric: onStateReplaced  →  Forge: onRemove
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof FurnaceGeneratorBlockEntity furnaceGenerator) {
                net.minecraft.world.Containers.dropContents(level, pos, furnaceGenerator);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide
                ? null
                : createTickerHelper(type, ModBlockEntities.SOLID_FUEL_GENERATOR.get(), FurnaceGeneratorBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FurnaceGeneratorBlockEntity(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b64000 §7Energy Capacity")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));
        tooltip.add(Component.literal("§b16/t §7Generation Rate")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));

        // Replace with NBT read when porting ModItems data components
        // int stored = stack.getOrCreateTag().getInt("StoredEnergy");
        // if (stored > 0) tooltip.add(...);
    }

    // Fabric: randomDisplayTick  →  Forge: animateTick
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;

            if (random.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double range = random.nextDouble() * 0.6 - 0.3;
            double xi = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : range;
            double yi = random.nextDouble() * 6.0 / 16.0 + 0.2;
            double zi = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : range;

            level.addParticle(ParticleTypes.SMOKE, x + xi, y + yi, z + zi, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x + xi, y + yi, z + zi, 0.0, 0.0, 0.0);

            if (random.nextFloat() < 0.11F) {
                for (int i = 0; i < random.nextInt(2) + 2; i++) {
                    // Fabric: CampfireBlock.spawnSmokeParticle  →  Forge: CampfireBlock.makeParticles
                    CampfireBlock.makeParticles(level, pos, false, false);
                }
            }
        }
    }
}
