package net.mateo.robomod.item;

import net.mateo.robomod.client.render.module.AnimatableModuleRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;

public abstract class AnimatableCyborgModule extends BaseCyborgModuleItem implements GeoItem {

    @OnlyIn(Dist.CLIENT)
    private AnimatableModuleRenderer renderer;

    public AnimatableCyborgModule(Properties properties) {
        super(properties);
    }

    public static long getOrAssignIdUpdate(ItemStack stack, ServerLevel serverLevel, Player player) {
        // In Forge 1.20.1, GeckoLib stores animatable IDs via ItemStack's capability or NBT,
        // not via Fabric's ComponentMapImpl. Use GeoItem's built-in helper instead:
        return GeoItem.getOrAssignId(stack, serverLevel);
    }

    @OnlyIn(Dist.CLIENT)
    public void setModuleRenderer(AnimatableModuleRenderer renderer) {
        this.renderer = renderer;
    }

    @OnlyIn(Dist.CLIENT)
    public AnimatableModuleRenderer getModuleRenderer() {
        return this.renderer;
    }
}
