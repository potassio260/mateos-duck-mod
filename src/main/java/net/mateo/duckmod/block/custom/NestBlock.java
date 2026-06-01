package net.mateo.duckmod.block.custom;

import net.mateo.duckmod.entity.ModEntities;
import net.mateo.duckmod.item.ModItems;
import net.mateo.duckmod.entity.custom.NestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NestBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);
    public static final IntegerProperty EGGS = IntegerProperty.create("eggs", 0, 4);
    public static final IntegerProperty HATCH = IntegerProperty.create("hatch", 0, 2);

    public NestBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(EGGS, 0)
                .setValue(HATCH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EGGS, HATCH);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack itemInHand = player.getItemInHand(hand);
        int currentEggs = state.getValue(EGGS);

        // Check if player is holding a duck egg and nest isn't full
        if (itemInHand.is(ModItems.DUCK_EGG.get()) && currentEggs < 4) {
            if (!level.isClientSide) {
                // Add an egg to the nest
                level.setBlock(pos, state.setValue(EGGS, currentEggs + 1), 3);

                // Play sound
                level.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1.0F, 1.0F);

                // Remove egg from player's hand if not in creative
                if (!player.getAbilities().instabuild) {
                    itemInHand.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // Check if player is trying to remove eggs (empty hand or sneaking)
        if (itemInHand.isEmpty() && currentEggs > 0) {
            if (!level.isClientSide) {
                // Remove an egg from the nest
                level.setBlock(pos, state.setValue(EGGS, currentEggs - 1), 3);

                // Give egg to player
                if (!player.getInventory().add(new ItemStack(ModItems.DUCK_EGG.get()))) {
                    player.drop(new ItemStack(ModItems.DUCK_EGG.get()), false);
                }

                // Play sound
                level.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof NestBlockEntity nestBE) {
            // Add heat when entity steps on nest
            nestBE.addHeat(10);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        return belowState.isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // Check if the block is actually being removed (not just state change)
        if (!state.is(newState.getBlock())) {
            int eggCount = state.getValue(EGGS);

            if (!level.isClientSide) {
                if (eggCount > 0) {
                    // Play egg breaking sound when nest with eggs is broken
                    level.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK,
                            SoundSource.BLOCKS, 0.7F, 0.9F + level.getRandom().nextFloat() * 0.2F);
                    // Don't drop eggs - they break with the nest
                } else {
                    // Play normal block break sound
                    // The default sound will play automatically from Block.playerDestroy()
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NestBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof NestBlockEntity nestBE) {
                nestBE.tick();
            }
        };
    }
}