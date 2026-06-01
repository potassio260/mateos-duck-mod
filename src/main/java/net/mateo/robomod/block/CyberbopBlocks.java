package net.mateo.robomod.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.item.ModItems;

public class CyberbopBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RoboMod.MOD_ID);

    // -------------------------------------------------------------------------
    // Block registrations
    // -------------------------------------------------------------------------

    public static final RegistryObject<Block> CHRONOSTEEL_BLOCK = BLOCKS.register("chronosteel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 4.0f)
                    .sound(SoundType.COPPER)));

    public static final RegistryObject<Block> CONTROLLER = BLOCKS.register("controller",
            () -> new ControllerBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 4.0f)));

    public static final RegistryObject<Block> ASSEMBLER = BLOCKS.register("assembler",
            () -> new AssemblerBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 4.0f)
                    .noOcclusion()));

    public static final RegistryObject<Block> ENERGY_WIRE = BLOCKS.register("energy_wire",
            () -> new EnergyWireBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final RegistryObject<Block> SOLAR_PANEL = BLOCKS.register("solar_panel",
            () -> new SolarPanelBlock(16000, 2, BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 4.0f)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> ADVANCED_SOLAR_PANEL = BLOCKS.register("advanced_solar_panel",
            () -> new SolarPanelBlock(32000, 8, BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 4.0f)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> CHARGING_PAD = BLOCKS.register("charging_pad",
            () -> new ChargingPadBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 4.0f)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> SOLID_FUEL_GENERATOR = BLOCKS.register("solid_fuel_generator",
            () -> new FurnaceGeneratorBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(2.0f, 4.0f)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> BATTERY_BLOCK = BLOCKS.register("battery_block",
            () -> new EnergyBatteryBlock(BlockBehaviour.Properties.of()
                    .strength(1.0f, 3.0f)));

    // Debug blocks
    public static final RegistryObject<Block> ENERGY_RECEIVER = BLOCKS.register("energy_receiver",
            () -> new EnergyReceiverBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 4.0f)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> ENERGY_GENERATOR = BLOCKS.register("energy_generator",
            () -> new EnergyGeneratorBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> BATTERY_TEST = BLOCKS.register("battery_test",
            () -> new BatteryTestBlock(BlockBehaviour.Properties.of()
                    .strength(2.0f, 4.0f)
                    .sound(SoundType.METAL)));

    // -------------------------------------------------------------------------
    // BlockItem registrations (handled via ModItems.ITEMS DeferredRegister)
    // Call this from ModItems so both registers are in one place, OR keep
    // it here. The pattern below registers BlockItems inside the Items register.
    // -------------------------------------------------------------------------

    public static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block) {
        return ModItems.ITEMS.register(name,
                () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // BlockItem entries — these live in the Items registry
    public static final RegistryObject<Item> CHRONOSTEEL_BLOCK_ITEM   = registerBlockItem("chronosteel_block",   CHRONOSTEEL_BLOCK);
    public static final RegistryObject<Item> CONTROLLER_ITEM           = registerBlockItem("controller",           CONTROLLER);
    public static final RegistryObject<Item> ASSEMBLER_ITEM            = registerBlockItem("assembler",            ASSEMBLER);
    public static final RegistryObject<Item> ENERGY_WIRE_ITEM          = registerBlockItem("energy_wire",          ENERGY_WIRE);
    public static final RegistryObject<Item> SOLAR_PANEL_ITEM          = registerBlockItem("solar_panel",          SOLAR_PANEL);
    public static final RegistryObject<Item> ADVANCED_SOLAR_PANEL_ITEM = registerBlockItem("advanced_solar_panel", ADVANCED_SOLAR_PANEL);
    public static final RegistryObject<Item> CHARGING_PAD_ITEM         = registerBlockItem("charging_pad",         CHARGING_PAD);
    public static final RegistryObject<Item> SOLID_FUEL_GENERATOR_ITEM = registerBlockItem("solid_fuel_generator", SOLID_FUEL_GENERATOR);
    public static final RegistryObject<Item> BATTERY_BLOCK_ITEM        = registerBlockItem("battery_block",        BATTERY_BLOCK);
    public static final RegistryObject<Item> ENERGY_RECEIVER_ITEM      = registerBlockItem("energy_receiver",      ENERGY_RECEIVER);
    public static final RegistryObject<Item> ENERGY_GENERATOR_ITEM     = registerBlockItem("energy_generator",     ENERGY_GENERATOR);
    public static final RegistryObject<Item> BATTERY_TEST_ITEM         = registerBlockItem("battery_test",         BATTERY_TEST);

    /**
     * Call this from your main mod constructor:
     *   CyberbopBlocks.BLOCKS.register(modEventBus);
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
