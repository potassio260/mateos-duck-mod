package net.mateo.duckmod.entity.behavior;

import net.mateo.duckmod.entity.custom.DuckEntity;
import net.mateo.duckmod.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class DuckInteractionBehavior {
    private final DuckEntity duck;
    private final Ingredient foodItems;

    private int breedingCooldown = 0;
    private Player breedingPlayer = null;

    public DuckInteractionBehavior(DuckEntity duck, Ingredient foodItems) {
        this.duck = duck;
        this.foodItems = foodItems;
    }

    public void tick() {
        // Handle eating animation completion
        if (duck.isEating()) {
            if (duck.getAnimationBehavior().gobbleAnimationTimeout > 0) {
                duck.getAnimationBehavior().gobbleAnimationTimeout--;
            }
            if (duck.getAnimationBehavior().gobbleAnimationTimeout <= 0) {
                duck.setEating(false);
            }
        }

        // Handle breeding after eating animation completes
        if (breedingCooldown > 0) {
            breedingCooldown--;
            if (breedingCooldown == 0 && breedingPlayer != null) {
                // Eating animation finished, now trigger breeding
                duck.setInLove(breedingPlayer);
                breedingPlayer = null;
            }
        }
    }

    public InteractionResult handleInteraction(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (duck.level().isClientSide) {
            boolean flag = duck.isOwnedBy(player) || duck.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        // TAMED DUCK INTERACTIONS
        if (duck.isTame()) {
            // Feed to heal
            if (foodItems.test(itemstack) && duck.getHealth() < duck.getMaxHealth()) {
                usePlayerItem(player, itemstack);
                duck.heal(2.0F);
                duck.ate();
                return InteractionResult.SUCCESS;
            }

            // Breeding - triggers after eating animation
            if (foodItems.test(itemstack) && duck.getAge() == 0 &&
                    canFallInLove() && !duck.isInLove()) {
                usePlayerItem(player, itemstack);
                duck.ate();
                this.breedingPlayer = player;
                this.breedingCooldown = 40;
                return InteractionResult.SUCCESS;
            }

            // For sit/stand toggle, this is handled in DuckEntity.mobInteract
            return InteractionResult.PASS;
        }

        // WILD DUCK INTERACTIONS

        // Baby duck feeding (growth boost)
        if (duck.isBaby() && foodItems.test(itemstack)) {
            usePlayerItem(player, itemstack);
            ageUp((int)((float)(-duck.getAge()) * 0.1F));
            return InteractionResult.SUCCESS;
        }

        // WILD ADULT DUCK BREEDING
        if (foodItems.test(itemstack) && duck.getAge() == 0 &&
                canFallInLove() && !duck.isInLove()) {
            usePlayerItem(player, itemstack);
            duck.ate();
            this.breedingPlayer = player;
            this.breedingCooldown = 40;
            return InteractionResult.SUCCESS;
        }

        // Adult wild duck eating (if not breeding)
        if (foodItems.test(itemstack)) {
            usePlayerItem(player, itemstack);
            duck.ate();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void usePlayerItem(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private boolean canFallInLove() {
        return duck.getAge() == 0 && duck.getHealth() >= duck.getMaxHealth();
    }

    private void ageUp(int amount) {
        int currentAge = duck.getAge();
        int newAge = currentAge + amount;
        duck.setAge(newAge);
    }

    public void handleEating() {
        duck.setEating(true);
        duck.getAnimationBehavior().gobbleAnimationTimeout = 40;
        duck.level().playSound(null, duck.getX(), duck.getY(), duck.getZ(),
                ModSounds.DUCK_EATING.get(), duck.getSoundSource(), 1.0F, 1.0F);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("BreedingCooldown", this.breedingCooldown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.breedingCooldown = pCompound.getInt("BreedingCooldown");
    }
}