package net.mateo.robomod.block.entity;

import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ChargingPadBlockEntity extends EnergyBlockEntity {

    public ChargingPadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHARGING_PAD.get(), pos, state);
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.RECEIVER;
    }

    @Override
    boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType) {
        return true;
    }

    @Override
    boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType) {
        // Only allows energy out towards cyborg players
        return target.type() == IEnergyStorage.Type.CYBORG;
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP) {
                directionMap.put(direction, BlockEnergyStorage.TypeIO.INPUT);
            }
        }
    }

    @Override
    public int getTransferRate() {
        return 32;
    }

    @Override
    public int getCapacity() {
        return 64_000;
    }
}
