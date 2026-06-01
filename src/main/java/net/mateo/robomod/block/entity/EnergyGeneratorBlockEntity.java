package net.mateo.robomod.block.entity;

import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class EnergyGeneratorBlockEntity extends EnergyBlockEntity {

    private final int generationRate = 1302;

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_GENERATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
                            EnergyGeneratorBlockEntity blockEntity) {
        if (!blockEntity.isFull()) {
            blockEntity.setEnergyStored(
                    Math.min(blockEntity.getCapacity(),
                            blockEntity.getEnergyStored() + blockEntity.generationRate));
        }
        EnergyBlockEntity.BatteryTick(level, pos, state, blockEntity);
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.GENERATOR;
    }

    @Override
    public int getTransferRate() {
        return 900;
    }

    @Override
    public int getCapacity() {
        return 4_096_000;
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        for (Direction direction : Direction.values()) {
            directionMap.put(direction, BlockEnergyStorage.TypeIO.OUTPUT);
        }
    }

    @Override
    boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType) {
        return false;
    }

    @Override
    boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType) {
        return true;
    }
}
