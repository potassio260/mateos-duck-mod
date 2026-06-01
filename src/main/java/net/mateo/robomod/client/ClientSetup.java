package net.mateo.robomod.client;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.ModBlocks;
import net.mateo.robomod.block.entity.ModBlockEntities;
import net.mateo.robomod.client.hud.RoboModHud;
import net.mateo.robomod.client.model.*;
import net.mateo.robomod.client.model.debug.ErrorModel;
import net.mateo.robomod.client.model.module.ExtraBatteryModuleModel;
import net.mateo.robomod.client.model.module.FlightModuleModel;
import net.mateo.robomod.client.model.module.JetpackModuleModel;
import net.mateo.robomod.client.render.*;
import net.mateo.robomod.client.render.debug.DebugEnergyRenderer;
import net.mateo.robomod.client.screen.AssemblerClientScreen;
import net.mateo.robomod.client.screen.FurnaceGeneratorClientScreen;
import net.mateo.robomod.entity.ModEntities;
import net.mateo.robomod.network.ModPackets;
import net.mateo.robomod.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RoboMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    public static final net.minecraft.client.model.geom.ModelLayerLocation BATTERY_BLOCK_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("battery_block"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation WIRES_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("wires"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation ASSEMBLER_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("assembler"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation CONTROLLER_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("controller"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation SOLID_FUEL_GENERATOR_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("solid_fuel_generator"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation ERROR_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("error"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation ADVANCED_CYBORG_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("advanced_cyborg"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation BASIC_CYBORG_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("basic_cyborg"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation JETPACK_MODULE_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("jetpack_module"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation FLIGHT_MODULE_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("flight_module"), "main");
    public static final net.minecraft.client.model.geom.ModelLayerLocation EXTRA_BATTERY_MODULE_LAYER =
            new net.minecraft.client.model.geom.ModelLayerLocation(RoboMod.id("extra_battery_module"), "main");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.FURNACE_GENERATOR_MENU.get(), FurnaceGeneratorClientScreen::new);
            MenuScreens.register(ModMenuTypes.ASSEMBLER_MENU.get(), AssemblerClientScreen::new);

            if (net.minecraftforge.fml.loading.FMLEnvironment.dist == Dist.CLIENT) {
                BlockEntityRenderers.register(ModBlockEntities.ASSEMBLER.get(),         ctx -> new AssemblerRenderer<>(ctx));
                BlockEntityRenderers.register(ModBlockEntities.CONTROLLER.get(),        ctx -> new ControllerRenderer<>(ctx));
                BlockEntityRenderers.register(ModBlockEntities.SOLID_FUEL_GENERATOR.get(), ctx -> new SolidFuelGeneratorRenderer<>(ctx));
                BlockEntityRenderers.register(ModBlockEntities.ENERGY_WIRE.get(),       ctx -> new WiresRenderer<>(ctx));
                BlockEntityRenderers.register(ModBlockEntities.BATTERY_BLOCK.get(),     ctx -> new BatteryRenderer<>(ctx));

                if (!net.minecraftforge.fml.loading.FMLEnvironment.production) {
                    BlockEntityRenderers.register(ModBlockEntities.ENERGY_GENERATOR.get(),
                            ctx -> new DebugEnergyRenderer(ModBlocks.ENERGY_GENERATOR.get(), ctx));
                    BlockEntityRenderers.register(ModBlockEntities.ENERGY_RECEIVER.get(),
                            ctx -> new DebugEnergyRenderer(ModBlocks.ENERGY_RECEIVER.get(), ctx));
                    BlockEntityRenderers.register(ModBlockEntities.SOLAR_PANEL.get(),
                            ctx -> new DebugEnergyRenderer(ModBlocks.SOLAR_PANEL.get(), ctx));
                    BlockEntityRenderers.register(ModBlockEntities.BATTERY_TEST.get(),
                            ctx -> new DebugEnergyRenderer(ModBlocks.BATTERY_TEST.get(), ctx));
                    BlockEntityRenderers.register(ModBlockEntities.CHARGING_PAD.get(),
                            ctx -> new DebugEnergyRenderer(ModBlocks.CHARGING_PAD.get(), ctx));
                    BlockEntityRenderers.register(ModBlockEntities.SOLID_FUEL_GENERATOR.get(),
                            ctx -> new DebugEnergyRenderer(ModBlocks.SOLID_FUEL_GENERATOR.get(), ctx));
                }
            }
        });

        ModPackets.registerClientReceivers();
        RoboModHud.init();
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BATTERY_BLOCK_LAYER,         BatteryModel::getTexturedModelData);
        event.registerLayerDefinition(WIRES_LAYER,                 WiresRenderer::getTexturedModelData);
        event.registerLayerDefinition(ASSEMBLER_LAYER,             AssemblerModel::getTexturedModelData);
        event.registerLayerDefinition(CONTROLLER_LAYER,            ControllerModel::getTexturedModelData);
        event.registerLayerDefinition(SOLID_FUEL_GENERATOR_LAYER,  CubeModel::getTexturedModelData);
        event.registerLayerDefinition(ERROR_LAYER,                 ErrorModel::getTexturedModelData);
        event.registerLayerDefinition(ADVANCED_CYBORG_LAYER,       AdvancedCyborgModel::getTexturedModelData);
        event.registerLayerDefinition(BASIC_CYBORG_LAYER,          BasicCyborgModel::getTexturedModelData);
        event.registerLayerDefinition(JETPACK_MODULE_LAYER,        JetpackModuleModel::getTexturedModelData);
        event.registerLayerDefinition(FLIGHT_MODULE_LAYER,         FlightModuleModel::getTexturedModelData);
        event.registerLayerDefinition(EXTRA_BATTERY_MODULE_LAYER,  ExtraBatteryModuleModel::getTexturedModelData);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CYBORG_ENTITY.get(), TestCyborgEntityRenderer::new);
    }
}