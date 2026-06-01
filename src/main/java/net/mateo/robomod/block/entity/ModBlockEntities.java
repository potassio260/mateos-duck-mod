package net.mateo.robomod.block.entity;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.ModBlocks;
import net.mateo.robomod.block.SolarPanelBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

/**
 * Forge port of ModBlockEntities.
 *
 * Fabric: Registry.register(Registries.BLOCK_ENTITY_TYPE, id, type)
 * Forge:  DeferredRegister<BlockEntityType<?>>  +  RegistryObject
 *
 * IEnergyStorage.SIDED.registerForBlockEntities() is removed — Forge Energy
 * capability is exposed via getCapability() in EnergyBlockEntity instead.
 */
public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RoboMod.MOD_ID);

    // -----------------------------------------------------------------------
    // Registrations
    // -----------------------------------------------------------------------

    public static final RegistryObject<BlockEntityType<EnergyGeneratorBlockEntity>> ENERGY_GENERATOR =
            BLOCK_ENTITY_TYPES.register("energy_generator",
                    () -> BlockEntityType.Builder
                            .of(EnergyGeneratorBlockEntity::new, ModBlocks.ENERGY_GENERATOR.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<EnergyWireBlockEntity>> ENERGY_WIRE =
            BLOCK_ENTITY_TYPES.register("energy_wire",
                    () -> BlockEntityType.Builder
                            .of(EnergyWireBlockEntity::new, ModBlocks.ENERGY_WIRE.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<EnergyTestReceiverBlockEntity>> ENERGY_RECEIVER =
            BLOCK_ENTITY_TYPES.register("energy_receiver",
                    () -> BlockEntityType.Builder
                            .of(EnergyTestReceiverBlockEntity::new, ModBlocks.ENERGY_RECEIVER.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<BatteryTestBlockEntity>> BATTERY_TEST =
            BLOCK_ENTITY_TYPES.register("battery_test",
                    () -> BlockEntityType.Builder
                            .of(BatteryTestBlockEntity::new, ModBlocks.BATTERY_TEST.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<EnergyBatteryBlockEntity>> BATTERY_BLOCK =
            BLOCK_ENTITY_TYPES.register("battery_block",
                    () -> BlockEntityType.Builder
                            .of(EnergyBatteryBlockEntity::new, ModBlocks.BATTERY_BLOCK.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITY_TYPES.register("solar_panel",
                    // Two blocks share the same block entity type — solar panel and advanced solar panel
                    () -> BlockEntityType.Builder
                            .of((pos, state) -> Objects.requireNonNull(((SolarPanelBlock) state.getBlock())
                                            .newBlockEntity(pos, state)),
                                    ModBlocks.SOLAR_PANEL.get(),
                                    ModBlocks.ADVANCED_SOLAR_PANEL.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<ChargingPadBlockEntity>> CHARGING_PAD =
            BLOCK_ENTITY_TYPES.register("charging_pad",
                    () -> BlockEntityType.Builder
                            .of(ChargingPadBlockEntity::new, ModBlocks.CHARGING_PAD.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<FurnaceGeneratorBlockEntity>> SOLID_FUEL_GENERATOR =
            BLOCK_ENTITY_TYPES.register("solid_fuel_generator",
                    () -> BlockEntityType.Builder
                            .of(FurnaceGeneratorBlockEntity::new, ModBlocks.SOLID_FUEL_GENERATOR.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<ControllerBlockEntity>> CONTROLLER =
            BLOCK_ENTITY_TYPES.register("controller",
                    () -> BlockEntityType.Builder
                            .of(ControllerBlockEntity::new, ModBlocks.CONTROLLER.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<AssemblerBlockEntity>> ASSEMBLER =
            BLOCK_ENTITY_TYPES.register("assembler",
                    () -> BlockEntityType.Builder
                            .of(AssemblerBlockEntity::new, ModBlocks.ASSEMBLER.get())
                            .build(null));

    // -----------------------------------------------------------------------
    // Called from RoboMod constructor
    // -----------------------------------------------------------------------
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
