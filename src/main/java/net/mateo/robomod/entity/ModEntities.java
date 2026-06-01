package net.mateo.robomod.entity;

import net.mateo.robomod.RoboMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Entity type registrations for RoboMod.
 *
 * IMPORTANT: You must register CyborgEntity's attributes in RoboMod.java:
 *
 *   bus.addListener((EntityAttributeCreationEvent event) ->
 *       event.put(ModEntities.CYBORG_ENTITY.get(), CyborgEntity.createAttributes().build())
 *   );
 */
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RoboMod.MOD_ID);

    public static final RegistryObject<EntityType<CyborgEntity>> CYBORG_ENTITY =
            ENTITY_TYPES.register("cyborg",
                    () -> EntityType.Builder.<CyborgEntity>of(CyborgEntity::new, MobCategory.MISC)
                            .sized(0.6F, 1.8F)
                            .clientTrackingRange(8)
                            .build(RoboMod.MOD_ID + ":cyborg"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}