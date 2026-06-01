package net.mateo.robomod.item;

import net.mateo.robomod.util.CyborgPartType;

public class CyborgLegPartItem extends CyborgPartItem {

    String right;
    String left;

    public CyborgLegPartItem(String right, String left, int energyCapacity, double health, Properties properties) {
        super("", energyCapacity, health, properties);
        this.right = right;
        this.left = left;
    }

    @Override
    public String getPartName(CyborgPartType partType) {
        if (partType.equals(CyborgPartType.LEFT_LEG)) return left;
        if (partType.equals(CyborgPartType.RIGHT_LEG)) return right;
        return null;
    }
}
