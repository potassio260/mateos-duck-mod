package net.mateo.robomod.item;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.ControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ExtendedHealthModule extends CyborgModuleItem {

    // Stable UUID derived from the modifier's resource location
    private static final UUID HEALTH_MODIFIER_UUID =
            UUID.nameUUIDFromBytes(RoboMod.id("cyborg_health_module").toString().getBytes());

    public ExtendedHealthModule(Properties properties) {
        super(properties);
    }

    @Override
    public void controllerLogic(ControllerBlock controllerBlock, BlockPos pos, Level level, Player player, ItemStack stack) {
        // Replaces addPersistentModifier + EntityAttributeModifier with ADD_VALUE → ADDITION
        player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(
                new AttributeModifier(HEALTH_MODIFIER_UUID, "cyborg_health_module", 20,
                        AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void onModuleRemoved(Level level, Player player) {
        player.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_MODIFIER_UUID);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Grants §a20.0 §7health, this extra health"));
        tooltip.add(Component.literal("takes extra energy to regenerate."));
    }
}
