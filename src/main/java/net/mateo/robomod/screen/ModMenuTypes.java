package net.mateo.robomod.screen;

import net.mateo.robomod.RoboMod;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RoboMod.MOD_ID);

    // IForgeMenuType.create() passes (containerId, playerInventory, FriendlyByteBuf)
    // so each menu class needs a matching (int, Inventory, FriendlyByteBuf) constructor.
    public static final RegistryObject<MenuType<FurnaceGeneratorMenu>> FURNACE_GENERATOR_MENU =
            MENUS.register("furnace_generator",
                    () -> IForgeMenuType.create(FurnaceGeneratorMenu::new));

    public static final RegistryObject<MenuType<AssemblerMenu>> ASSEMBLER_MENU =
            MENUS.register("assembler",
                    () -> IForgeMenuType.create(AssemblerMenu::new));

    // Aliases so ClientSetup.java references (FURNACE_GENERATOR_SCREEN / ASSEMBLER_SCREEN) still compile.
    // These point to the exact same RegistryObject — no extra registry entries.
    public static final RegistryObject<MenuType<FurnaceGeneratorMenu>> FURNACE_GENERATOR_SCREEN =
            FURNACE_GENERATOR_MENU;
    public static final RegistryObject<MenuType<AssemblerMenu>> ASSEMBLER_SCREEN =
            ASSEMBLER_MENU;

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}