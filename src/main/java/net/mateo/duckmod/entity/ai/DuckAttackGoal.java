package net.mateo.duckmod.entity.ai;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class DuckAttackGoal extends MeleeAttackGoal {
    private final DuckEntity entity;
    private int attackDelay = 5;
    private int ticksUntilNextAttack = 10;
    private boolean shouldCountTillNextAttack = false;

    public DuckAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        entity = ((DuckEntity) pMob);
    }

    @Override
    public void start() {
        super.start();
        ticksUntilNextAttack = 5; // Faster initial attack
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
        if (isEnemyWithinAttackDistance(pEnemy, pDistToEnemySqr)) {
            shouldCountTillNextAttack = true;

            if(isTimeToStartAttackAnimation()) {
                entity.setAttacking(true);
            }

            if(isTimeToAttack()) {
                this.mob.getLookControl().setLookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performAttack(pEnemy);
            }
        } else {
            if (pDistToEnemySqr > this.getAttackReachSqr(pEnemy) * 2.0) {
                resetAttackCooldown();
                shouldCountTillNextAttack = false;
                entity.setAttacking(false);
                // Reset animation timeout through behavior
                entity.getAnimationBehavior().attackAnimationTimeout = 0;
            }
            // If still relatively close, keep attacking state active for aggression
        }
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        return pDistToEnemySqr <= this.getAttackReachSqr(pEnemy);
    }

    protected void resetAttackCooldown() {
        // Reduced cooldown multiplier from 2x to 1.5x for more aggressive attacks
        this.ticksUntilNextAttack = this.adjustedTickDelay((int)(attackDelay * 1.5));
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected void performAttack(LivingEntity pEnemy) {
        this.resetAttackCooldown();
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(pEnemy);
    }

    @Override
    public void tick() {  // takes charge of the clocks
        super.tick();
        if(shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);  // reduces ticksUntilNextAttack
        }

        // Continuously track target even when not attacking (Zombie pig-man style)
        LivingEntity target = this.mob.getTarget();
        if (target != null && target.isAlive()) {
            this.mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
        }
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }
}