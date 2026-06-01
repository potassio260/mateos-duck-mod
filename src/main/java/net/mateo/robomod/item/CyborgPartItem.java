package net.mateo.robomod.item;

import net.mateo.robomod.util.CyborgPartType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CyborgPartItem extends EnergyItem {

    String partName;
    int energyCapacity;
    double health;

    public CyborgPartItem(String partName, int energyCapacity, double health, Properties properties) {
        super(properties);
        this.partName = partName;
        this.energyCapacity = energyCapacity;
        this.health = health;
    }

    public String getPartName(CyborgPartType partType) {
        return partName;
    }

    public double getHealth() {
        return health;
    }

    @Override
    public int getCapacity() {
        return energyCapacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("+§b" + getCapacity() + " §7Energy Capacity"));
        tooltip.add(Component.literal("+§a" + getHealth() + " §7Max Health"));
    }
}
