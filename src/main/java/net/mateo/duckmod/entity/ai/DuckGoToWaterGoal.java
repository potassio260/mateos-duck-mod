package net.mateo.duckmod.entity.ai;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;

import java.util.List;

public class DuckGoToWaterGoal extends MoveToBlockGoal {
    private final DuckEntity duck;

    public DuckGoToWaterGoal(DuckEntity pDuck, double pSpeedModifier) {
        super(pDuck, pSpeedModifier, 32);
        this.duck = pDuck;
    }

    @Override
    public boolean canUse() {
        // Lower chance if in a flock (ducks follow each other to water)
        List<DuckEntity> nearby = this.duck.getNearbyDucks(8.0D);
        int flockSize = nearby.size();

        // Adjust chance based on flock size
        int chance = flockSize > 2 ? 150 : 100;

        return !this.duck.isInWaterOrRain()
                && this.duck.getRandom().nextInt(chance) == 0
                && super.canUse();
    }

    @Override
    protected boolean isValidTarget(net.minecraft.world.level.LevelReader pLevel, BlockPos pPos) {
        return pLevel.getFluidState(pPos).is(FluidTags.WATER)
                && pLevel.getFluidState(pPos).isSource();
    }
}