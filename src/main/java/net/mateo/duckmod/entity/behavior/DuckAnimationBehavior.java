package net.mateo.duckmod.entity.behavior;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

public class DuckAnimationBehavior { // handling animations is torture
    private final DuckEntity duck;

    // Animation States
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState quackAnimationState = new AnimationState();
    public int quackAnimationTimeout = 0;

    public final AnimationState hissAnimationState = new AnimationState();
    public int hissAnimationTimeout = 0;

    public final AnimationState tailWagAnimationState = new AnimationState();
    public int tailWagAnimationTimeout = 0;

    public final AnimationState gobbleAnimationState = new AnimationState();
    public int gobbleAnimationTimeout = 0;

    public final AnimationState runAnimationState = new AnimationState();

    public final AnimationState jumpAnimationState = new AnimationState();
    public int jumpAnimationTimeout = 0;

    public final AnimationState downAnimationState = new AnimationState();
    public int downAnimationTimeout = 0;

    // Animation timers
    private int nextQuackTime = 0;
    private int nextHissTime = 0;
    private int nextTailWagTime = 0;
    private int nextDownTime = 0;

    public DuckAnimationBehavior(DuckEntity duck) {
        this.duck = duck;
        this.nextQuackTime = duck.getRandom().nextInt(200) + 200;
        this.nextHissTime = duck.getRandom().nextInt(400) + 400;
        this.nextTailWagTime = duck.getRandom().nextInt(300) + 300;
        this.nextDownTime = duck.getRandom().nextInt(600) + 600;
    }

    public void setupAnimationStates() {
        // JUMP ANIMATION
        if (duck.getWaterBehavior().shouldPlayWaterJump() && !jumpAnimationState.isStarted() && !duck.isDuckSitting()) {
            this.jumpAnimationTimeout = 16;
            this.jumpAnimationState.start(duck.tickCount);
            duck.getWaterBehavior().activateJumpCooldown();
            Vec3 viewVec = duck.getViewVector(1.0F);
            duck.setDeltaMovement(viewVec.x * 0.4D, 0.4D, viewVec.z * 0.4D);
        }
        if (this.jumpAnimationTimeout > 0) this.jumpAnimationTimeout--;
        else if (jumpAnimationState.isStarted()) jumpAnimationState.stop();

        // RUN ANIMATION
        if (isRunning()) runAnimationState.startIfStopped(duck.tickCount);
        else runAnimationState.stop();

        // GOBBLE/EAT ANIMATION
        if (duck.isEating()) {
            if (!gobbleAnimationState.isStarted()) {
                gobbleAnimationState.start(duck.tickCount);
            }
        } else {
            if (gobbleAnimationState.isStarted()) gobbleAnimationState.stop();
        }

        // ATTACK ANIMATION
        if (duck.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 20;
            attackAnimationState.start(duck.tickCount);
        } else {
            --this.attackAnimationTimeout;
        }
        if (!duck.isAttacking()) attackAnimationState.stop();

        boolean isBusy = duck.isDuckSitting() || duck.isPreparingNest() ||
                duck.isEating() || duck.isOrderedToSit();

        // QUACK ANIMATION
        if (!isBusy && nextQuackTime > 0) --nextQuackTime;
        if (nextQuackTime <= 0 && !isBusy) {
            quackAnimationTimeout = 91;
            quackAnimationState.start(duck.tickCount);
            nextQuackTime = duck.getRandom().nextInt(200) + 200;
        }
        if (quackAnimationTimeout > 0) --quackAnimationTimeout;
        else quackAnimationState.stop();

        // HISS ANIMATION
        if (hissAnimationTimeout > 0) --hissAnimationTimeout;
        else hissAnimationState.stop();

        // TAIL WAG ANIMATION
        if (!isBusy && nextTailWagTime > 0) --nextTailWagTime;
        if (nextTailWagTime <= 0 && !isBusy) {
            tailWagAnimationTimeout = 20;
            tailWagAnimationState.start(duck.tickCount);
            nextTailWagTime = duck.getRandom().nextInt(300) + 300;
        }
        if (tailWagAnimationTimeout > 0) --tailWagAnimationTimeout;
        else tailWagAnimationState.stop();

        // HEAD DOWN ANIMATION
        if (!isBusy && nextDownTime > 0) --nextDownTime;
        if (nextDownTime <= 0 && !isBusy && !isMoving()) {
            downAnimationTimeout = 103;
            downAnimationState.start(duck.tickCount);
            nextDownTime = duck.getRandom().nextInt(600) + 600;
        }
        if (downAnimationTimeout > 0) --downAnimationTimeout;
        else downAnimationState.stop();

        // BASE IDLE ANIMATION
        boolean isDoingAction = isMoving()
                || duck.isEating()
                || jumpAnimationState.isStarted()
                || quackAnimationState.isStarted()
                || hissAnimationState.isStarted()
                || attackAnimationState.isStarted()
                || downAnimationState.isStarted()
                || isBusy;

        if (isDoingAction) {
            this.idleAnimationState.stop();
            this.idleAnimationTimeout = 0;
        } else {
            if (this.idleAnimationTimeout <= 0) {
                this.idleAnimationTimeout = duck.getRandom().nextInt(40) + 80;
                this.idleAnimationState.start(duck.tickCount);
            } else {
                --this.idleAnimationTimeout;
            }
        }
    }

    public void triggerHissAnimation() {
        if (duck.level().isClientSide) {
            hissAnimationTimeout = 50;
            hissAnimationState.start(duck.tickCount);
        }
    }

    private boolean isMoving() {
        return duck.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6;
    }

    private boolean isRunning() {
        return duck.getDeltaMovement().horizontalDistanceSqr() > 0.01 &&
                !duck.isDuckSitting() && !duck.isPreparingNest() && !duck.isOrderedToSit();
    }

    public void updateWalkAnimation(float pPartialTick) {
        float f;
        if (duck.getPose() == Pose.STANDING && !duck.isDuckSitting() &&
                !duck.isPreparingNest() && !jumpAnimationState.isStarted() && !duck.isOrderedToSit()) {
            f = Math.min(pPartialTick * 6F, 1f);
        } else {
            f = 0f;
        }
        duck.walkAnimation.update(f, 0.2f);
    }
}