package net.mateo.duckmod.entity.behavior;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DuckAngerBehavior {
    protected final DuckEntity duck;

    // Base alert interval - can be overridden by subclasses
    protected UniformInt alertInterval = TimeUtil.rangeOfSeconds(4, 6);
    protected int ticksUntilNextAlert;

    // Base anger duration - can be overridden by subclasses
    protected UniformInt persistentAngerTime = TimeUtil.rangeOfSeconds(20, 39);
    protected int remainingPersistentAngerTime;

    @Nullable
    protected UUID persistentAngerTarget;

    // Alert range - can be overridden by subclasses
    protected static final int ALERT_RANGE_Y = 10;

    // Whether this species alerts others when attacked
    protected boolean alertsOthersWhenAngry = true;

    public DuckAngerBehavior(DuckEntity duck) {
        this.duck = duck;
        configureAngerBehavior();
    }

    protected void configureAngerBehavior() {
        // Default duck behavior
        this.alertInterval = TimeUtil.rangeOfSeconds(4, 6);
        this.persistentAngerTime = TimeUtil.rangeOfSeconds(20, 39);
        this.alertsOthersWhenAngry = true;
    }

    protected Class<? extends DuckEntity> getAlertableEntityClass() {
        return duck.getClass();
    }

    public void tick(ServerLevel level) {
        duck.updatePersistentAnger(level, true);

        // Only alert others if NOT sitting on nest and if this species alerts others
        if (duck.getTarget() != null && !duck.isDuckSitting() && alertsOthersWhenAngry) {
            maybeAlertOthers();
        }

        if (duck.isAngry()) {
            duck.setLastHurtByMob(duck.getTarget());
        }
    }

    private void maybeAlertOthers() {
        if (ticksUntilNextAlert > 0) {
            --ticksUntilNextAlert;
        } else {
            if (duck.getSensing().hasLineOfSight(duck.getTarget())) {
                alertOthers();
            }
            ticksUntilNextAlert = alertInterval.sample(duck.getRandom());
        }
    }

    protected void alertOthers() {
        double range = duck.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB searchBox = AABB.unitCubeFromLowerCorner(duck.position()).inflate(range, ALERT_RANGE_Y, range);

        // Alert only entities of the same class (ducks alert ducks, geese alert geese)
        Class<? extends DuckEntity> alertClass = getAlertableEntityClass();

        duck.level().getEntitiesOfClass(alertClass, searchBox, otherDuck -> otherDuck != duck)
                .stream()
                .filter(otherDuck -> otherDuck.getTarget() == null)
                .filter(otherDuck -> !otherDuck.isAlliedTo(duck.getTarget()))
                .filter(otherDuck -> !otherDuck.isDuckSitting()) // Don't alert sitting ducks/geese
                .filter(this::shouldAlertEntity) // Additional filtering hook for subclasses
                .forEach(otherDuck -> {
                    otherDuck.setTarget(duck.getTarget());
                    otherDuck.startPersistentAngerTimer();
                });
    }

    protected boolean shouldAlertEntity(DuckEntity otherEntity) {
        return true; // Default: alert all valid entities
    }

    public void onSetTarget(@Nullable LivingEntity pTarget) {
        if (duck.getTarget() == null && pTarget != null) {
            ticksUntilNextAlert = alertInterval.sample(duck.getRandom());
        }

        if (pTarget instanceof Player) {
            duck.setLastHurtByPlayer((Player) pTarget);
        }
    }

    public void startPersistentAngerTimer() {
        setRemainingPersistentAngerTime(persistentAngerTime.sample(duck.getRandom()));
    }

    public void setRemainingPersistentAngerTime(int pTime) {
        this.remainingPersistentAngerTime = pTime;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@Nullable UUID pTarget) {
        this.persistentAngerTarget = pTarget;
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        duck.addPersistentAngerSaveData(pCompound);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        duck.readPersistentAngerSaveData(duck.level(), pCompound);
    }
}