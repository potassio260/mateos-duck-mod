package net.mateo.duckmod.entity.behavior;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DuckWaterBehavior {
    private final DuckEntity duck;

    private double waterLevel;
    private float invFriction = 0.9F;
    private DuckStatus status = DuckStatus.ON_LAND;
    private DuckStatus oldStatus = DuckStatus.ON_LAND;

    // Cooldown prevents jump spam
    private int jumpCooldown = 0;

    public DuckWaterBehavior(DuckEntity duck) {
        this.duck = duck;
    }

    public boolean shouldPlayWaterJump() { // Jumping in water logic
        if (jumpCooldown > 0) return false;

        // Raise threshold significantly - tweaked this so much !!!!
        Vec3 motion = duck.getDeltaMovement();
        if (motion.horizontalDistanceSqr() < 0.005) return false;

        boolean validState = (status == DuckStatus.ON_LAND) || (status == DuckStatus.IN_WATER && oldStatus == DuckStatus.ON_LAND);
        if (!validState) return false;

        return isWaterAhead();
    }

    private boolean isWaterAhead() {
        // Use movement direction, not view direction
        Vec3 motion = duck.getDeltaMovement();
        Vec3 moveDir = new Vec3(motion.x, 0, motion.z).normalize();

        // Fall back to view vector if motion is too small to normalize reliably
        if (moveDir.lengthSqr() < 0.5) {
            Vec3 viewVec = duck.getViewVector(1.0F);
            moveDir = new Vec3(viewVec.x, 0, viewVec.z).normalize();
        }

        return checkPosForWater(moveDir, 0.5) || checkPosForWater(moveDir, 1.2);
    }

    private boolean checkPosForWater(Vec3 direction, double distance) {
        BlockPos posAhead = BlockPos.containing(
                duck.getX() + direction.x * distance,
                duck.getY(), // Check at feet level
                duck.getZ() + direction.z * distance
        );

        // Check feet level
        if (duck.level().getFluidState(posAhead).is(FluidTags.WATER)) return true;

        // Check one block down (for stepping down into a river/pond)
        if (duck.level().getFluidState(posAhead.below()).is(FluidTags.WATER)) return true;

        return false;
    }

    public void activateJumpCooldown() {
        this.jumpCooldown = 20; // 1 second cooldown
    }

    public void tick() {
        this.oldStatus = this.status; // Save previous state
        this.status = this.getWaterStatus();

        // Tick down the cooldown
        if (this.jumpCooldown > 0) {
            this.jumpCooldown--;
        }

        // Only apply water physics if NOT sitting or preparing nest
        // FIXED: Changed isSitting() to isDuckSitting()
        if (!duck.isDuckSitting() && !duck.isPreparingNest()) {
            this.floatDuck();

            // Run swimming logic if in water
            if (!duck.level().isClientSide && (this.status == DuckStatus.IN_WATER || this.status == DuckStatus.UNDER_WATER)) {
                this.swimDuck();
            }
        }
    }

    private void floatDuck() { // this method was a nightmare
        double gravity = duck.isNoGravity() ? 0.0D : -0.04D;
        double verticalPush = 0.0D;

        if (this.status == DuckStatus.IN_WATER) {
            this.invFriction = 0.9F;
            verticalPush = ((this.waterLevel - 0.1875D) - duck.getY()) / (double) duck.getBbHeight();
        } else if (this.status == DuckStatus.UNDER_WATER) {
            this.invFriction = 0.7F;
            verticalPush = 0.03D;
        } else {
            this.invFriction = 1.0F;
        }

        Vec3 motion = duck.getDeltaMovement();

        if (this.status == DuckStatus.IN_WATER || this.status == DuckStatus.UNDER_WATER) {
            if (verticalPush > 0.0D) {
                duck.setDeltaMovement(
                        motion.x * (double) this.invFriction,
                        (motion.y + verticalPush * 0.06153846016296973D) * 0.75D,
                        motion.z * (double) this.invFriction
                );
            } else {
                duck.setDeltaMovement(
                        motion.x * (double) this.invFriction,
                        motion.y + gravity * 0.5D,
                        motion.z * (double) this.invFriction
                );
            }
        }
    }

    private void swimDuck() {
        Vec3 target = duck.getNavigation().getTargetPos() != null
                ? Vec3.atCenterOf(duck.getNavigation().getTargetPos())
                : null;

        if (target != null && !duck.getNavigation().isDone()) {
            double dx = target.x - duck.getX();
            double dz = target.z - duck.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0.5D) {
                double swimSpeed = duck.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.8D;

                if (this.status == DuckStatus.UNDER_WATER) {
                    swimSpeed *= 0.8D;
                }

                double velocityX = (dx / distance) * swimSpeed;
                double velocityZ = (dz / distance) * swimSpeed;

                Vec3 currentMotion = duck.getDeltaMovement();
                duck.setDeltaMovement(
                        currentMotion.x + velocityX * 0.15D,
                        currentMotion.y,
                        currentMotion.z + velocityZ * 0.15D
                );

                float targetYaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
                duck.setYRot(Mth.wrapDegrees(targetYaw));
            }
        }
    }

    private DuckStatus getWaterStatus() {
        AABB aabb = duck.getBoundingBox();
        double waterCheckHeight = aabb.maxY + 0.001D;
        int minX = Mth.floor(aabb.minX);
        int maxX = Mth.ceil(aabb.maxX);
        int minY = Mth.floor(aabb.minY);
        int maxY = Mth.ceil(waterCheckHeight);
        int minZ = Mth.floor(aabb.minZ);
        int maxZ = Mth.ceil(aabb.maxZ);

        boolean foundWater = false;
        this.waterLevel = -Double.MAX_VALUE;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    pos.set(x, y, z);
                    FluidState fluidState = duck.level().getFluidState(pos);

                    if (fluidState.is(FluidTags.WATER)) {
                        float fluidHeight = (float) y + fluidState.getHeight(duck.level(), pos);
                        this.waterLevel = Math.max((double) fluidHeight, this.waterLevel);

                        if (waterCheckHeight < (double) fluidHeight) {
                            return DuckStatus.UNDER_WATER;
                        }
                        foundWater = true;
                    }
                }
            }
        }

        if (foundWater && this.waterLevel > aabb.minY) {
            return DuckStatus.IN_WATER;
        }

        return DuckStatus.ON_LAND;
    }

    public boolean isInWaterOrRain() {
        return this.status == DuckStatus.IN_WATER || this.status == DuckStatus.UNDER_WATER;
    }

    public DuckStatus getStatus() {
        return status;
    }

    public enum DuckStatus {
        IN_WATER,
        UNDER_WATER,
        ON_LAND
    }
}