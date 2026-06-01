package net.mateo.robomod.item;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Creative mode tab registrations for RoboMod.
 *
 * Add items to the tab's displayItems consumer below.
 * In 1.20.1 Forge, creative tabs use the Registries.CREATIVE_MODE_TAB key.
 */
public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RoboMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ROBOMOD_TAB = CREATIVE_TABS.register("robomod",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + RoboMod.MOD_ID))
                    .icon(() -> new ItemStack(ModItems.CHRONOSTEEL_INGOT.get()))
                    .displayItems((parameters, output) -> {
                        // -------------------------------------------------------------------------
                        // Blocks
                        // -------------------------------------------------------------------------
                        output.accept(ModBlocks.ENERGY_GENERATOR.get());
                        output.accept(ModBlocks.ENERGY_WIRE.get());
                        output.accept(ModBlocks.ENERGY_RECEIVER.get());
                        output.accept(ModBlocks.BATTERY_TEST.get());
                        output.accept(ModBlocks.BATTERY_BLOCK.get());
                        output.accept(ModBlocks.SOLAR_PANEL.get());
                        output.accept(ModBlocks.ADVANCED_SOLAR_PANEL.get());
                        output.accept(ModBlocks.CHARGING_PAD.get());
                        output.accept(ModBlocks.SOLID_FUEL_GENERATOR.get());
                        output.accept(ModBlocks.CONTROLLER.get());
                        output.accept(ModBlocks.ASSEMBLER.get());

                        // -------------------------------------------------------------------------
                        // Items — modules
                        // -------------------------------------------------------------------------
                        output.accept(ModItems.FLIGHT_MODULE.get());
                        output.accept(ModItems.JETPACK_MODULE.get());
                        output.accept(ModItems.SOLAR_CELL_MODULE.get());
                        output.accept(ModItems.NIGHT_VISION_MODULE.get());
                        output.accept(ModItems.XRAY_VISION_MODULE.get());
                        output.accept(ModItems.RETREAT_MODULE.get());
                        output.accept(ModItems.EXTRA_HEALTH_MODULE.get());
                        output.accept(ModItems.MINING_GAUNTLETS_MODULE.get());
                        output.accept(ModItems.LONG_ARM_MODULE.get());
                        output.accept(ModItems.LARGE_BATTERY_MODULE.get());
                        output.accept(ModItems.EXTRA_BATTERY_MODULE.get());
                        output.accept(ModItems.CREATIVE_BATTERY_MODULE.get());

                        // -------------------------------------------------------------------------
                        // Cyborg parts
                        // -------------------------------------------------------------------------
                        output.accept(ModItems.BASIC_HEAD.get());
                        output.accept(ModItems.BASIC_BODY.get());
                        output.accept(ModItems.BASIC_ARM.get());
                        output.accept(ModItems.BASIC_LEG.get());
                        output.accept(ModItems.GOLDEN_HEAD.get());
                        output.accept(ModItems.GOLDEN_BODY.get());
                        output.accept(ModItems.GOLDEN_ARM.get());
                        output.accept(ModItems.GOLDEN_LEG.get());
                        output.accept(ModItems.ADVANCED_HEAD.get());
                        output.accept(ModItems.ADVANCED_BODY.get());
                        output.accept(ModItems.ADVANCED_ARM.get());
                        output.accept(ModItems.ADVANCED_LEG.get());

                        // -------------------------------------------------------------------------
                        // Misc
                        // -------------------------------------------------------------------------
                        output.accept(ModItems.CHRONOSTEEL_INGOT.get());
                        output.accept(ModItems.BATTERY.get());
                        output.accept(ModItems.SOLAR_CELL.get());
                        output.accept(ModItems.DEBUG_ENERGY_STICK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}