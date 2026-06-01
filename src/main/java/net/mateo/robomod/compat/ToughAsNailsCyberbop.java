package net.mateo.robomod.compat;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.api.thirst.ThirstHelper;

@Mod.EventBusSubscriber(modid = RoboMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ToughAsNailsCyberbop {

    public static boolean initialized = false;

    // Replaces ServerLifecycleEvents.SERVER_STARTING
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        if (!initialized) {
            TemperatureHelper.registerPlayerTemperatureModifier((player, temperatureLevel) -> {
                if (player instanceof PlayerExtension ex && ex.isCyborg()) {
                    return TemperatureLevel.NEUTRAL;
                }
                return temperatureLevel;
            });
            initialized = true;
        }
    }

    // Replaces ServerTickEvents.END_SERVER_TICK
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof PlayerExtension ex
                    && ThirstHelper.getThirst(player) instanceof ThirstDataExtension tde) {
                tde.setCyborg(ex.isCyborg());
            }
        });
    }
}