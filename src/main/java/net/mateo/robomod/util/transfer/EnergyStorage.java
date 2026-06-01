package net.mateo.robomod.util.transfer;

/**
 * Base energy storage — holds storedEnergy and provides the static transfer() helper.
 * All block-level energy logic uses this class; Forge Energy is an adapter on top.
 */
public abstract class EnergyStorage implements IEnergyStorage {

    public int storedEnergy;

    @Override
    public int getEnergy() {
        return storedEnergy;
    }

    public void setEnergy(int energy) {
        this.storedEnergy = Math.max(0, Math.min(energy, capacity()));
    }

    @Override
    public boolean isFull() {
        return storedEnergy >= capacity();
    }

    /**
     * Transfers energy from source → target up to maxTransfer,
     * respecting both storages' canInsert / canExtract guards.
     */
    public static int transfer(EnergyStorage source, EnergyStorage target,
                               int maxTransfer, IEnergyStorage.Type sourceType) {
        if (!target.canInsert(source, sourceType)) return 0;
        if (!source.canExtract(target, sourceType)) return 0;
        int amount = Math.min(maxTransfer,
                Math.min(source.storedEnergy, target.capacity() - target.storedEnergy));
        if (amount > 0) {
            source.storedEnergy -= amount;
            target.storedEnergy += amount;
        }
        return amount;
    }
}
