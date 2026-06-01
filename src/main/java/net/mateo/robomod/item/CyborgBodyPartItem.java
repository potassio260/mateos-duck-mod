package net.mateo.robomod.item;

import net.mateo.robomod.util.CyborgPartType;

public class CyborgBodyPartItem extends CyborgPartItem {

    public CyborgBodyPartItem(String partName, int energyCapacity, double health, Properties properties) {
        super(partName, energyCapacity, health, properties);
    }

    @Override
    public String getPartName(CyborgPartType partType) {
        if (partType.equals(CyborgPartType.BODY)) return partName;
        return null;
    }
}
