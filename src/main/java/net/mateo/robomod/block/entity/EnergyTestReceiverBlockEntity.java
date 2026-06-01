package net.mateo.robomod.block.entity;

import net.mateo.robomod.block.EnergyReceiverBlock;
import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class EnergyTestReceiverBlockEntity extends EnergyBlockEntity {

    private boolean isActive = false;

    public EnergyTestReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_RECEIVER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
                            EnergyTestReceiverBlockEntity blockEntity) {
        // DebugUtil.updateEnergyDebug(level, pos, state, blockEntity); // TODO: port DebugUtil
        blockEntity.updateReceiver();
    }

    public int energyConsumption() {
        return 300;
    }

    private void updateReceiver() {
        boolean wasActive = isActive;
        int consumption = energyConsumption();

        if (getEnergyStored() >= consumption) {
            setEnergyStored(getEnergyStored() - consumption);
            isActive = true;
        } else {
            isActive = false;
        }

        if (wasActive != isActive) {
            updateBlockState();
        }
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        for (Direction direction : Direction.values()) {
            directionMap.put(direction, BlockEnergyStorage.TypeIO.INPUT);
        }
    }

    private void updateBlockState() {
        // level / pos field names in Forge 1.20.1 (world → level, pos stays pos)
        if (level != null && !level.isClientSide()) {
            BlockState currentState = level.getBlockState(getBlockPos());
            if (currentState.getBlock() instanceof EnergyReceiverBlock) {
                level.setBlock(getBlockPos(),
                        currentState.setValue(EnergyReceiverBlock.POWERED, isActive),
                        3); // Block.UPDATE_ALL = 3

                level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.RECEIVER;
    }

    @Override
    public int getTransferRate() {
        return 90;
    }

    @Override
    public int getCapacity() {
        return 15_000;
    }

    @Override
    boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType) {
        return true;
    }

    @Override
    boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType) {
        return false;
    }

    // -----------------------------------------------------------------------
    // NBT — super handles EnergyStored; we add IsActive on top
    // -----------------------------------------------------------------------
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("IsActive", isActive);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        isActive = tag.getBoolean("IsActive");
    }
}
