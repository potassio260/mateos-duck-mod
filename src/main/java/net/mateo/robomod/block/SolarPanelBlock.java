package net.mateo.robomod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import org.jetbrains.annotations.Nullable;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.block.entity.SolarPanelBlockEntity;

import java.util.List;

public class SolarPanelBlock extends BaseEntityBlock implements WireConnectable {

    protected static final VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public final int energyCapacity;
    public final int generationRate;

    public SolarPanelBlock(int energyCapacity, int generationRate, Properties properties) {
        super(properties);
        this.energyCapacity = energyCapacity;
        this.generationRate = generationRate;
    }

    @Nullable
    @Override
    public SolarPanelBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarPanelBlockEntity(this.energyCapacity, this.generationRate, pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide
                ? null
                : createTickerHelper(type, ModBlockEntities.SOLAR_PANEL.get(), SolarPanelBlockEntity::tick);
    }

    @Override
    public boolean canConnect(BlockState state, BlockPos pos, BlockState wireState, BlockPos wirePos, Direction direction) {
        return !direction.equals(Direction.DOWN);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§b" + energyCapacity + " §7Energy Capacity")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));
        tooltip.add(Component.literal("§b" + generationRate + "/t §7Generation Rate")
                .withStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GRAY)));

        // Replace with NBT read when porting ModItems data components
        // int stored = stack.getOrCreateTag().getInt("StoredEnergy");
        // if (stored > 0) tooltip.add(...);
    }
}
