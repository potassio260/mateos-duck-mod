package net.mateo.robomod.client.util;

public class EnergySynchronization {

    private static int storedEnergy;
    private static int capacity;

    public static void updateEnergyInGui(int storedEnergy1, int capacity1) {
        storedEnergy = storedEnergy1;
        capacity     = capacity1;
    }

    public static int[] getEnergy() {
        return new int[]{ storedEnergy, capacity };
    }
}
