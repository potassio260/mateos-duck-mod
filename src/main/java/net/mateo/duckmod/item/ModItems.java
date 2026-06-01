package net.mateo.duckmod.item;

import net.mateo.duckmod.DuckMod;
import net.mateo.duckmod.entity.ModEntities;
import net.mateo.duckmod.item.custom.ThrowableEggItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DuckMod.MOD_ID);
    
    // Items
    @SuppressWarnings("null")
    public static final RegistryObject<Item> RAW_DUCK = ITEMS.register("raw_duck", () -> new Item(new Item.Properties().food(ModFoods.RAW_DUCK)));

    @SuppressWarnings("null")
    public static final RegistryObject<Item> COOKED_DUCK = ITEMS.register("cooked_duck", () -> new Item(new Item.Properties().food(ModFoods.COOKED_DUCK)));

    @SuppressWarnings("null")
    public static final RegistryObject<Item> DUCK_EGG = ITEMS.register("duck_egg", () -> new ThrowableEggItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static final RegistryObject<Item> DUCK_SPAWN_EGG = ITEMS.register("duck_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.DUCK, 0x2B1A17, 0x074710,
            new Item.Properties()));

    public static final RegistryObject<Item> GOOSE_SPAWN_EGG = ITEMS.register("goose_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.GOOSE, 0xFFFFFF, 0xD3D3D3,
            new Item.Properties()));
}
