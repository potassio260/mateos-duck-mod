package net.mateo.robomod.util.transfer;

/**
 * Internal energy interface — keeps the mod's own type system (BATTERY, GENERATOR,
 * RECEIVER, WIRE, CYBORG) which drives wire routing and transfer logic.
 * Forge's ForgeCapabilities.ENERGY is exposed separately via getCapability().
 */
public interface IEnergyStorage {

    enum Type {
        BATTERY,
        GENERATOR,
        RECEIVER,
        WIRE,
        CYBORG
    }

    Type type();

    int transferRate();

    int capacity();

    boolean isFull();

    int getEnergy();

    boolean canInsert(EnergyStorage source, Type sourceType);

    boolean canExtract(EnergyStorage target, Type sourceType);
}
