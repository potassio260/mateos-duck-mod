package net.mateo.robomod.block.entity;

import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class SolarPanelBlockEntity extends EnergyBlockEntity {

    public final int energyCapacity;
    public final int generationRate;

    public SolarPanelBlockEntity(int energyCapacity, int generationRate,
                                  BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state);
        this.energyCapacity  = energyCapacity;
        this.generationRate  = generationRate;
    }

    /** No-arg constructor required by BlockEntityType — values default to 0.
     *  The SolarPanelBlock subclass overrides createBlockEntity() to pass real values. */
    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, state);
        this.energyCapacity = 0;
        this.generationRate = 0;
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP) {
                directionMap.put(direction, BlockEnergyStorage.TypeIO.OUTPUT);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
                            SolarPanelBlockEntity blockEntity) {
        // LightType.SKY → LightLayer.SKY  |  getLightLevel → getBrightness
        if (level.getBrightness(LightLayer.SKY, pos) == 15) {
            int rate = level.isRaining()
                    ? blockEntity.generationRate / 2
                    : blockEntity.generationRate;

            if (blockEntity.getEnergyStored() < blockEntity.getCapacity()) {
                blockEntity.setEnergyStored(
                        Math.min(blockEntity.getCapacity(),
                                blockEntity.getEnergyStored() + rate));
                blockEntity.setChanged(); // markDirty() → setChanged()
            }
        }
        EnergyBlockEntity.BatteryTick(level, pos, state, blockEntity);
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.GENERATOR;
    }

    @Override
    public int getTransferRate() {
        return generationRate * 4;
    }

    @Override
    public int getCapacity() {
        return energyCapacity;
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
