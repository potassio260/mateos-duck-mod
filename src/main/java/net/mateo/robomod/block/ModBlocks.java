package net.mateo.robomod.block;

import net.mateo.robomod.RoboMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * All block registrations for RoboMod.
 *
 * IMPORTANT: Replace each `new Block(...)` stub with your actual custom block class
 * (e.g. `new EnergyGeneratorBlock(...)`) once you provide those files.
 * The field names must stay exactly as they are — every other file references them.
 */
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RoboMod.MOD_ID);

    // -------------------------------------------------------------------------
    // Energy system blocks
    // -------------------------------------------------------------------------
    public static final RegistryObject<Block> ENERGY_GENERATOR = BLOCKS.register("energy_generator",
            () -> new EnergyGeneratorBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> ENERGY_WIRE = BLOCKS.register("energy_wire",
            () -> new EnergyWireBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(2.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> ENERGY_RECEIVER = BLOCKS.register("energy_receiver",
            () -> new EnergyReceiverBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    // -------------------------------------------------------------------------
    // Battery / storage blocks
    // -------------------------------------------------------------------------
    public static final RegistryObject<Block> BATTERY_TEST = BLOCKS.register("battery_test",
            () -> new BatteryTestBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> BATTERY_BLOCK = BLOCKS.register("battery_block",
            () -> new EnergyBatteryBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    // -------------------------------------------------------------------------
    // Solar panels
    // -------------------------------------------------------------------------
    public static final RegistryObject<Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(
                    10, 1000, // Added required int parameters (e.g., generation/capacity)
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE)
                            .requiresCorrectToolForDrops().strength(2.0F).sound(SoundType.GLASS)));

    public static final RegistryObject<Block> ADVANCED_SOLAR_PANEL = BLOCKS.register("advanced_solar_panel",
            () -> new SolarPanelBlock(
                    100, 10000, // Added required int parameters here as well
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE)
                            .requiresCorrectToolForDrops().strength(2.0F).sound(SoundType.GLASS)));

    // -------------------------------------------------------------------------
    // Machine blocks
    // -------------------------------------------------------------------------
    public static final RegistryObject<Block> CHARGING_PAD = BLOCKS.register("charging_pad",
            () -> new ChargingPadBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> SOLID_FUEL_GENERATOR = BLOCKS.register("solid_fuel_generator",
            () -> new FurnaceGeneratorBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> CONTROLLER = BLOCKS.register("controller",
            () -> new ControllerBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> ASSEMBLER = BLOCKS.register("assembler",
            () -> new AssemblerBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                            .requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));

    // -------------------------------------------------------------------------
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}