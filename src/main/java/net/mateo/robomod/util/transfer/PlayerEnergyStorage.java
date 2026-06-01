package net.mateo.robomod.util.transfer;

public class PlayerEnergyStorage extends EnergyStorage {

    private final int robomod$capacity;

    public PlayerEnergyStorage(int capacity) {
        this.robomod$capacity = capacity;
    }

    @Override
    public Type type() {
        return Type.CYBORG;
    }

    @Override
    public int transferRate() {
        return 64;
    }

    @Override
    public int capacity() {
        return robomod$capacity;
    }

    @Override
    public boolean canInsert(EnergyStorage source, Type sourceType) {
        return !isFull();
    }

    @Override
    public boolean canExtract(EnergyStorage target, Type sourceType) {
        return storedEnergy > 0;
    }
}