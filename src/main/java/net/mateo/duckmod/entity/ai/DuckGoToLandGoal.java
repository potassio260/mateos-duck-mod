package net.mateo.duckmod.entity.ai;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;

public class DuckGoToLandGoal extends Goal {
    private final DuckEntity duck;
    private final double speedModifier;
    private BlockPos targetPos;
    private int cooldown = 0;

    public DuckGoToLandGoal(DuckEntity pDuck, double pSpeedModifier) {
        this.duck = pDuck;
        this.speedModifier = pSpeedModifier;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }

        // Even lower chance if in a group
        List<DuckEntity> nearby = this.duck.getNearbyDucks(8.0D);
        int chance = nearby.size() > 2 ? 800 : 600;

        if (!this.duck.isInWaterOrRain() || this.duck.getRandom().nextInt(chance) != 0) {
            return false;
        }

        // Find nearby land
        BlockPos duckPos = this.duck.blockPosition();
        for (int i = 0; i < 10; i++) {
            int x = duckPos.getX() + this.duck.getRandom().nextInt(16) - 8;
            int z = duckPos.getZ() + this.duck.getRandom().nextInt(16) - 8;
            BlockPos checkPos = new BlockPos(x, duckPos.getY(), z);

            while (checkPos.getY() > this.duck.level().getMinBuildHeight()
                    && this.duck.level().isEmptyBlock(checkPos)) {
                checkPos = checkPos.below();
            }
            checkPos = checkPos.above();

            if (!this.duck.level().getFluidState(checkPos).is(FluidTags.WATER)
                    && !this.duck.level().getFluidState(checkPos.below()).is(FluidTags.WATER)
                    && this.duck.level().getBlockState(checkPos.below()).isSolid()) {
                this.targetPos = checkPos;
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        this.duck.getNavigation().moveTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), this.speedModifier);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.duck.getNavigation().isDone() && this.duck.isInWaterOrRain();
    }

    @Override
    public void stop() {
        this.cooldown = 400 + this.duck.getRandom().nextInt(400);
        this.targetPos = null;
    }
}