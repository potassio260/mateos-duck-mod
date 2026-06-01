package net.mateo.duckmod.event;

import net.mateo.duckmod.DuckMod;
import net.mateo.duckmod.entity.ModEntities;
import net.mateo.duckmod.entity.client.*;
import net.mateo.duckmod.entity.client.model.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DuckMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEntities.DUCK_EGG_PROJECTILE.get(), ThrownItemRenderer::new);
        });
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Register all duck variant model layers
        event.registerLayerDefinition(ModModelLayers.MALLARD_DUCK, MallardDuckModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CALL_DUCK, CallDuckModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.WOOD_DUCK, WoodDuckModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CRESTED_DUCK, CrestedDuckModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.RUNNER_DUCK, RunnerDuckModel::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.GOOSE, GooseModel::createBodyLayer);

        // NEW: Register the Donald Duck model layer!
        event.registerLayerDefinition(ModModelLayers.DONALD_DUCK, DonaldDuckModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DUCK.get(), DuckRenderer::new);
        event.registerEntityRenderer(ModEntities.GOOSE.get(), GooseRenderer::new);
    }
}