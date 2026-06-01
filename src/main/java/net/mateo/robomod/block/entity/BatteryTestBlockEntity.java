package net.mateo.robomod.block.entity;

import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class BatteryTestBlockEntity extends EnergyBlockEntity {

    public BatteryTestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATTERY_TEST.get(), pos, state);
        // Pre-fill to max — energyStorage.storedEnergy is set directly to avoid
        // the cap check in setEnergyStored() before the block entity fully loads.
        energyStorage.storedEnergy = Integer.MAX_VALUE;
    }

    @Override
    boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType) {
        return false;
    }

    @Override
    boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType) {
        return true;
    }

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.BATTERY;
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        for (Direction direction : Direction.values()) {
            directionMap.put(direction, BlockEnergyStorage.TypeIO.OUTPUT);
        }
    }

    @Override
    public int getTransferRate() {
        return 900;
    }
}
