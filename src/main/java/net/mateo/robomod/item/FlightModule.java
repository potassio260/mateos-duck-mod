package net.mateo.robomod.item;

import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class FlightModule extends AnimatableCyborgModule {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation FLY  = RawAnimation.begin().thenPlay("fly");
    protected static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");

    public FlightModule(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void tick(ServerLevel serverLevel, Player player, PlayerExtension ex, ItemStack stack) {
        long id = getOrAssignIdUpdate(stack, serverLevel, player);

        if (ex.isCyborg() && player.getAbilities().flying && !player.isSpectator()) {
            triggerAnim(player, id, "flight_module", "fly");
            // FIX: stopTriggeredAnim(Player,long,String,String) does not exist in this GeckoLib build.
            // Triggered animations play once and stop; the "idle" animation is simply not triggered
            // while flying. If your base class exposes stopTriggeredAnim, uncomment these calls.
            // stopTriggeredAnim(player, id, "flight_module", "idle");
        } else {
            // stopTriggeredAnim(player, id, "flight_module", "fly");
            triggerAnim(player, id, "flight_module", "idle");
        }

        if (ex.isCyborg() && !player.isCreative() && !player.isSpectator()) {
            if (ex.getEnergyStored() >= 20) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
                if (player.getAbilities().flying) {
                    ex.setEnergyStored(Math.max(ex.getEnergyStored() - 20, 0));
                }
            } else {
                if (player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
            }
        }
    }

    @Override
    public void onModuleRemoved(Level level, Player player) {
        if (!level.isClientSide() && !player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Grants §fcreative flight §7but"));
        tooltip.add(Component.literal("consumes a lot of energy."));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "flight_module", 5, state -> PlayState.STOP)
                .triggerableAnim("fly", FLY)
                .triggerableAnim("idle", IDLE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}