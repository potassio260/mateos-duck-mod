package net.mateo.duckmod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = DuckMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config { // Read by the rest of the mod

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // -------------------------------------------------------------------------
    // Egg hatching
    // -------------------------------------------------------------------------
    private static final ForgeConfigSpec.IntValue MIN_HATCH_TIME_CFG = BUILDER
            .comment("Minimum ticks before a duck egg hatches (default: 6000 = 5 minutes)")
            .defineInRange("minHatchTimeTicks", 6000, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue MAX_HATCH_TIME_CFG = BUILDER
            .comment("Maximum ticks before a duck egg hatches (default: 9600 = 8 minutes)")
            .defineInRange("maxHatchTimeTicks", 9600, 1, Integer.MAX_VALUE);

    // -------------------------------------------------------------------------
    // Nest heat
    // -------------------------------------------------------------------------
    private static final ForgeConfigSpec.IntValue MAX_HEAT_CFG = BUILDER
            .comment("Maximum heat value a nest can reach (default: 100)")
            .defineInRange("nestMaxHeat", 100, 1, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue INITIAL_HEAT_CFG = BUILDER
            .comment("Heat value a nest starts with when eggs are first laid (default: 50)")
            .defineInRange("nestInitialHeat", 50, 0, Integer.MAX_VALUE);

    // -------------------------------------------------------------------------
    // Nest defense
    // -------------------------------------------------------------------------
    private static final ForgeConfigSpec.DoubleValue NEST_DEFENSE_RANGE_CFG = BUILDER
            .comment("Radius in blocks within which a mother duck will hiss at / attack players (default: 3.0)")
            .defineInRange("nestDefenseRange", 3.0, 0.0, 64.0);

    // -------------------------------------------------------------------------
    // Spawning
    // -------------------------------------------------------------------------
    private static final ForgeConfigSpec.BooleanValue DUCKS_SPAWN_NATURALLY_CFG = BUILDER
            .comment("Whether ducks spawn naturally in the world")
            .define("ducksSpawnNaturally", true);

    private static final ForgeConfigSpec.BooleanValue GEESE_SPAWN_NATURALLY_CFG = BUILDER
            .comment("Whether geese spawn naturally in the world")
            .define("geeseSpawnNaturally", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // -------------------------------------------------------------------------
    // Public values
    // -------------------------------------------------------------------------
    public static int minHatchTimeTicks;
    public static int maxHatchTimeTicks;
    public static int nestMaxHeat;
    public static int nestInitialHeat;
    public static double nestDefenseRange;
    public static boolean ducksSpawnNaturally;
    public static boolean geeseSpawnNaturally;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        minHatchTimeTicks    = MIN_HATCH_TIME_CFG.get();
        maxHatchTimeTicks    = MAX_HATCH_TIME_CFG.get();
        nestMaxHeat          = MAX_HEAT_CFG.get();
        nestInitialHeat      = INITIAL_HEAT_CFG.get();
        nestDefenseRange     = NEST_DEFENSE_RANGE_CFG.get();
        ducksSpawnNaturally  = DUCKS_SPAWN_NATURALLY_CFG.get();
        geeseSpawnNaturally  = GEESE_SPAWN_NATURALLY_CFG.get();
    }
}