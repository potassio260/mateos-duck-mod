package net.mateo.duckmod.entity.ai;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.List;

public class DuckSwimToDeepWaterGoal extends Goal {
    private final DuckEntity duck;
    private final double speedModifier;
    private BlockPos targetPos;
    private int searchCooldown = 0;

    public DuckSwimToDeepWaterGoal(DuckEntity pDuck, double pSpeedModifier) {
        this.duck = pDuck;
        this.speedModifier = pSpeedModifier;
        this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.searchCooldown > 0) {
            --this.searchCooldown;
            return false;
        }

        // Reduced chance if in a group
        List<DuckEntity> nearby = this.duck.getNearbyDucks(8.0D);
        int chance = nearby.size() > 2 ? 200 : 150;

        if (!this.duck.isInWaterOrRain() || this.duck.getRandom().nextInt(chance) != 0) {
            return false;
        }

        // Find deeper water (center of lake)
        BlockPos duckPos = this.duck.blockPosition();
        BlockPos bestPos = null;
        int maxWaterDepth = 0;

        for (int attempt = 0; attempt < 15; attempt++) {
            int offsetX = this.duck.getRandom().nextInt(20) - 10;
            int offsetZ = this.duck.getRandom().nextInt(20) - 10;
            BlockPos checkPos = duckPos.offset(offsetX, 0, offsetZ);

            int waterDepth = this.getWaterDepth(checkPos);

            if (waterDepth > maxWaterDepth) {
                maxWaterDepth = waterDepth;
                bestPos = checkPos;
            }
        }

        if (bestPos != null && maxWaterDepth > 2) {
            this.targetPos = bestPos;
            return true;
        }

        return false;
    }

    private int getWaterDepth(BlockPos pos) {
        int depth = 0;
        BlockPos.MutableBlockPos checkPos = pos.mutable();

        for (int i = 0; i < 10; i++) {
            if (this.duck.level().getFluidState(checkPos).is(FluidTags.WATER)) {
                depth++;
                checkPos.move(0, -1, 0);
            } else {
                break;
            }
        }

        return depth;
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
        this.searchCooldown = 100 + this.duck.getRandom().nextInt(100);
        this.targetPos = null;
    }
}