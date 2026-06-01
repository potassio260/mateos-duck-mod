package net.mateo.robomod.item;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.ControllerBlock;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class LongArmModule extends AnimatableCyborgModule {

    public static final int ENERGY_CONSUME = 10;

    private static final UUID LONG_ARM_UUID =
            UUID.nameUUIDFromBytes(RoboMod.id("long_arm_module").toString().getBytes());

    // Pre-built modifiers so we can check by UUID via getModifier()
    private static final AttributeModifier LONG_ARM_ENTITY_MODIFIER =
            new AttributeModifier(LONG_ARM_UUID, "long_arm_module", 3, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier LONG_ARM_BLOCK_MODIFIER =
            new AttributeModifier(LONG_ARM_UUID, "long_arm_module", 3, AttributeModifier.Operation.ADDITION);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation HOOK_ANIM = RawAnimation.begin().thenPlay("hook");

    public LongArmModule(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void onModuleRemoved(Level level, Player player) {
        player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get()).removeModifier(LONG_ARM_UUID);
        player.getAttribute(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get()).removeModifier(LONG_ARM_UUID);
    }

    @Override
    public void controllerLogic(ControllerBlock controllerBlock, BlockPos pos, Level level, Player player, ItemStack stack) {
        if (level instanceof ServerLevel serverLevel) {
            GeoItem.getOrAssignId(stack, serverLevel);
        }
    }

    @Override
    public void tick(ServerLevel level, Player player, PlayerExtension extension, ItemStack stack) {
        long id = getOrAssignIdUpdate(stack, level, player);

        var entityReach = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get());
        var blockReach  = player.getAttribute(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get());

        if (ENERGY_CONSUME <= extension.getEnergyStored()) {
            // FIX: AttributeInstance.hasModifier(UUID) was removed in 1.20.1.
            // Use getModifier(UUID) != null instead.
            if (entityReach.getModifier(LONG_ARM_UUID) == null && blockReach.getModifier(LONG_ARM_UUID) == null) {
                entityReach.addPermanentModifier(LONG_ARM_ENTITY_MODIFIER);
                blockReach.addPermanentModifier(LONG_ARM_BLOCK_MODIFIER);
            }
            if (!player.isCreative() && !player.isSpectator()) {
                extension.setEnergyStored(Math.max(extension.getEnergyStored() - 1, 0));
            }
        } else {
            entityReach.removeModifier(LONG_ARM_UUID);
            blockReach.removeModifier(LONG_ARM_UUID);
        }

        if (player.swinging) {
            triggerAnim(player, id, "hook", "hook");
        }
        // FIX: stopTriggeredAnim(Player, long, String, String) does not exist in this GeckoLib version.
        // Triggered animations play once then stop naturally — no explicit stop needed.
        // If your AnimatableCyborgModule base class adds stopTriggeredAnim, keep the call.

        super.tick(level, player, extension, stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "hook", 0, state -> PlayState.STOP)
                .triggerableAnim("hook", HOOK_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}