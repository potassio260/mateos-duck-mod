package net.mateo.robomod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.block.entity.EnergyWireBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnergyWireBlock extends BaseEntityBlock {

    // Fabric: TagKey.of(RegistryKeys.BLOCK, ...)  →  Forge: TagKey with ForgeRegistries or BlockTags.create
    public static final TagKey<Block> WIRE_CONNECTABLE =
            BlockTags.create(new ResourceLocation(RoboMod.MOD_ID, "wire_connectable"));

    // Fabric: Properties.UP / DOWN / etc.  →  Forge: BlockStateProperties.UP / DOWN / etc.
    public static final BooleanProperty UP    = BlockStateProperties.UP;
    public static final BooleanProperty DOWN  = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST  = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST  = BlockStateProperties.WEST;

    public static final Map<Direction, BooleanProperty> WIRE_DIRECTIONS = Map.of(
            Direction.UP,    UP,
            Direction.DOWN,  DOWN,
            Direction.NORTH, NORTH,
            Direction.EAST,  EAST,
            Direction.SOUTH, SOUTH,
            Direction.WEST,  WEST
    );

    protected static final VoxelShape SHAPE         = box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);
    protected static final VoxelShape SHAPE_Z       = box(4.0, 4.0, 12.0, 12.0, 12.0, 16.0);
    protected static final VoxelShape SHAPE_MINUS_Z = box(4.0, 4.0, 0.0, 12.0, 12.0, 4.0);
    protected static final VoxelShape SHAPE_X       = box(12.0, 4.0, 4.0, 16.0, 12.0, 12.0);
    protected static final VoxelShape SHAPE_MINUS_X = box(0.0, 4.0, 4.0, 4.0, 12.0, 12.0);
    protected static final VoxelShape SHAPE_Y       = box(4.0, 12.0, 4.0, 12.0, 16.0, 12.0);
    protected static final VoxelShape SHAPE_MINUS_Y = box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0);

    public EnergyWireBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, false).setValue(DOWN, false)
                .setValue(NORTH, false).setValue(EAST, false)
                .setValue(SOUTH, false).setValue(WEST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        List<VoxelShape> shapes = getSideOutline(state);
        if (shapes.isEmpty()) return SHAPE;
        return Shapes.or(SHAPE, shapes.toArray(new VoxelShape[0]));
    }

    private List<VoxelShape> getSideOutline(BlockState state) {
        List<VoxelShape> shapes = new ArrayList<>();
        for (var entry : WIRE_DIRECTIONS.entrySet()) {
            if (state.getValue(entry.getValue())) {
                shapes.add(switch (entry.getKey()) {
                    case UP    -> SHAPE_Y;
                    case DOWN  -> SHAPE_MINUS_Y;
                    case NORTH -> SHAPE_MINUS_Z;
                    case EAST  -> SHAPE_X;
                    case SOUTH -> SHAPE_Z;
                    case WEST  -> SHAPE_MINUS_X;
                });
            }
        }
        return shapes;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyWireBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide
                ? null
                : createTickerHelper(type, ModBlockEntities.ENERGY_WIRE.get(), EnergyWireBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState wireState = defaultBlockState();
        BlockPos wirePos = ctx.getClickedPos();

        for (Direction direction : Direction.values()) {
            BlockPos directionPos = wirePos.relative(direction);
            BlockState directionState = ctx.getLevel().getBlockState(directionPos);
            BooleanProperty property = WIRE_DIRECTIONS.get(direction);

            if (directionState.getBlock() instanceof WireConnectable wc) {
                wireState = wireState.setValue(property, wc.canConnect(directionState, directionPos, wireState, wirePos, direction));
            } else {
                wireState = wireState.setValue(property, directionState.is(WIRE_CONNECTABLE));
            }
        }
        return wireState;
    }

    // Fabric: getStateForNeighborUpdate  →  Forge: updateShape
    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState,
                                  LevelAccessor level, BlockPos wirePos, BlockPos neighborPos) {
        BlockState wireState = defaultBlockState();

        for (Direction direction : Direction.values()) {
            BlockPos directionPos = wirePos.relative(direction);
            BlockState directionState = level.getBlockState(directionPos);
            BooleanProperty property = WIRE_DIRECTIONS.get(direction);

            if (directionState.getBlock() instanceof WireConnectable wc) {
                wireState = wireState.setValue(property, wc.canConnect(directionState, directionPos, wireState, wirePos, direction));
            } else {
                wireState = wireState.setValue(property, directionState.is(WIRE_CONNECTABLE));
            }
        }
        return wireState;
    }
}
