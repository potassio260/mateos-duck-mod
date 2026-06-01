package net.mateo.duckmod.entity.client;

import net.mateo.duckmod.DuckMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation MALLARD_DUCK = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "mallard_duck"), "main");

    public static final ModelLayerLocation CALL_DUCK = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "call_duck"), "main");

    public static final ModelLayerLocation WOOD_DUCK = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "wood_duck"), "main");

    public static final ModelLayerLocation CRESTED_DUCK = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "crested_duck"), "main");

    public static final ModelLayerLocation RUNNER_DUCK = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "runner_duck"), "main");

    public static final ModelLayerLocation GOOSE = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "goose"), "main");

    public static final ModelLayerLocation DONALD_DUCK = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "donald_duck"), "main");
}