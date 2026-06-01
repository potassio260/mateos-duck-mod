package net.mateo.duckmod.entity.custom;

import net.mateo.duckmod.entity.ModEntities;
import net.mateo.duckmod.entity.ai.*;
import net.mateo.duckmod.entity.behavior.*;
import net.mateo.duckmod.entity.variant.DuckVariant;
import net.mateo.duckmod.item.ModItems;
import net.mateo.duckmod.sound.ModSounds;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DuckEntity extends TamableAnimal implements NeutralMob {
    // Entity Data Accessors
    private static final EntityDataAccessor<Boolean> IS_MALE =
            SynchedEntityData.defineId(DuckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(DuckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SITTING =
            SynchedEntityData.defineId(DuckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PREPARING_NEST =
            SynchedEntityData.defineId(DuckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_EATING =
            SynchedEntityData.defineId(DuckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> JUMPING_INTO_WATER =
            SynchedEntityData.defineId(DuckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(DuckEntity.class, EntityDataSerializers.INT);

    // Behavior Components
    private final DuckAnimationBehavior animationBehavior;
    private final DuckWaterBehavior waterBehavior;
    private final DuckNestingBehavior nestingBehavior;
    private final DuckAngerBehavior angerBehavior;
    private final DuckInteractionBehavior interactionBehavior;
    private final DuckSoundBehavior soundBehavior;

    // Food for breeding and healing
    private static final Ingredient FOOD_ITEMS = Ingredient.of(
            Items.BREAD,
            Items.MELON,
            Items.BEETROOT_SEEDS,
            Items.MELON_SEEDS,
            Items.PUMPKIN_SEEDS,
            Items.WHEAT_SEEDS
    );

    public DuckEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setTame(false);
        this.refreshDimensions();
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);

        // Initialize behaviors
        this.animationBehavior = new DuckAnimationBehavior(this);
        this.waterBehavior = new DuckWaterBehavior(this);
        this.nestingBehavior = new DuckNestingBehavior(this);
        this.angerBehavior = new DuckAngerBehavior(this);
        this.interactionBehavior = new DuckInteractionBehavior(this, FOOD_ITEMS);
        this.soundBehavior = new DuckSoundBehavior(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(IS_MALE, false);
        this.entityData.define(IS_SITTING, false);
        this.entityData.define(PREPARING_NEST, false);
        this.entityData.define(IS_EATING, false);
        this.entityData.define(JUMPING_INTO_WATER, false);
        this.entityData.define(VARIANT, 0);
    }

    // Override to set smaller hitbox
    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        // Smaller hitbox: width 0.4, height 0.5 (adjust as needed)
        // Baby ducks are even smaller
        if (this.isBaby()) {
            return EntityDimensions.scalable(0.3F, 0.35F);
        }
        return EntityDimensions.scalable(0.4F, 0.5F);
    }

    // Getters and Setters
    public void setMale(boolean male) { this.entityData.set(IS_MALE, male); }
    public boolean isMale() { return this.entityData.get(IS_MALE); }

    public void setJumpingIntoWater(boolean value) { this.entityData.set(JUMPING_INTO_WATER, value); }
    public boolean isJumpingIntoWater() { return this.entityData.get(JUMPING_INTO_WATER); }

    public void setDuckSitting(boolean sitting) { this.entityData.set(IS_SITTING, sitting); }
    public boolean isDuckSitting() { return this.entityData.get(IS_SITTING); }

    public void setPreparingNest(boolean preparing) { this.entityData.set(PREPARING_NEST, preparing); }
    public boolean isPreparingNest() { return this.entityData.get(PREPARING_NEST); }

    public void setAttacking(boolean attacking) { this.entityData.set(ATTACKING, attacking); }
    public boolean isAttacking() { return this.entityData.get(ATTACKING); }

    public void setEating(boolean eating) { this.entityData.set(IS_EATING, eating); }
    public boolean isEating() { return this.entityData.get(IS_EATING); }

    public DuckVariant getVariant() {
        return DuckVariant.byId(this.entityData.get(VARIANT));
    }

    public void setVariant(DuckVariant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    // Behavior accessors
    public DuckAnimationBehavior getAnimationBehavior() { return animationBehavior; }
    public DuckWaterBehavior getWaterBehavior() { return waterBehavior; }
    public DuckNestingBehavior getNestingBehavior() { return nestingBehavior; }
    public DuckAngerBehavior getAngerBehavior() { return angerBehavior; }
    public DuckInteractionBehavior getInteractionBehavior() { return interactionBehavior; }
    public DuckSoundBehavior getSoundBehavior() { return soundBehavior; }

    // Convenience methods for nesting
    public void setNestPos(BlockPos pos) { nestingBehavior.setNestPos(pos); }
    public BlockPos getNestPos() { return nestingBehavior.getNestPos(); }
    public void setTargetNestPos(BlockPos pos) { nestingBehavior.setTargetNestPos(pos); }
    public BlockPos getTargetNestPos() { return nestingBehavior.getTargetNestPos(); }

    @Override
    public void setTame(boolean pTamed) {
        super.setTame(pTamed);
        if (pTamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
            this.setHealth(8.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(4.0D);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);

        // Drop duck meat
        int duckAmount = 1 + this.random.nextInt(2) + this.random.nextInt(1 + pLooting);
        if (this.isOnFire()) {
            ItemEntity item = this.spawnAtLocation(ModItems.COOKED_DUCK.get(), duckAmount);
            if (item != null) item.setPos(this.getX(), this.getY() + 0.2, this.getZ());
        } else {
            ItemEntity item = this.spawnAtLocation(ModItems.RAW_DUCK.get(), duckAmount);
            if (item != null) item.setPos(this.getX(), this.getY() + 0.2, this.getZ());
        }

        // Drop feathers
        int featherAmount = this.random.nextInt(3) + this.random.nextInt(1 + pLooting);
        if (featherAmount > 0) {
            ItemEntity featherItem = this.spawnAtLocation(Items.FEATHER, featherAmount);
            if (featherItem != null) featherItem.setPos(this.getX(), this.getY() + 0.2, this.getZ());
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        this.setMale(this.random.nextBoolean());

        // Set random variant
        DuckVariant variant = Util.getRandom(DuckVariant.values(), this.random);
        this.setVariant(variant);

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IsMale", this.isMale());
        pCompound.putBoolean("IsDuckSitting", this.isDuckSitting());
        pCompound.putBoolean("PreparingNest", this.isPreparingNest());
        pCompound.putBoolean("IsEating", this.isEating());
        pCompound.putInt("Variant", this.getVariant().getId());

        interactionBehavior.addAdditionalSaveData(pCompound);
        nestingBehavior.addAdditionalSaveData(pCompound);
        angerBehavior.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setMale(pCompound.getBoolean("IsMale"));
        this.setDuckSitting(pCompound.getBoolean("IsDuckSitting"));
        this.setPreparingNest(pCompound.getBoolean("PreparingNest"));
        this.setEating(pCompound.getBoolean("IsEating"));
        this.setVariant(DuckVariant.byId(pCompound.getInt("Variant")));

        interactionBehavior.readAdditionalSaveData(pCompound);
        nestingBehavior.readAdditionalSaveData(pCompound);
        angerBehavior.readAdditionalSaveData(pCompound);
    }

    @Override
    public void tick() {
        super.tick();

        waterBehavior.tick();

        if (!this.level().isClientSide) {
            nestingBehavior.tick();
            interactionBehavior.tick();

            // Handle ordered sitting
            if (this.isOrderedToSit() && !this.isDuckSitting()) {
                this.setDuckSitting(true);
            } else if (!this.isOrderedToSit() && this.isDuckSitting() &&
                    !this.isPreparingNest() && this.getNestPos() == null) {
                this.setDuckSitting(false);
            }

            angerBehavior.tick((ServerLevel) this.level());
        }

        if (this.level().isClientSide()) {
            animationBehavior.setupAnimationStates();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            soundBehavior.tick();
        }
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel pLevel, Animal pMate) {
        nestingBehavior.handleBreeding(pLevel, pMate);
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (!(pOtherAnimal instanceof DuckEntity)) {
            return false;
        } else {
            DuckEntity otherDuck = (DuckEntity)pOtherAnimal;

            // Can't breed tamed with wild
            if (this.isTame() != otherDuck.isTame()) {
                return false;
            }

            // If tamed, check sit status
            if (this.isTame() && otherDuck.isOrderedToSit()) {
                return false;
            }

            // Must be same variant
            if (this.getVariant() != otherDuck.getVariant()) {
                return false;
            }

            // Both must be in love
            return this.isInLove() && otherDuck.isInLove();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (!this.level().isClientSide) {
            angerBehavior.tick((ServerLevel) this.level());
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        angerBehavior.onSetTarget(pTarget);
        super.setTarget(pTarget);
    }

    // NeutralMob Interface Methods
    @Override
    public void setRemainingPersistentAngerTime(int pTime) {
        angerBehavior.setRemainingPersistentAngerTime(pTime);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return angerBehavior.getRemainingPersistentAngerTime();
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pTarget) {
        angerBehavior.setPersistentAngerTarget(pTarget);
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return angerBehavior.getPersistentAngerTarget();
    }

    public void startPersistentAngerTimer() {
        angerBehavior.startPersistentAngerTimer();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Check if this is a sit/stand toggle for tamed ducks (not consuming an item)
        if (this.isTame() && !isFood(itemstack) && this.isOwnedBy(player)) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.setJumping(false);
                this.getNavigation().stop();
                this.setTarget(null);
            }
            return InteractionResult.SUCCESS;
        }

        // Otherwise, delegate to interaction behavior
        return interactionBehavior.handleInteraction(player, hand);
    }

    @Override
    public void ate() {
        interactionBehavior.handleEating();
    }

    public void triggerHissAnimation() {
        animationBehavior.triggerHissAnimation();
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        animationBehavior.updateWalkAnimation(pPartialTick);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return FOOD_ITEMS.test(pStack);
    }

    public List<DuckEntity> getNearbyDucks(double range) {
        AABB searchBox = this.getBoundingBox().inflate(range);
        return this.level().getEntitiesOfClass(DuckEntity.class, searchBox,
                duck -> duck != this && !duck.isBaby());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new DuckAttackGoal(this, 1.0D, true) {
            @Override public boolean canUse() {
                return !isPreparingNest() && !(isDuckSitting() && getTarget() == null)
                        && !isOrderedToSit() && super.canUse();
            }
            @Override public boolean canContinueToUse() {
                return !isPreparingNest() && !(isDuckSitting() && getTarget() == null)
                        && !isOrderedToSit() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, FOOD_ITEMS, false) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false) {
            @Override public boolean canUse() {
                return !isOrderedToSit() && super.canUse();
            }
            @Override public boolean canContinueToUse() {
                return !isOrderedToSit() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(7, new DuckFlockGoal(this, 1.0D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new DuckGoToWaterGoal(this, 1.0D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(9, new DuckSwimToDeepWaterGoal(this, 0.8D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(10, new RandomStrollGoal(this, 1.0D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(11, new DuckGoToLandGoal(this, 1.0D) {
            @Override public boolean canUse() {
                return !isDuckSitting() && !isPreparingNest() && !isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(13, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this) {
            @Override public boolean canUse() {
                if (isOrderedToSit()) return false;
                if (isDuckSitting() && !isOrderedToSit()) return getLastHurtByMob() != null;
                return super.canUse();
            }
        }.setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class,
                10, true, false, this::isAngryAt) {
            @Override public boolean canUse() {
                return !isPreparingNest() && super.canUse();
            }
        });
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return this.isBaby() ? pDimensions.height * 0.85F : pDimensions.height * 0.92F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D);
    }

    @Override
    @Nullable
    public DuckEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        DuckEntity baby = ModEntities.DUCK.get().create(pLevel);
        if (baby != null) {
            // Inherit variant from parents (50/50 chance)
            DuckVariant variant;
            if (pOtherParent instanceof DuckEntity otherDuck) {
                variant = this.random.nextBoolean() ? this.getVariant() : otherDuck.getVariant();
            } else {
                variant = this.getVariant();
            }
            baby.setVariant(variant);

            // If parent is tamed, baby inherits taming
            if (this.isTame()) {
                UUID uuid = this.getOwnerUUID();
                if (uuid != null) {
                    baby.setOwnerUUID(uuid);
                    baby.setTame(true);
                }
            } else {
                baby.setTame(false);
            }
        }
        return baby;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        // Return the quack sound so Minecraft's ambient sound system works
        if (this.isMale()) {
            int variant = this.random.nextInt(3);
            return switch (variant) {
                case 1 -> ModSounds.MALE_QUACK_2.get();
                case 2 -> ModSounds.MALE_QUACK_3.get();
                default -> ModSounds.MALE_QUACK_1.get();
            };
        } else {
            int variant = this.random.nextInt(2);
            return variant == 0 ? ModSounds.FEMALE_QUACK_1.get() : ModSounds.FEMALE_QUACK_2.get();
        }
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return soundBehavior.getHurtSound();
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return soundBehavior.getDeathSound();
    }

    public boolean isInWaterOrRain() {
        return waterBehavior.isInWaterOrRain();
    }

    @Override
    public boolean wantsToAttack(LivingEntity pTarget, LivingEntity pOwner) {
        if (pTarget instanceof TamableAnimal && ((TamableAnimal)pTarget).isTame()) {
            return false;
        } else if (pTarget instanceof Player && pOwner instanceof Player &&
                !((Player)pOwner).canHarmPlayer((Player)pTarget)) {
            return false;
        } else {
            return true;
        }
    }
}