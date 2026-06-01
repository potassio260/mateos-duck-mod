package net.mateo.duckmod.entity;

import net.mateo.duckmod.DuckMod;
import net.mateo.duckmod.block.ModBlocks;
import net.mateo.duckmod.entity.custom.DuckEggProjectileEntity;
import net.mateo.duckmod.entity.custom.DuckEntity;
import net.mateo.duckmod.entity.custom.GooseEntity;
import net.mateo.duckmod.entity.custom.NestBlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    // Registry for living entities and projectiles
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DuckMod.MOD_ID);
    
    // Registry for block entities (separate!)
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DuckMod.MOD_ID);
    
    // Entity registrations
    public static final RegistryObject<EntityType<DuckEntity>> DUCK =
        ENTITY_TYPES.register("duck",
                () -> EntityType.Builder.<DuckEntity>of(DuckEntity::new, MobCategory.CREATURE)
                        .sized(1.00F, 1.00F)
                        .build("duck"));

    public static final RegistryObject<EntityType<GooseEntity>> GOOSE =
            ENTITY_TYPES.register("goose",
                    () -> EntityType.Builder.<GooseEntity>of(GooseEntity::new, MobCategory.CREATURE)
                            .sized(1.00F, 1.00F)
                            .build("goose"));

    public static final RegistryObject<EntityType<DuckEggProjectileEntity>> DUCK_EGG_PROJECTILE = 
        ENTITY_TYPES.register("duck_egg_projectile",
            () -> EntityType.Builder.<DuckEggProjectileEntity>of(DuckEggProjectileEntity::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build("duck_egg_projectile"));

    // Block entity registrations
    public static final RegistryObject<BlockEntityType<NestBlockEntity>> NEST_BLOCK_ENTITY = 
        BLOCK_ENTITY_TYPES.register("nest_block_entity", () ->
            BlockEntityType.Builder.of(NestBlockEntity::new, 
                ModBlocks.NEST_BLOCK.get()).build(null));
    
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}