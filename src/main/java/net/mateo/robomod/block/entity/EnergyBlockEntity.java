package net.mateo.robomod.block.entity;

import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;          // ← keep only YOUR interface
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
// ← REMOVED: import net.minecraftforge.energy.IEnergyStorage;  (was causing ambiguity)
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class EnergyBlockEntity extends BlockEntity {

    // -----------------------------------------------------------------------
    // Internal energy storage (drives wire/battery routing logic)
    // -----------------------------------------------------------------------
    public final BlockEnergyStorage energyStorage = new BlockEnergyStorage() {

        @Override
        public void getDirectionIO(Map<Direction, TypeIO> direction) {
            getDirectionsIO(direction);
        }

        @Override
        public boolean canInsert(EnergyStorage source, IEnergyStorage.Type sourceType) {
            return canInsertEnergy(source, sourceType);
        }

        @Override
        public boolean canExtract(EnergyStorage target, IEnergyStorage.Type sourceType) {
            return canExtractEnergy(target, sourceType);
        }

        @Override
        public IEnergyStorage.Type type() {
            return typeMachine();
        }

        @Override
        public int transferRate() {
            return getTransferRate();
        }

        @Override
        public int capacity() {
            return getCapacity();
        }
    };

    // -----------------------------------------------------------------------
    // Forge Energy capability — thin adapter so other mods (pipes, etc.) work
    // Uses fully-qualified Forge type to avoid any ambiguity with our IEnergyStorage
    // -----------------------------------------------------------------------
    private final net.minecraftforge.energy.IEnergyStorage forgeEnergyAdapter =
            new net.minecraftforge.energy.IEnergyStorage() {

                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    if (!canReceive()) return 0;
                    int received = Math.min(getCapacity() - energyStorage.storedEnergy,
                            Math.min(maxReceive, getTransferRate()));
                    if (!simulate && received > 0) {
                        energyStorage.storedEnergy += received;
                        setChanged();
                    }
                    return received;
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    if (!canExtract()) return 0;
                    int extracted = Math.min(energyStorage.storedEnergy,
                            Math.min(maxExtract, getTransferRate()));
                    if (!simulate && extracted > 0) {
                        energyStorage.storedEnergy -= extracted;
                        setChanged();
                    }
                    return extracted;
                }

                @Override
                public int getEnergyStored() {
                    return energyStorage.storedEnergy;
                }

                @Override
                public int getMaxEnergyStored() {
                    return getCapacity();
                }

                @Override
                public boolean canExtract() {
                    IEnergyStorage.Type t = typeMachine();
                    return t == IEnergyStorage.Type.GENERATOR
                            || t == IEnergyStorage.Type.BATTERY;
                }

                @Override
                public boolean canReceive() {
                    IEnergyStorage.Type t = typeMachine();
                    return t == IEnergyStorage.Type.RECEIVER
                            || t == IEnergyStorage.Type.BATTERY;
                }
            };

    // LazyOptional now uses the fully-qualified Forge type
    private LazyOptional<net.minecraftforge.energy.IEnergyStorage> lazyEnergyHandler =
            LazyOptional.empty();

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
    public EnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // -----------------------------------------------------------------------
    // Capability
    // -----------------------------------------------------------------------
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap,
                                                      @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyEnergyHandler = LazyOptional.of(() -> forgeEnergyAdapter);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyHandler.invalidate();
    }

    // -----------------------------------------------------------------------
    // Abstract API — implemented by every specific block entity
    // -----------------------------------------------------------------------
    public abstract IEnergyStorage.Type typeMachine();
    public abstract int getTransferRate();
    public abstract int getCapacity();
    abstract boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType);
    abstract boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType);
    public abstract void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> direction);

    // -----------------------------------------------------------------------
    // Energy helpers
    // -----------------------------------------------------------------------
    public boolean isFull() {
        return getEnergyStored() >= getCapacity();
    }

    public void setEnergyStored(int energy) {
        energyStorage.storedEnergy = Math.max(0, Math.min(energy, getCapacity()));
        setChanged();
    }

    public int getEnergyStored() {
        return energyStorage.getEnergy();
    }

    public boolean canIO(BlockEnergyStorage storage, Direction direction, boolean input) {
        Map<Direction, BlockEnergyStorage.TypeIO> ioMap = storage.getDirectionIOMap();
        BlockEnergyStorage.TypeIO type = ioMap.get(direction);
        if (type == null) return false;
        if (type == BlockEnergyStorage.TypeIO.IO) return true;
        if (input  && type == BlockEnergyStorage.TypeIO.INPUT)  return true;
        if (!input && type == BlockEnergyStorage.TypeIO.OUTPUT) return true;
        return false;
    }

    // -----------------------------------------------------------------------
    // BatteryTick — called from Block tickers
    // -----------------------------------------------------------------------
    public static void BatteryTick(Level level, BlockPos pos, BlockState state,
                                   EnergyBlockEntity blockEntity) {
        IEnergyStorage.Type machineType = blockEntity.typeMachine();
        if (machineType != IEnergyStorage.Type.BATTERY
                && machineType != IEnergyStorage.Type.GENERATOR) return;

        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
        Collections.shuffle(directions);

        for (Direction direction : directions) {
            BlockEnergyStorage neighbor = BlockEnergyStorage.findAt(
                    level, pos.relative(direction), direction.getOpposite());

            if (neighbor != null
                    && blockEntity.canIO(blockEntity.energyStorage, direction, false)
                    && blockEntity.canIO(neighbor, direction.getOpposite(), true)) {

                EnergyStorage.transfer(blockEntity.energyStorage, neighbor,
                        blockEntity.getTransferRate(), blockEntity.typeMachine());
                blockEntity.setChanged();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Network sync
    // -----------------------------------------------------------------------
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void updateListeners() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    // -----------------------------------------------------------------------
    // NBT
    // -----------------------------------------------------------------------
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("EnergyStored", energyStorage.getEnergy());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.storedEnergy = tag.getInt("EnergyStored");
    }
}