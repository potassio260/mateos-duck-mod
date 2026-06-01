package net.mateo.robomod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.entity.ChargingPadBlockEntity;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.block.entity.EnergyBlockEntity;
import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.item.ModItems;
import net.mateo.robomod.util.transfer.EnergyStorage;

import java.util.List;

public class ChargingPadBlock extends BaseEntityBlock implements WireConnectable {

    // Fabric: Block.createCuboidShape  →  Forge: Block.box
    protected static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0);

    public ChargingPadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // Fabric: onSteppedOn  →  Forge: stepOn
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide) {
            ChargingPadBlockEntity chargingPadBlock = level.getBlockEntity(pos, ModBlockEntities.CHARGING_PAD.get()).orElse(null);
            if (chargingPadBlock == null) {
                RoboMod.LOGGER.warn("Ignoring receive energy attempt for Charging Pad without matching block entity at {}", pos);
            } else {
                if (0 < chargingPadBlock.getEnergyStored()
                        && entity instanceof ServerPlayer player
                        && player instanceof PlayerExtension cyborg
                        && cyborg.isCyborg()
                        && cyborg.getEnergyStored() != cyborg.getCapacity()) {
                    if (EnergyStorage.transfer(chargingPadBlock.energyStorage, cyborg.getEnergyStorage(),
                            chargingPadBlock.getTransferRate(), chargingPadBlock.typeMachine()) > 0) {
                        chargingPadBlock.setChanged();
                    }
                }
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChargingPadBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide
                // NOTE: original Fabric code referenced BATTERY_TEST here — kept as-is intentionally
                ? createTickerHelper(type, ModBlockEntities.BATTERY_TEST.get(), EnergyBlockEntity::BatteryTick)
                : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b64000 §7Energy Capacity")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));

        // Replace with NBT read when porting ModItems data components
        // int stored = stack.getOrCreateTag().getInt("StoredEnergy");
        // if (stored > 0) tooltip.add(...);
    }

    @Override
    public boolean canConnect(BlockState state, BlockPos pos, BlockState wireState, BlockPos wirePos, Direction direction) {
        return !direction.equals(Direction.DOWN);
    }
}
