package net.mateo.robomod.item;

import net.mateo.robomod.util.CyborgPartType;

public class CyborgHeadPartItem extends CyborgPartItem {

    public CyborgHeadPartItem(String partName, int energyCapacity, double health, Properties properties) {
        super(partName, energyCapacity, health, properties);
    }

    @Override
    public String getPartName(CyborgPartType partType) {
        if (partType.equals(CyborgPartType.HEAD)) return partName;
        return null;
    }
}
