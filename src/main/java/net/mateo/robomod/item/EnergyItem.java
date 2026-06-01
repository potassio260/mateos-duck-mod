package net.mateo.robomod.item;

import net.mateo.robomod.util.transfer.EnergyStorage;
import net.minecraft.world.item.Item;

public abstract class EnergyItem extends Item {

    public final EnergyStorage storageTEST = new EnergyStorage() {
        @Override
        public Type type() {
            return Type.BATTERY;
        }

        @Override
        public int transferRate() {
            return 0;
        }

        @Override
        public boolean canInsert(EnergyStorage target, Type sourceType) {
            return true;
        }

        @Override
        public boolean canExtract(EnergyStorage source, Type sourceType) {
            return true;
        }

        @Override
        public int capacity() {
            return getCapacity();
        }
    };

    abstract int getCapacity();

    public EnergyItem(Properties properties) {
        super(properties);
    }
}
