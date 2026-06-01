package net.mateo.robomod.item;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.ModBlocks;
import net.mateo.robomod.item.parts.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RoboMod.MOD_ID);

    // -------------------------------------------------------------------------
    // Block Items (Required for blocks to exist in the inventory/creative tab)
    // -------------------------------------------------------------------------
    public static final RegistryObject<Item> ENERGY_GENERATOR = ITEMS.register("energy_generator",
            () -> new BlockItem(ModBlocks.ENERGY_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> ENERGY_WIRE = ITEMS.register("energy_wire",
            () -> new BlockItem(ModBlocks.ENERGY_WIRE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ENERGY_RECEIVER = ITEMS.register("energy_receiver",
            () -> new BlockItem(ModBlocks.ENERGY_RECEIVER.get(), new Item.Properties()));

    public static final RegistryObject<Item> BATTERY_TEST = ITEMS.register("battery_test",
            () -> new BlockItem(ModBlocks.BATTERY_TEST.get(), new Item.Properties()));

    public static final RegistryObject<Item> BATTERY_BLOCK = ITEMS.register("battery_block",
            () -> new BlockItem(ModBlocks.BATTERY_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> SOLAR_PANEL = ITEMS.register("solar_panel",
            () -> new BlockItem(ModBlocks.SOLAR_PANEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> ADVANCED_SOLAR_PANEL = ITEMS.register("advanced_solar_panel",
            () -> new BlockItem(ModBlocks.ADVANCED_SOLAR_PANEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHARGING_PAD = ITEMS.register("charging_pad",
            () -> new BlockItem(ModBlocks.CHARGING_PAD.get(), new Item.Properties()));

    public static final RegistryObject<Item> SOLID_FUEL_GENERATOR = ITEMS.register("solid_fuel_generator",
            () -> new BlockItem(ModBlocks.SOLID_FUEL_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONTROLLER = ITEMS.register("controller",
            () -> new BlockItem(ModBlocks.CONTROLLER.get(), new Item.Properties()));

    public static final RegistryObject<Item> ASSEMBLER = ITEMS.register("assembler",
            () -> new BlockItem(ModBlocks.ASSEMBLER.get(), new Item.Properties()));

    // -------------------------------------------------------------------------
    // Modules
    // -------------------------------------------------------------------------
    public static final RegistryObject<Item> FLIGHT_MODULE = ITEMS.register("flight_module",
            () -> new FlightModule(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> CREATIVE_BATTERY_MODULE = ITEMS.register("creative_battery_module",
            () -> new CreativeBatteryModule(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> LARGE_BATTERY_MODULE = ITEMS.register("large_battery_module",
            () -> new BatteryModule(128000, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXTRA_BATTERY_MODULE = ITEMS.register("extra_battery_module",
            () -> new BatteryModule(32000, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> JETPACK_MODULE = ITEMS.register("jetpack_module",
            () -> new JetpackModule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SOLAR_CELL_MODULE = ITEMS.register("solar_cell_module",
            () -> new SolarCellModule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SOLAR_CELL = ITEMS.register("solar_cell",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NIGHT_VISION_MODULE = ITEMS.register("night_vision_module",
            () -> new NightVisionModule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> XRAY_VISION_MODULE = ITEMS.register("xray_vision_module",
            () -> new XrayVisionModule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> RETREAT_MODULE = ITEMS.register("retreat_module",
            () -> new RetreatModule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EXTRA_HEALTH_MODULE = ITEMS.register("extra_health_module",
            () -> new ExtendedHealthModule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MINING_GAUNTLETS_MODULE = ITEMS.register("mining_gauntlets_module",
            () -> new MiningGauntletsModule(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LONG_ARM_MODULE = ITEMS.register("long_arm_module",
            () -> new LongArmModule(new Item.Properties().stacksTo(1)));

    // -------------------------------------------------------------------------
    // Basic cyborg parts
    // -------------------------------------------------------------------------
    public static final RegistryObject<Item> BASIC_HEAD = ITEMS.register("basic_cyborg_head",
            () -> new CyborgHeadPartItem("basic_head", 5000, 3, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BASIC_BODY = ITEMS.register("basic_cyborg_body",
            () -> new CyborgBodyPartItem("basic_body", 5000, 5, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BASIC_ARM = ITEMS.register("basic_cyborg_arm",
            () -> new CyborgArmPartItem("basic_right_arm", "basic_left_arm", 3500, 1, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BASIC_LEG = ITEMS.register("basic_cyborg_leg",
            () -> new CyborgLegPartItem("basic_right_leg", "basic_left_leg", 3500, 1, new Item.Properties().stacksTo(1)));

    // -------------------------------------------------------------------------
    // Golden cyborg parts
    // -------------------------------------------------------------------------
    public static final RegistryObject<Item> GOLDEN_HEAD = ITEMS.register("golden_cyborg_head",
            () -> new CyborgHeadPartItem("golden_head", 8000, 4, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GOLDEN_BODY = ITEMS.register("golden_cyborg_body",
            () -> new CyborgBodyPartItem("golden_body", 8000, 6, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GOLDEN_ARM = ITEMS.register("golden_cyborg_arm",
            () -> new CyborgArmPartItem("golden_right_arm", "golden_left_arm", 5000, 1.5, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GOLDEN_LEG = ITEMS.register("golden_cyborg_leg",
            () -> new CyborgLegPartItem("golden_right_leg", "golden_left_leg", 5000, 1.5, new Item.Properties().stacksTo(1)));

    // -------------------------------------------------------------------------
    // Advanced cyborg parts
    // -------------------------------------------------------------------------
    public static final RegistryObject<Item> ADVANCED_HEAD = ITEMS.register("advanced_cyborg_head",
            () -> new AdvancedCyborgHead("advanced_head", 11350, 7, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ADVANCED_BODY = ITEMS.register("advanced_cyborg_body",
            () -> new AdvancedCyborgBody("advanced_body", 11350, 7, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ADVANCED_ARM = ITEMS.register("advanced_cyborg_arm",
            () -> new AdvancedCyborgArm("advanced_right_arm", "advanced_left_arm", 7350, 4, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ADVANCED_LEG = ITEMS.register("advanced_cyborg_leg",
            () -> new AdvancedCyborgLeg("advanced_right_leg", "advanced_left_leg", 7350, 4, new Item.Properties().stacksTo(1)));

    // -------------------------------------------------------------------------
    // Misc
    // -------------------------------------------------------------------------
    public static final RegistryObject<Item> CHRONOSTEEL_INGOT = ITEMS.register("chronosteel_ingot",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DEBUG_ENERGY_STICK = ITEMS.register("debug_energy_stick",
            () -> new DebugEnergyStick(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}