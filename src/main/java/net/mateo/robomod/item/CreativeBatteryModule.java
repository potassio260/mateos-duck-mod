package net.mateo.robomod.item;

import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeBatteryModule extends CyborgModuleItem {

    public CreativeBatteryModule(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ServerLevel level, Player player, PlayerExtension extension, ItemStack stack) {
        extension.setEnergyStored(extension.getCapacity());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Disables any energy consumption."));
        tooltip.add(Component.literal("Unobtainable item."));
    }
}
