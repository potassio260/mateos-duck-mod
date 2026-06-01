package net.mateo.robomod.block;

import net.mateo.robomod.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.item.BaseCyborgModuleItem;
import net.mateo.robomod.item.ModItems;
import net.mateo.robomod.item.CyborgPartItem;
import net.mateo.robomod.util.CyborgPartType;

import java.util.List;

public class AssemblerBlock extends BaseEntityBlock {

    // Fabric: HorizontalFacingBlock.FACING  →  Forge: BlockStateProperties.HORIZONTAL_FACING
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public AssemblerBlock(Properties properties) {
        super(properties);
        // Register default state with FACING
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // -------------------------------------------------------------------------
    // Fabric: getCodec()  — not present in Forge 1.20.1 BaseEntityBlock, skip it
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AssemblerBlockEntity(pos, state);
    }

    // Fabric: getTicker  →  Forge: getTicker (same signature, different imports)
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Only tick on client (animation), mirror of Fabric original
        return level.isClientSide
                ? createTickerHelper(type, ModBlockEntities.ASSEMBLER.get(), AssemblerBlockEntity::animateError)
                : null;
    }

    // Fabric: getRenderType  →  Forge: getRenderShape
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // Fabric: onUse  →  Forge: use
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AssemblerBlockEntity assembler) {
            ItemStack stack = player.getItemInHand(hand);

            if (stack.getItem() instanceof BaseCyborgModuleItem moduleItem) {
                if (assembler.hasEmptyModuleSlot() && !assembler.containsModule(moduleItem) && assembler.addModule(stack)) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                    assembler.setChanged();
                    return InteractionResult.SUCCESS;
                }
            }

            if (stack.getItem() instanceof CyborgPartItem partItem) {
                for (CyborgPartType partType : CyborgPartType.values()) {
                    if (partItem.getPartName(partType) != null && assembler.getPartStack(partType).isEmpty()) {
                        assembler.setPartStack(partType, stack);
                        player.setItemInHand(hand, ItemStack.EMPTY);
                        assembler.setChanged();
                        return InteractionResult.SUCCESS;
                    }
                }
            }

            // Fabric: state.createScreenHandlerFactory  →  Forge: getMenuProvider
            var menuProvider = state.getMenuProvider(level, pos);
            if (menuProvider != null) {
                player.openMenu(menuProvider);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

    // Fabric: onStateReplaced  →  Forge: onRemove
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AssemblerBlockEntity assembler) {
                // Fabric: ItemScatterer.spawn(world, pos, inventory)
                // Forge: Containers.dropContents(level, pos, container)
                net.minecraft.world.Containers.dropContents(level, pos, assembler);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // Fabric: getPlacementState  →  Forge: getStateForPlacement
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
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
        builder.add(FACING);
    }

    // Fabric: appendTooltip  →  Forge: appendHoverText
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b128000 §7Energy Capacity")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));

        // Fabric: stack.get(DataComponentType)  →  Forge 1.20.1 uses NBT/Capabilities, not data components
        // Replace with your NBT-based energy storage read when you port ModItems
        // Example placeholder:
        // int stored = stack.getOrCreateTag().getInt("StoredEnergy");
        // if (stored > 0) tooltip.add(...);
    }
}
