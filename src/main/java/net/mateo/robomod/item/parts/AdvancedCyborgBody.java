package net.mateo.robomod.item.parts;

import net.mateo.robomod.item.CyborgBodyPartItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdvancedCyborgBody extends CyborgBodyPartItem {

    public AdvancedCyborgBody(String partName, int energyCapacity, double health, Item.Properties properties) {
        super(partName, energyCapacity, health, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("Full Set Bonus: +§61 §7Module slot"));
    }
}