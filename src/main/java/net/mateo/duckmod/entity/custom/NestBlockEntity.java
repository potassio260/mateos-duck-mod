package net.mateo.duckmod.entity.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.mateo.duckmod.block.custom.NestBlock;
import net.mateo.duckmod.entity.ModEntities;
import net.mateo.duckmod.entity.variant.DuckVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class NestBlockEntity extends BlockEntity {
    private static final int MAX_HEAT = 100;
    private static final int MIN_HATCH_TIME = 6000; // 5 minutes (6000 ticks)
    private static final int MAX_HATCH_TIME = 9600; // 8 minutes (9600 ticks)

    private int heat = 50;
    private int tickCounter = 0;
    private UUID lastEntityUUID = null;
    private boolean isGooseNest = false;

    // Track each egg separately for parallel hatching
    private List<EggData> eggs = new ArrayList<>();

    // Queue of variant IDs set by the nesting behavior when a duck lays an egg.
    // Consumed in initializeEggs() so each egg gets the correct variant.
    private List<Integer> pendingVariantIds = new ArrayList<>();

    private static class EggData {
        int hatchTimer = 0;
        int targetHatchTime = -1;
        int variantId = 0; // Variant of the duck/goose that laid this egg

        public EggData(int targetHatchTime, int variantId) {
            this.targetHatchTime = targetHatchTime;
            this.variantId = variantId;
        }
    }

    public NestBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.NEST_BLOCK_ENTITY.get(), pos, state);
    }

    public void queueEggVariant(int variantId) {
        pendingVariantIds.add(variantId);
        setChanged();
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        BlockState state = getBlockState();
        int eggCount = state.getValue(NestBlock.EGGS);

        // Sync eggs list size to block state without wiping existing eggs
        if (eggs.size() != eggCount) {
            syncEggList(eggCount);
        }

        // Only process if there are eggs
        if (eggCount == 0) {
            heat = 0;
            tickCounter = 0;
            lastEntityUUID = null;
            isGooseNest = false;
            eggs.clear();
            pendingVariantIds.clear();
            return;
        }

        // Increment tick counter
        tickCounter++;

        // Only update heat every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            boolean hasHeatSource = checkForHeatSources();

            int oldHeat = heat;
            if (hasHeatSource) {
                heat = Math.min(heat + 1, MAX_HEAT);
            } else {
                heat = Math.max(heat - 1, 0);
            }

            if (heat != oldHeat) {
                System.out.println("Nest at " + worldPosition + " - Heat: " + heat + "/" + MAX_HEAT);
            }
        }

        // Process all eggs in parallel
        if (heat > 0) {
            for (EggData egg : eggs) {
                egg.hatchTimer++;
            }

            List<Integer> eggsToHatch = new ArrayList<>();
            for (int i = 0; i < eggs.size(); i++) {
                EggData egg = eggs.get(i);
                if (egg.hatchTimer >= egg.targetHatchTime) {
                    eggsToHatch.add(i);
                }
            }

            if (!eggsToHatch.isEmpty()) {
                hatchEggsWithBabies(eggsToHatch.size());
                return;
            }
        } else if (heat == 0) {
            boolean wasIncubating = false;
            for (EggData egg : eggs) {
                if (egg.hatchTimer > 0) {
                    wasIncubating = true;
                    break;
                }
            }

            if (wasIncubating) {
                hatchAllEggsWithoutSpawn();
                return;
            }
        }

        setChanged();
    }

    private void syncEggList(int eggCount) {
        if (eggCount < eggs.size()) {
            // Eggs were removed — trim from the end, preserving existing timers and variants
            while (eggs.size() > eggCount) {
                eggs.remove(eggs.size() - 1);
            }
            System.out.println("Nest at " + worldPosition + " - Removed egg(s), now " + eggs.size() + " egg(s)");
        } else {
            // Eggs were added — append new entries, consuming the pending variant queue
            while (eggs.size() < eggCount) {
                int targetTime = MIN_HATCH_TIME + level.getRandom().nextInt(MAX_HATCH_TIME - MIN_HATCH_TIME + 1);
                int variantId = (!pendingVariantIds.isEmpty()) ? pendingVariantIds.remove(0) : 0;
                eggs.add(new EggData(targetTime, variantId));
                System.out.println("Nest at " + worldPosition + " - Added egg " + (eggs.size() - 1)
                        + " target: " + targetTime + " ticks, variant: " + variantId);
            }

            // Initialize heat only when going from 0 eggs to some eggs
            if (heat == 0 && eggCount > 0) {
                heat = 50;
                System.out.println("Nest at " + worldPosition + " - Heat initialized to: " + heat);
            }
        }
    }

    private boolean checkForHeatSources() {
        if (level == null) return false;

        // Check for light-producing blocks within 1 block range
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = worldPosition.offset(x, y, z);
                    BlockState checkState = level.getBlockState(checkPos);
                    if (checkState.getLightEmission() > 0) {
                        return true;
                    }
                }
            }
        }

        // Check for living entities standing ON TOP of the nest block ( in a 1 block range ) -> not 2 blocks on top
        AABB searchBox = new AABB(
                worldPosition.getX(),
                worldPosition.getY(),
                worldPosition.getZ(),
                worldPosition.getX() + 1.0,
                worldPosition.getY() + 1.0,
                worldPosition.getZ() + 1.0
        );
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchBox);

        if (!nearbyEntities.isEmpty()) {
            LivingEntity entity = nearbyEntities.get(0);

            if (entity instanceof GooseEntity) {
                isGooseNest = true;
            } else if (entity instanceof DuckEntity) {
                isGooseNest = false;
            }

            UUID newEntityUUID = entity.getUUID();
            if (lastEntityUUID == null || !lastEntityUUID.equals(newEntityUUID)) {
                lastEntityUUID = newEntityUUID;
                String entityType = entity instanceof Player // checks if the entity is a player to get it as a pet
                        ? "Player: " + ((Player) entity).getName().getString()
                        : entity.getType().toString();
                System.out.println("Entity detected on nest at " + worldPosition
                        + " - UUID: " + lastEntityUUID + " - Type: " + entityType + " is now the owner");
            }

            System.out.println("Living entity detected on top of nest at " + worldPosition
                    + " - Type: " + entity.getType().toString());
            return true;
        }

        return false;
    }

    public void addHeat(int amount) { // Adds heat to nest
        heat = Math.min(heat + amount, MAX_HEAT);
        setChanged();
    }

    private void hatchAllEggsWithoutSpawn() {
        if (level == null) return;

        BlockState state = getBlockState();

        System.out.println("Nest at " + worldPosition + " - Hatching all eggs without spawn (heat reached 0)");

        level.playSound(null, worldPosition, SoundEvents.TURTLE_EGG_BREAK,
                SoundSource.BLOCKS, 0.7F, 0.9F + level.getRandom().nextFloat() * 0.2F);

        level.setBlock(worldPosition, state.setValue(NestBlock.EGGS, 0)
                .setValue(NestBlock.HATCH, 0), 3);

        eggs.clear();
        pendingVariantIds.clear();
        lastEntityUUID = null;
        isGooseNest = false;
    }

    private void hatchEggsWithBabies(int count) {
        if (level == null) return;

        BlockState state = getBlockState();
        int eggCount = state.getValue(NestBlock.EGGS);

        String babyType = isGooseNest ? "goose(s)" : "duck(s)";
        System.out.println("Nest at " + worldPosition + " - Hatching " + count + " baby " + babyType);

        level.playSound(null, worldPosition, SoundEvents.TURTLE_EGG_BREAK,
                SoundSource.BLOCKS, 0.7F, 0.9F + level.getRandom().nextFloat() * 0.2F);

        for (int i = 0; i < count; i++) {
            // Each egg carries its own variant so babies always match their parent
            int eggVariantId = (!eggs.isEmpty()) ? eggs.get(0).variantId : 0;

            TamableAnimal baby = null;

            if (isGooseNest) {
                baby = ModEntities.GOOSE.get().create(level);
            } else {
                baby = ModEntities.DUCK.get().create(level);
                if (baby instanceof DuckEntity duckBaby) {
                    duckBaby.setVariant(DuckVariant.byId(eggVariantId));
                }
            }

            if (baby != null) {
                double xOffset = 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.3;
                double zOffset = 0.5 + (level.getRandom().nextDouble() - 0.5) * 0.3;

                baby.setPos(worldPosition.getX() + xOffset, worldPosition.getY() + 0.3, worldPosition.getZ() + zOffset);
                baby.setBaby(true);

                if (baby instanceof DuckEntity duck) {
                    duck.setMale(level.getRandom().nextBoolean());
                } else if (baby instanceof GooseEntity goose) {
                    goose.setMale(level.getRandom().nextBoolean());
                }

                // Handle ownership
                if (lastEntityUUID != null) {
                    LivingEntity owner = null;

                    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition).inflate(50))) {
                        if (entity.getUUID().equals(lastEntityUUID)) {
                            owner = entity;
                            break;
                        }
                    }

                    if (owner != null) {
                        if (owner instanceof Player player) {
                            baby.tame(player);
                            baby.setOrderedToSit(false);
                            level.broadcastEntityEvent(baby, (byte) 7);
                            System.out.println("Baby " + babyType + " tamed to player: " + player.getName().getString());
                        } else if (owner instanceof DuckEntity parentDuck) {
                            System.out.println("Baby hatched with parent duck: " + parentDuck.getUUID());
                            if (parentDuck.isTame() && parentDuck.getOwner() instanceof Player parentOwner) {
                                baby.tame(parentOwner);
                                baby.setOrderedToSit(false);
                                level.broadcastEntityEvent(baby, (byte) 7);
                                System.out.println("Baby " + babyType + " tamed to parent's owner: " + parentOwner.getName().getString());
                            } else {
                                System.out.println("Parent duck is wild, baby will be wild too");
                            }
                        } else if (owner instanceof GooseEntity parentGoose) {
                            System.out.println("Baby hatched with parent goose: " + parentGoose.getUUID());
                            if (parentGoose.isTame() && parentGoose.getOwner() instanceof Player parentOwner) {
                                baby.tame(parentOwner);
                                baby.setOrderedToSit(false);
                                level.broadcastEntityEvent(baby, (byte) 7);
                                System.out.println("Baby " + babyType + " tamed to parent's owner: " + parentOwner.getName().getString());
                            } else {
                                System.out.println("Parent goose is wild, baby will be wild too");
                            }
                        } else {
                            System.out.println("Baby hatched with entity type: " + owner.getType().toString());
                        }
                    } else {
                        System.out.println("Entity with UUID " + lastEntityUUID + " not found in world");
                    }
                }

                level.addFreshEntity(baby);
                String genderStr = "";
                if (baby instanceof DuckEntity duck) {
                    genderStr = duck.isMale() ? "Male" : "Female";
                } else if (baby instanceof GooseEntity goose) {
                    genderStr = goose.isMale() ? "Male" : "Female";
                }
                System.out.println("Baby " + babyType + " spawned at " + worldPosition
                        + " - Gender: " + genderStr + " - Variant ID: " + eggVariantId);
            }

            // Remove the egg we just hatched
            if (!eggs.isEmpty()) {
                eggs.remove(0);
            }
        }

        int newEggCount = Math.max(0, eggCount - count);
        level.setBlock(worldPosition, state.setValue(NestBlock.EGGS, newEggCount)
                .setValue(NestBlock.HATCH, 0), 3);

        if (newEggCount == 0) {
            lastEntityUUID = null;
            isGooseNest = false;
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Heat", heat);
        tag.putInt("TickCounter", tickCounter);
        tag.putBoolean("IsGooseNest", isGooseNest);

        if (lastEntityUUID != null) {
            tag.putUUID("LastEntityUUID", lastEntityUUID);
        }

        // Save egg data
        ListTag eggList = new ListTag();
        for (EggData egg : eggs) {
            CompoundTag eggTag = new CompoundTag();
            eggTag.putInt("HatchTimer", egg.hatchTimer);
            eggTag.putInt("TargetHatchTime", egg.targetHatchTime);
            eggTag.putInt("VariantId", egg.variantId); // NEW
            eggList.add(eggTag);
        }
        tag.put("Eggs", eggList);

        // Save pending variant queue so it survives a reload
        ListTag pendingList = new ListTag();
        for (int variantId : pendingVariantIds) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("VariantId", variantId);
            pendingList.add(entry);
        }
        tag.put("PendingVariants", pendingList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heat = tag.getInt("Heat");
        tickCounter = tag.getInt("TickCounter");
        isGooseNest = tag.getBoolean("IsGooseNest");

        if (tag.hasUUID("LastEntityUUID")) {
            lastEntityUUID = tag.getUUID("LastEntityUUID");
        }

        // Load egg data
        eggs.clear();
        ListTag eggList = tag.getList("Eggs", 10);
        for (int i = 0; i < eggList.size(); i++) {
            CompoundTag eggTag = eggList.getCompound(i);
            EggData egg = new EggData(eggTag.getInt("TargetHatchTime"), eggTag.getInt("VariantId"));
            egg.hatchTimer = eggTag.getInt("HatchTimer");
            eggs.add(egg);
        }

        // Load pending variant queue
        pendingVariantIds.clear();
        ListTag pendingList = tag.getList("PendingVariants", 10);
        for (int i = 0; i < pendingList.size(); i++) {
            pendingVariantIds.add(pendingList.getCompound(i).getInt("VariantId"));
        }
    }

    public int getHeat() {
        return heat;
    }
}