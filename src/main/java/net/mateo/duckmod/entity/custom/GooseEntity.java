package net.mateo.duckmod.entity.custom;

import net.mateo.duckmod.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GooseEntity extends DuckEntity implements PlayerRideable { // Must say, a friend of mine made the geese

    public GooseEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // ========== ATTRIBUTES ==========
    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D);
    }

    @Override
    public void setTame(boolean pTamed) {
        super.setTame(pTamed);
        if (pTamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0D);
            this.setHealth(10.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(6.0D);
        }
    }

    // ========== SOUNDS ==========
    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return null;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return null;
    }

    // ========== LOOT DROPS ==========
    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        int meatAmount = 2 + this.random.nextInt(2) + this.random.nextInt(1 + pLooting);
        if (this.isOnFire()) {
            ItemEntity item = this.spawnAtLocation(Items.COOKED_CHICKEN, meatAmount);
            if (item != null) item.setPos(this.getX(), this.getY() + 0.2, this.getZ());
        } else {
            ItemEntity item = this.spawnAtLocation(Items.CHICKEN, meatAmount);
            if (item != null) item.setPos(this.getX(), this.getY() + 0.2, this.getZ());
        }

        int featherAmount = 2 + this.random.nextInt(3) + this.random.nextInt(1 + pLooting);
        if (featherAmount > 0) {
            ItemEntity featherItem = this.spawnAtLocation(Items.FEATHER, featherAmount);
            if (featherItem != null) featherItem.setPos(this.getX(), this.getY() + 0.2, this.getZ());
        }
    }

    // ========== BREEDING ==========
    @Override
    @Nullable
    public GooseEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        GooseEntity baby = ModEntities.GOOSE.get().create(pLevel);
        if (baby != null) {
            baby.setVariant(this.getVariant());

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
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (!(pOtherAnimal instanceof GooseEntity)) {
            return false;
        } else {
            GooseEntity otherGoose = (GooseEntity)pOtherAnimal;

            if (this.isTame() != otherGoose.isTame()) {
                return false;
            }

            if (this.isTame() && otherGoose.isOrderedToSit()) {
                return false;
            }

            return this.isInLove() && otherGoose.isInLove();
        }
    }

    // ========== RIDEABLE IMPLEMENTATION ==========
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Empty hand + tamed + owner + not baby = RIDE
        if (itemstack.isEmpty() && this.isTame() && this.isOwnedBy(player) && !this.isBaby()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        // Item in hand + tamed + owner + not food = SIT/STAND toggle
        if (!itemstack.isEmpty() && this.isTame() && !isFood(itemstack) && this.isOwnedBy(player)) {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.setJumping(false);
                this.getNavigation().stop();
                this.setTarget(null);
            }
            return InteractionResult.SUCCESS;
        }

        // Otherwise use default duck interaction (taming, breeding, feeding, etc.)
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof Player player && this.isTame() && this.isOwnedBy(player)) {
            return player;
        }
        return null;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isAlive() && this.isVehicle()) {
            LivingEntity rider = this.getControllingPassenger();
            if (rider instanceof Player) {
                // Sync rotation with rider
                this.setYRot(rider.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(rider.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;

                // Get rider input
                float strafe = rider.xxa * 0.5F;
                float forward = rider.zza;

                if (forward <= 0.0F) {
                    forward *= 0.25F; // Slower backwards
                }

                // Use the goose's natural movement speed
                float moveSpeed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);

                // Apply movement based on input
                // The DuckWaterBehavior in tick() will handle floating/swimming
                this.setSpeed(moveSpeed);
                this.moveRelative(this.getSpeed(), new Vec3(strafe, pTravelVector.y, forward));
                this.move(MoverType.SELF, this.getDeltaMovement());

                // Apply friction and gravity
                Vec3 motion = this.getDeltaMovement();

                // The water behavior handles water friction/buoyancy in tick()
                // So we only apply land friction here if not in water
                if (!this.isInWater()) {
                    // Land friction
                    motion = motion.multiply(0.91D, 0.98D, 0.91D);

                    // Gravity
                    if (!this.isNoGravity()) {
                        motion = motion.add(0.0D, -0.08D, 0.0D);
                    }

                    this.setDeltaMovement(motion);
                }
                // If in water, DuckWaterBehavior.floatDuck() handles the motion in tick()

                // Calculate limb animation
                this.calculateEntityAnimation(false);
                return;
            }
        }

        // When not being ridden, use default behavior
        super.travel(pTravelVector);
    }

    public boolean boost() {
        return false;
    }

    // ========== PASSENGER POSITIONING ==========
    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            double yOffset = this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset();
            pCallback.accept(pPassenger, this.getX(), yOffset, this.getZ());
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.75D;
    }
}