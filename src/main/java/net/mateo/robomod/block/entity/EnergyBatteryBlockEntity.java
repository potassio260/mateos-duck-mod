package net.mateo.robomod.block.entity;

import net.mateo.robomod.block.EnergyBatteryBlock;
import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class EnergyBatteryBlockEntity extends EnergyBlockEntity {

    public EnergyBatteryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATTERY_BLOCK.get(), pos, state);
    }

    @Override
    boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType) {
        return true;
    }

    @Override
    boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType) {
        return true;
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        // getCachedState() → getBlockState() in Forge 1.20.1
        Direction facing = getBlockState().getValue(EnergyBatteryBlock.FACING);
        directionMap.put(facing, BlockEnergyStorage.TypeIO.OUTPUT);
        directionMap.put(facing.getOpposite(), BlockEnergyStorage.TypeIO.INPUT);
    }

    @Override
    public int getCapacity() {
        return 256_000;
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.BATTERY;
    }

    @Override
    public int getTransferRate() {
        return 256;
    }
}
