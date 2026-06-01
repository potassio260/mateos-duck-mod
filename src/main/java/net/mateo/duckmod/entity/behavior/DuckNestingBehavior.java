package net.mateo.duckmod.entity.behavior;

import net.mateo.duckmod.block.ModBlocks;
import net.mateo.duckmod.block.custom.NestBlock;
import net.mateo.duckmod.entity.custom.DuckEntity;
import net.mateo.duckmod.entity.custom.GooseEntity;
import net.mateo.duckmod.entity.custom.NestBlockEntity;
import net.mateo.duckmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DuckNestingBehavior {
    private final DuckEntity duck;

    private BlockPos nestPos = null;
    private BlockPos targetNestPos = null;
    private int preparingNestTimer = 0;
    private int ticksSinceLastSit = 0;
    private int timeAwayFromNest = 0;
    private int hissCooldown = 0;

    private static final int MAX_TIME_AWAY = 200;  // TO-DO: will get up and return after certain time
    private static final int PREPARE_NEST_TIME = 100;
    private static final double NEST_DEFENSE_RANGE = 3.0;
    private static final int HISS_COOLDOWN_TICKS = 40;

    public DuckNestingBehavior(DuckEntity duck) {
        this.duck = duck;
    }

    public void tick() {
        if (hissCooldown > 0) {
            hissCooldown--;
        }

        // Phase 1: Searching for and preparing nest location
        if (duck.isPreparingNest() && targetNestPos != null && nestPos == null) {
            handleNestPreparation();
        }
        // Phase 2: Stay on nest, return if pushed off, defend against nearby players
        else if (duck.isDuckSitting() && nestPos != null) {
            handleSittingOnNest();

            // Check if we are still sitting/have a nest before defending
            if (duck.isDuckSitting() && nestPos != null) {
                defendNest();
            }
        }
    }

    private void defendNest() {
        if (duck.level().isClientSide) {
            return;
        }

        if (duck.isMale()) {
            return;
        }

        AABB searchBox = new AABB(nestPos).inflate(NEST_DEFENSE_RANGE);

        List<Player> nearbyPlayers = duck.level().getEntitiesOfClass(
                Player.class,
                searchBox,
                player -> !player.isCreative() && !player.isSpectator()
        );

        if (duck.getTarget() instanceof Player currentTarget) {
            double distanceToTarget = currentTarget.distanceToSqr(
                    nestPos.getX() + 0.5,
                    nestPos.getY(),
                    nestPos.getZ() + 0.5
            );

            if (distanceToTarget > NEST_DEFENSE_RANGE * NEST_DEFENSE_RANGE) {
                duck.setTarget(null);
                duck.setRemainingPersistentAngerTime(0);
                duck.setPersistentAngerTarget(null);
                return;
            }
        }

        if (!nearbyPlayers.isEmpty()) {
            Player closestPlayer = null;
            double closestDistance = Double.MAX_VALUE;

            for (Player player : nearbyPlayers) {
                double distance = player.distanceToSqr(nestPos.getX() + 0.5, nestPos.getY(), nestPos.getZ() + 0.5);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }

            if (closestPlayer != null) {
                if (hissCooldown <= 0 && closestDistance < (NEST_DEFENSE_RANGE * NEST_DEFENSE_RANGE)) {

                    duck.level().playSound(
                            null,
                            duck.getX(), duck.getY(), duck.getZ(),
                            ModSounds.DUCK_HISS.get(),
                            net.minecraft.sounds.SoundSource.HOSTILE,
                            1.0F,
                            1.0F
                    );

                    duck.triggerHissAnimation();
                    hissCooldown = HISS_COOLDOWN_TICKS;
                }

                if (closestDistance < (2.0 * 2.0) && duck.getTarget() != closestPlayer) {

                    duck.setTarget(closestPlayer);
                    duck.setRemainingPersistentAngerTime(400);
                    duck.setPersistentAngerTarget(closestPlayer.getUUID());
                }
            }
        } else {
            if (duck.getTarget() != null) {
                duck.setTarget(null);
                duck.setRemainingPersistentAngerTime(0);
                duck.setPersistentAngerTarget(null);
            }
        }
    }

    private void handleNestPreparation() {
        Vec3 targetCenter = new Vec3(
                targetNestPos.getX() + 0.5,
                targetNestPos.getY(),
                targetNestPos.getZ() + 0.5
        );

        double dx = targetCenter.x - duck.getX();
        double dz = targetCenter.z - duck.getZ();
        double distSq = dx * dx + dz * dz;
        double distance = Math.sqrt(distSq);

        if (distSq <= 0.5 * 0.5) {
            duck.getNavigation().stop();
            Vec3 currentMotion = duck.getDeltaMovement();
            duck.setDeltaMovement(0, currentMotion.y, 0);
            preparingNestTimer++;


            if (preparingNestTimer >= PREPARE_NEST_TIME) {
                createNest();
            }
        } else {
            navigateToNestLocation(targetCenter, dx, dz, distance);
            preparingNestTimer = 0;
        }
    }

    private void navigateToNestLocation(Vec3 targetCenter, double dx, double dz, double distance) {
        boolean navSuccess = duck.getNavigation().moveTo(targetCenter.x, targetCenter.y, targetCenter.z, 0.8);
        if (!navSuccess || duck.getDeltaMovement().horizontalDistanceSqr() < 0.001) {
            double pushSpeed = 0.05;
            double normalizedDx = dx / distance;
            double normalizedDz = dz / distance;

            Vec3 currentMotion = duck.getDeltaMovement();
            duck.setDeltaMovement(
                    normalizedDx * pushSpeed,
                    currentMotion.y,
                    normalizedDz * pushSpeed
            );

            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
            duck.setYRot(targetYaw);
        }
    }

    private void createNest() {
        int eggCount = 2 + duck.getRandom().nextInt(3);
        BlockState nestState = ModBlocks.NEST_BLOCK.get().defaultBlockState()
                .setValue(NestBlock.EGGS, eggCount);
        duck.level().setBlock(targetNestPos, nestState, 3);

        // Tell the NestBlockEntity which variant laid each egg so babies hatch correctly
        BlockEntity be = duck.level().getBlockEntity(targetNestPos);
        if (be instanceof NestBlockEntity nestBlockEntity) {
            int variantId = duck.getVariant().getId();
            for (int i = 0; i < eggCount; i++) {
                nestBlockEntity.queueEggVariant(variantId);
            }
        }

        nestPos = targetNestPos;
        targetNestPos = null;
        duck.setPreparingNest(false);
        duck.setDuckSitting(true);
        preparingNestTimer = 0;
        ticksSinceLastSit = 0;
        timeAwayFromNest = 0;
        hissCooldown = 0;
    }

    private void handleSittingOnNest() {
        BlockState state = duck.level().getBlockState(nestPos);

        if (state.getBlock() == ModBlocks.NEST_BLOCK.get() && state.getValue(NestBlock.EGGS) > 0) {
            maintainNestPosition(state);
        } else {
            leaveNest();
        }
    }

    private void maintainNestPosition(BlockState state) {
        Vec3 nestCenter = new Vec3(
                nestPos.getX() + 0.5,
                nestPos.getY(),
                nestPos.getZ() + 0.5
        );

        double dx = nestCenter.x - duck.getX();
        double dz = nestCenter.z - duck.getZ();
        double distSq = dx * dx + dz * dz;
        double distance = Math.sqrt(distSq);

        if (distSq > 0.3 * 0.3) {
            returnToNest(nestCenter, dx, dz, distance);
        } else {
            stayAtNest();
        }
    }

    private void returnToNest(Vec3 nestCenter, double dx, double dz, double distance) {
        boolean navSuccess = duck.getNavigation().moveTo(nestCenter.x, nestCenter.y, nestCenter.z, 1.2D);

        if (!navSuccess || duck.getDeltaMovement().horizontalDistanceSqr() < 0.001) {
            double pushSpeed = 0.05;
            double normalizedDx = dx / distance;
            double normalizedDz = dz / distance;

            Vec3 currentMotion = duck.getDeltaMovement();
            duck.setDeltaMovement(
                    normalizedDx * pushSpeed,
                    currentMotion.y,
                    normalizedDz * pushSpeed
            );

            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
            duck.setYRot(targetYaw);
        }

        timeAwayFromNest = 0;
    }

    private void stayAtNest() {
        duck.getNavigation().stop();
        Vec3 currentMotion = duck.getDeltaMovement();
        duck.setDeltaMovement(0, currentMotion.y, 0);
        timeAwayFromNest = 0;
    }

    private void leaveNest() {
        duck.setDuckSitting(false);
        duck.setPreparingNest(false);
        nestPos = null;
        targetNestPos = null;
        ticksSinceLastSit = 0;
        timeAwayFromNest = 0;
        preparingNestTimer = 0;
        hissCooldown = 0;

        duck.getNavigation().stop();

        duck.setTarget(null);
        duck.setRemainingPersistentAngerTime(0);
        duck.setPersistentAngerTarget(null);
    }

    public void handleBreeding(ServerLevel pLevel, Animal pMate) {
        if (pMate instanceof DuckEntity otherDuck) {

            // Only check variants for actual ducks, not geese
            if (!(duck instanceof GooseEntity)) {
                if (duck.getVariant() != otherDuck.getVariant()) {
                    return;
                }
            }

            if (duck.isMale() != otherDuck.isMale()) {
                DuckEntity female = duck.isMale() ? otherDuck : duck;

                BlockPos nestLocation = findNestLocation(female.blockPosition());

                if (nestLocation != null) {
                    female.setTargetNestPos(nestLocation);
                    female.setPreparingNest(true);
                    female.getNestingBehavior().preparingNestTimer = 0;
                    female.getNestingBehavior().ticksSinceLastSit = 0;
                    female.getNestingBehavior().timeAwayFromNest = 0;
                    female.getNestingBehavior().hissCooldown = 0;

                    duck.resetLove();
                    pMate.resetLove();
                }
            }
        }
    }

    private BlockPos findNestLocation(BlockPos center) {
        for (int distance = 1; distance <= 3; distance++) {
            for (int x = -distance; x <= distance; x++) {
                for (int z = -distance; z <= distance; z++) {
                    if (Math.abs(x) == distance || Math.abs(z) == distance) {
                        for (int y = -1; y <= 1; y++) {
                            BlockPos pos = center.offset(x, y, z);
                            BlockPos below = pos.below();

                            if (duck.level().getBlockState(pos).isAir() &&
                                    duck.level().getBlockState(below).isSolid() &&
                                    !duck.level().getBlockState(below).liquid()) {
                                return pos;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == duck) {
            return false;
        } else if (!(pOtherAnimal instanceof DuckEntity)) {
            return false;
        } else {
            DuckEntity otherDuck = (DuckEntity) pOtherAnimal;

            if (duck.getVariant() != otherDuck.getVariant()) {
                return false;
            }

            return duck.isInLove() && otherDuck.isInLove() &&
                    duck.isMale() != otherDuck.isMale() &&
                    !duck.isDuckSitting() && !otherDuck.isDuckSitting() &&
                    !duck.isPreparingNest() && !otherDuck.isPreparingNest();
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("TicksSinceLastSit", ticksSinceLastSit);
        pCompound.putInt("PreparingNestTimer", preparingNestTimer);
        pCompound.putInt("TimeAwayFromNest", timeAwayFromNest);
        pCompound.putInt("HissCooldown", hissCooldown);
        if (nestPos != null) {
            pCompound.putInt("NestX", nestPos.getX());
            pCompound.putInt("NestY", nestPos.getY());
            pCompound.putInt("NestZ", nestPos.getZ());
        }
        if (targetNestPos != null) {
            pCompound.putInt("TargetNestX", targetNestPos.getX());
            pCompound.putInt("TargetNestY", targetNestPos.getY());
            pCompound.putInt("TargetNestZ", targetNestPos.getZ());
        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        ticksSinceLastSit = pCompound.getInt("TicksSinceLastSit");
        preparingNestTimer = pCompound.getInt("PreparingNestTimer");
        timeAwayFromNest = pCompound.getInt("TimeAwayFromNest");
        hissCooldown = pCompound.getInt("HissCooldown");
        if (pCompound.contains("NestX")) {
            nestPos = new BlockPos(
                    pCompound.getInt("NestX"),
                    pCompound.getInt("NestY"),
                    pCompound.getInt("NestZ")
            );
        }
        if (pCompound.contains("TargetNestX")) {
            targetNestPos = new BlockPos(
                    pCompound.getInt("TargetNestX"),
                    pCompound.getInt("TargetNestY"),
                    pCompound.getInt("TargetNestZ")
            );
        }
    }

    public BlockPos getNestPos() {
        return nestPos;
    }

    public void setNestPos(BlockPos pos) {
        this.nestPos = pos;
    }

    public BlockPos getTargetNestPos() {
        return targetNestPos;
    }

    public void setTargetNestPos(BlockPos pos) {
        this.targetNestPos = pos;
    }
}