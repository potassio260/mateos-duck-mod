package net.mateo.duckmod.entity.behavior;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.mateo.duckmod.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;

public class DuckSoundBehavior {
    private final DuckEntity duck;
    private int soundTimer = 0;

    public DuckSoundBehavior(DuckEntity duck) {
        this.duck = duck;
        this.soundTimer = 200 + duck.getRandom().nextInt(200);
    }

    public void tick() {
        soundTimer--;

        if (soundTimer <= 0 && !duck.isPreparingNest() && !duck.isEating()) {
            SoundEvent quackSound = getQuackSound();
            duck.level().playSound(
                    null,
                    duck.getX(), duck.getY(), duck.getZ(),
                    quackSound,
                    net.minecraft.sounds.SoundSource.NEUTRAL,
                    1.0F,
                    1.0F + (duck.getRandom().nextFloat() - duck.getRandom().nextFloat()) * 0.2F
            );
            soundTimer = 200 + duck.getRandom().nextInt(200);
        }
    }

    private SoundEvent getQuackSound() {
        if (duck.isMale()) {
            int variant = duck.getRandom().nextInt(3);
            return switch (variant) {
                case 1 -> ModSounds.MALE_QUACK_2.get();
                case 2 -> ModSounds.MALE_QUACK_3.get();
                default -> ModSounds.MALE_QUACK_1.get();
            };
        } else {
            int variant = duck.getRandom().nextInt(2);
            return variant == 0 ? ModSounds.FEMALE_QUACK_1.get() : ModSounds.FEMALE_QUACK_2.get();
        }
    }

    public SoundEvent getHurtSound() {
        return duck.isMale() ? ModSounds.HIT_MALE_DUCK.get() : ModSounds.HIT_FEMALE_DUCK.get();
    }

    public SoundEvent getDeathSound() {
        // 5% chance for rare death sound
        if (duck.getRandom().nextFloat() < 0.05F) {
            return ModSounds.RARE_DEATH_SOUND.get();
        }

        if (duck.isMale()) {
            return duck.getRandom().nextBoolean() ?
                    ModSounds.DEATH_MALE_DUCK_1.get() : ModSounds.DEATH_MALE_DUCK_2.get();
        } else {
            return duck.getRandom().nextBoolean() ?
                    ModSounds.DEATH_FEMALE_DUCK_1.get() : ModSounds.DEATH_FEMALE_DUCK_2.get();
        }
    }
}