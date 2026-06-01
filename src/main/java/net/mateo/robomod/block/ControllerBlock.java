package net.mateo.robomod.block;

import net.mateo.robomod.network.ModPackets;
import net.mateo.robomod.network.packet.SyncCyborgStatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.block.entity.ControllerBlockEntity;
import net.mateo.robomod.entity.CyborgEntity;
import net.mateo.robomod.entity.ModEntities;
import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.item.BaseCyborgModuleItem;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ControllerBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected static final VoxelShape NORTH_SHAPE = createNorthShape();
    protected static final VoxelShape SOUTH_SHAPE = createSouthShape();
    protected static final VoxelShape EAST_SHAPE  = createEastShape();
    protected static final VoxelShape WEST_SHAPE  = createWestShape();

    // Prevent Forge's double-fire of use() from immediately reversing the cyborg state.
    // Stores the last game tick each player successfully triggered the block.
    private static final Map<UUID, Long> USE_COOLDOWN = new HashMap<>();

    public ControllerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    static VoxelShape createNorthShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.25, 0.8125, 0.875, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.0625, 1, 0.875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.5625, 1, 1.125, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0.3125, 1, 1, 0.5625), BooleanOp.OR);
        return shape;
    }

    static VoxelShape createSouthShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0, 0.25, 0.8125, 0.875, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.6875, 1, 0.875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0.1875, 1, 1.125, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0.4375, 1, 1, 0.6875), BooleanOp.OR);
        return shape;
    }

    static VoxelShape createEastShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.1875, 0.75, 0.875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.625, 0, 0.9375, 0.875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.875, 0, 0.4375, 1.125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.75, 0, 0.6875, 1, 1), BooleanOp.OR);
        return shape;
    }

    static VoxelShape createWestShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.1875, 0.75, 0.875, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0, 0.3125, 0.875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.875, 0, 0.8125, 1.125, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.75, 0, 0.5625, 1, 1), BooleanOp.OR);
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST  -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST  -> WEST_SHAPE;
            default    -> NORTH_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ControllerBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

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

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        // FIX: Forge can fire use() twice on the same right-click (once per hand).
        // After becomeCyborg() clears the assembler, the second call sees
        // isCyborg=true + assembler.isEmpty() and immediately calls becomeFlesh(),
        // sending isCyborg=false to the client before it ever renders.
        // Block any repeat trigger within 10 ticks (half a second) per player.
        if (!level.isClientSide) {
            long now = level.getGameTime();
            Long last = USE_COOLDOWN.get(player.getUUID());
            if (last != null && now - last < 10L) {
                return InteractionResult.PASS;
            }
            USE_COOLDOWN.put(player.getUUID(), now);
        }

        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (Direction direction : directions) {
            BlockPos assemblerPos = pos.offset(direction.getNormal());
            BlockEntity blockEntity = level.getBlockEntity(assemblerPos);

            if (blockEntity instanceof AssemblerBlockEntity assembler && player instanceof PlayerExtension cyborg) {
                if (!cyborg.isCyborg() && assembler.isComplete()) {
                    if (!level.isClientSide) {
                        becomeCyborg(level, pos, player, assemblerPos, assembler);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else if (cyborg.isCyborg() && assembler.isEmpty()) {
                    if (!level.isClientSide) {
                        cyborg.getModules().forEach(stack -> {
                            if (stack.getItem() instanceof BaseCyborgModuleItem moduleItem) {
                                moduleItem.onModuleRemoved(level, player);
                            }
                        });
                        becomeFlesh(level, player, assembler);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

    private void becomeFlesh(Level level, Player player, AssemblerBlockEntity assembler) {
        if (player.getVehicle() instanceof CyborgEntity cyborgEntity) {
            // Discard FIRST so the CyborgMountEvent correctly allows the player to dismount
            cyborgEntity.discard();
            player.stopRiding();
        } else {
            level.getEntitiesOfClass(CyborgEntity.class, player.getBoundingBox().inflate(10.0))
                    .forEach(Entity::discard);
        }

        PlayerExtension cyborg = (PlayerExtension) player;

        if (!assembler.isFull()) {
            EnergyStorage.transfer(cyborg.getEnergyStorage(), assembler.energyStorage,
                    assembler.getCapacity(), IEnergyStorage.Type.CYBORG);
        }
        cyborg.setEnergyStored(0);
        cyborg.setCyborg(false);
        assembler.setItem(0, cyborg.getCyborgHead());
        assembler.setItem(1, cyborg.getCyborgBody());
        assembler.setItem(2, cyborg.getCyborgRightArm());
        assembler.setItem(3, cyborg.getCyborgLeftArm());
        assembler.setItem(4, cyborg.getCyborgRightLeg());
        assembler.setItem(5, cyborg.getCyborgLeftLeg());

        assembler.setModule(1, cyborg.getModule1());
        assembler.setModule(2, cyborg.getModule2());
        assembler.setModule(3, cyborg.getModule3());
        assembler.setModule(4, cyborg.getModule4());

        cyborg.clearAllParts();

        var healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(RoboMod.CYBORG_HEALTH_UUID);
        }

        cyborg.setModule1(ItemStack.EMPTY);
        cyborg.setModule2(ItemStack.EMPTY);
        cyborg.setModule3(ItemStack.EMPTY);
        cyborg.setModule4(ItemStack.EMPTY);
        assembler.setChanged();
        ModPackets.sendToPlayer((ServerPlayer) player, new SyncCyborgStatePacket(cyborg));
    }

    private void becomeCyborg(Level level, BlockPos pos, Player player,
                              BlockPos assemblerPos, AssemblerBlockEntity assembler) {
        PlayerExtension cyborg = (PlayerExtension) player;
        cyborg.setCyborg(true);

        cyborg.setCyborgHead(assembler.getHead());
        cyborg.setCyborgBody(assembler.getBody());
        cyborg.setCyborgRightArm(assembler.getRightArm());
        cyborg.setCyborgLeftArm(assembler.getLeftArm());
        cyborg.setCyborgRightLeg(assembler.getRightLeg());
        cyborg.setCyborgLeftLeg(assembler.getLeftLeg());

        for (int i = 1; i <= 4; i++) {
            ItemStack module = assembler.getModule(i);
            if (!module.isEmpty()) {
                if (module.getItem() instanceof BaseCyborgModuleItem moduleItem) {
                    moduleItem.controllerLogic(this, pos, level, player, module);
                    switch (i) {
                        case 1 -> cyborg.setModule1(module);
                        case 2 -> cyborg.setModule2(module);
                        case 3 -> cyborg.setModule3(module);
                        case 4 -> cyborg.setModule4(module);
                    }
                } else {
                    Block.popResource(level, assemblerPos, module);
                }
            }
        }

        assembler.getItems().clear();
        EnergyStorage.transfer(assembler.energyStorage, cyborg.getEnergyStorage(),
                cyborg.getCapacity(), IEnergyStorage.Type.RECEIVER);
        assembler.setChanged();

        cyborg.setupAttributes(player);

        if (!level.isClientSide) {
            player.teleportTo(
                    assemblerPos.getX() + 0.5,
                    assemblerPos.getY() + 1.0,
                    assemblerPos.getZ() + 0.5
            );
            player.setYRot(assembler.getBlockState().getValue(AssemblerBlock.FACING).toYRot());

            ModPackets.sendToPlayer((ServerPlayer) player, new SyncCyborgStatePacket(cyborg));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Use controller to take control of the cyborg.")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));
        tooltip.add(Component.literal("Place nearby assembler to connect.")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));
    }
}