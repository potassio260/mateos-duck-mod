package net.mateo.robomod.mixin.toughasnails;

import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.thirst.ThirstHooks;
import net.mateo.robomod.extension.PlayerExtension;

import java.lang.reflect.Field;

@Mixin(ThirstHooks.class)
public class ThirstHooksMixin {

    /**
     * FIX: data.foodTickTimer is package-private in FoodData and cannot be @Shadow-ed
     * from a Mixin targeting a different class (ThirstHooks, not FoodData).
     *
     * Solution: use reflection to access the field.
     * The field name "foodTickTimer" is correct under Forge 1.20.1 official mappings.
     *
     * Alternative: create a separate @Mixin(FoodData.class) with an @Accessor interface.
     */
    private static final Field FOOD_TICK_TIMER_FIELD;
    static {
        Field f = null;
        try {
            f = FoodData.class.getDeclaredField("foodTickTimer");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            // If mappings differ, try the SRG/intermediate name
            try {
                f = FoodData.class.getDeclaredField("f_38739_"); // SRG name fallback
                f.setAccessible(true);
            } catch (NoSuchFieldException ignored) { }
        }
        FOOD_TICK_TIMER_FIELD = f;
    }

    @Inject(method = "doFoodDataTick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void update(FoodData data, Player player, CallbackInfo ci) {
        if (player instanceof PlayerExtension ex && ex.isCyborg()) {

            if (ex.getEnergyStored() > 0 && FOOD_TICK_TIMER_FIELD != null) {
                if (player.getHealth() != player.getMaxHealth()) {
                    try {
                        int timer = (int) FOOD_TICK_TIMER_FIELD.get(data) + 1;
                        FOOD_TICK_TIMER_FIELD.set(data, timer);

                        if (timer >= 40) {
                            player.heal(1.0F);
                            if (!player.isCreative() && !player.isSpectator()) {
                                ex.setEnergyStored(Math.max(ex.getEnergyStored() - 20, 0));
                            }
                            FOOD_TICK_TIMER_FIELD.set(data, 0);
                        }
                    } catch (IllegalAccessException ignored) { }
                }
            }
            ci.cancel();
        }
    }
}