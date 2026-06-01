package net.mateo.duckmod.entity.ai;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;

public class DuckFlockGoal extends Goal {
    private final DuckEntity duck;
    private final double speedModifier;
    private DuckEntity leaderDuck;
    private int checkCooldown = 0;

    public DuckFlockGoal(DuckEntity pDuck, double pSpeedModifier) {
        this.duck = pDuck;
        this.speedModifier = pSpeedModifier;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.checkCooldown > 0) {
            --this.checkCooldown;
            return false;
        }

        // Don't flock if being tempted, breeding, panicking, or attacking
        if (this.duck.isInLove() || this.duck.isAttacking()) {
            return false;
        }

        // Random chance to check for flock (30% chance every 20 ticks)
        if (this.duck.getRandom().nextInt(20) != 0) {
            return false;
        }

        // Find nearby ducks
        List<DuckEntity> nearbyDucks = this.duck.getNearbyDucks(10.0D);

        if (nearbyDucks.isEmpty()) {
            return false;
        }

        // Find the closest duck as the leader
        DuckEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (DuckEntity otherDuck : nearbyDucks) {
            double dist = this.duck.distanceToSqr(otherDuck);
            if (dist < closestDist && dist > 4.0D) { // Not too close (2 blocks squared)
                closestDist = dist;
                closest = otherDuck;
            }
        }

        if (closest != null) {
            this.leaderDuck = closest;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        // Move towards the leader duck's position
        if (this.leaderDuck != null && this.leaderDuck.isAlive()) {
            BlockPos targetPos = this.leaderDuck.blockPosition();
            this.duck.getNavigation().moveTo(
                    targetPos.getX(),
                    targetPos.getY(),
                    targetPos.getZ(),
                    this.speedModifier
            );
        }
    }

    @Override
    public boolean canContinueToUse() {
        // Stop if leader is gone, too close, or navigation is done
        if (this.leaderDuck == null || !this.leaderDuck.isAlive()) {
            return false;
        }

        double distance = this.duck.distanceToSqr(this.leaderDuck);

        // Stop if within 2-3 blocks of leader
        if (distance < 9.0D) {
            return false;
        }

        // Stop if leader is too far (beyond 15 blocks)
        if (distance > 225.0D) {
            return false;
        }

        return !this.duck.getNavigation().isDone();
    }

    @Override
    public void tick() {
        // Update path to follow moving leader
        if (this.leaderDuck != null && this.leaderDuck.isAlive() && this.duck.tickCount % 10 == 0) {
            BlockPos targetPos = this.leaderDuck.blockPosition();
            this.duck.getNavigation().moveTo(
                    targetPos.getX(),
                    targetPos.getY(),
                    targetPos.getZ(),
                    this.speedModifier
            );
        }
    }

    @Override
    public void stop() {
        this.leaderDuck = null;
        this.checkCooldown = 40 + this.duck.getRandom().nextInt(40);
    }
}