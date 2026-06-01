package net.mateo.robomod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryModule extends CyborgModuleItem {

    int energyCapacity;

    public BatteryModule(int energyCapacity, Properties properties) {
        super(properties);
        this.energyCapacity = energyCapacity;
    }

    public int getEnergyCapacity() {
        return energyCapacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("+§b" + getEnergyCapacity() + " §7Energy Capacity"));
    }
}
