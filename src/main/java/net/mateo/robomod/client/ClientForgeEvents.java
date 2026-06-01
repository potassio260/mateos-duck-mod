package net.mateo.robomod.client;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.client.render.OreHighlightRenderer;
import net.mateo.robomod.client.util.ClientOreHighlightData;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RoboMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEvents {

    // Replaces ClientTickEvents.END_CLIENT_TICK
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player instanceof PlayerExtension ex && !ex.isCyborg() && !ClientOreHighlightData.isEmpty()) {
            ClientOreHighlightData.clearHighlights();
        }
    }

    // Replaces WorldRenderEvents.LAST
    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            OreHighlightRenderer.renderOutlines(event);
        }
    }
}