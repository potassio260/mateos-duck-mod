package net.mateo.robomod.item;

import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarCellModule extends CyborgModuleItem {

    public SolarCellModule(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ServerLevel level, Player player, PlayerExtension extension, ItemStack stack) {
        // Replaces getTimeOfDay() → getDayTime()
        if (level.getDayTime() % 24000 < 13000) {
            // Replaces getLightLevel(LightType.SKY, ...) → getBrightness(LightLayer.SKY, ...)
            if (level.getBrightness(LightLayer.SKY, player.blockPosition()) == 15) {
                extension.setEnergyStored(Math.min(
                        extension.getEnergyStored() + (level.isRaining() ? 1 : 2),
                        extension.getCapacity()));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Charges your battery in sunlight."));
    }
}
