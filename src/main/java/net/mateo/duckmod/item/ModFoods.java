package net.mateo.duckmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    @SuppressWarnings("null")
    public static final FoodProperties RAW_DUCK = new FoodProperties.Builder().nutrition(2).meat()
            .saturationMod(0.2f).effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100), 0.3f).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200), 0.3f).build();

    @SuppressWarnings("null")
    public static final FoodProperties COOKED_DUCK = new FoodProperties.Builder().nutrition(8).meat()
            .saturationMod(0.2f).build();
}