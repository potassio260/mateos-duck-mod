package net.mateo.robomod.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class CyborgAnimationTestEntity extends LivingEntity implements GeoEntity {

    // 1. Declare both of your raw animations here at the top
    protected static final RawAnimation FAN = RawAnimation.begin().thenPlayAndHold("fan");
    protected static final RawAnimation WAKE_DEFAULT = RawAnimation.begin().thenPlayAndHold("wake_default");

    // Replaces DataTracker.registerData → SynchedEntityData.defineId
    private static final EntityDataAccessor<Boolean> OFF =
            SynchedEntityData.defineId(CyborgAnimationTestEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WAKE =
            SynchedEntityData.defineId(CyborgAnimationTestEntity.class, EntityDataSerializers.BOOLEAN);

    // 2. Properly initialize the GeckoLib 4 Cache
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public int tickEye;
    public boolean reverse;

    public CyborgAnimationTestEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    // Replaces initDataTracker(DataTracker.Builder) → defineSynchedData()
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OFF, true);
        this.entityData.define(WAKE, false);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public boolean isDeadOrDying() {
        return super.isDeadOrDying();
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        // intentionally empty, same as Fabric original
    }

    @Override
    public void tick() {
        // Replaces getWorld().isClient → level.isClientSide
        // Replaces getDataTracker().get(...) → entityData.get(...)
        if (!this.level().isClientSide && !this.entityData.get(WAKE)) {
            // Replaces getNonSpectatingEntities → getEntitiesOfClass
            List<Player> list = this.level().getEntitiesOfClass(
                    Player.class,
                    this.getBoundingBox().inflate(3, -1, 3),
                    p -> !p.isSpectator()
            );
            if (!list.isEmpty()) {
                Player player = list.get(this.getRandom().nextInt(list.size()));
                this.setOff(false);
                if (this.hasLineOfSight(player)) {
                    triggerAnim("wake", "fan");
                    // Replaces getWorld().getDamageSources().mobAttack(this)
                    player.hurt(this.level().damageSources().mobAttack(this), 1);
                    this.setWake(true);
                } else {
                    triggerAnim("wake", "wake_default");
                    this.setWake(true);
                }
            }
        }
        if (this.level().isClientSide) {
            tickEye = 255;
        }
        super.tick();
    }

    private void setOff(boolean state) {
        this.entityData.set(OFF, state);
    }

    public boolean isOff() {
        return this.entityData.get(OFF);
    }

    private void setWake(boolean state) {
        this.entityData.set(WAKE, state);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    // 3. Register your controller here, requiring the 5-tick transition length
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "wake", 5, state -> PlayState.STOP)
                .triggerableAnim("fan", FAN)
                .triggerableAnim("wake_default", WAKE_DEFAULT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}