package net.mateo.duckmod.entity.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.duckmod.DuckMod;
import net.mateo.duckmod.entity.client.model.*;
import net.mateo.duckmod.entity.custom.DuckEntity;
import net.mateo.duckmod.entity.variant.DuckVariant;
import net.minecraft.Util;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.Map;

public class DuckRenderer extends MobRenderer<DuckEntity, HierarchicalModel<DuckEntity>> {

    private static final ResourceLocation DONALD_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/donald_duck.png");

    private final HierarchicalModel<DuckEntity> donaldModel;

    // Store different models for each variant
    private final Map<DuckVariant, HierarchicalModel<DuckEntity>> variantModels = new EnumMap<>(DuckVariant.class);

    // Baby texture (same for all variants)
    private static final Map<DuckVariant, ResourceLocation> BABY_TEXTURE =
            Util.make(Maps.newEnumMap(DuckVariant.class), (map) -> {
                map.put(DuckVariant.MALLARD, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/mallard_baby.png"));
                map.put(DuckVariant.CALL, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/call_baby.png"));
                map.put(DuckVariant.WOOD, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/wood_baby.png"));
                map.put(DuckVariant.CRESTED, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/crested_baby.png"));
                map.put(DuckVariant.RUNNER, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/runner_baby.png"));
            });

    // Male textures by variant
    private static final Map<DuckVariant, ResourceLocation> MALE_TEXTURES =
            Util.make(Maps.newEnumMap(DuckVariant.class), (map) -> {
                map.put(DuckVariant.MALLARD, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/mallard_male.png"));
                map.put(DuckVariant.CALL, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/call_male.png"));
                map.put(DuckVariant.WOOD, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/wood_male.png"));
                map.put(DuckVariant.CRESTED, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/crested_male.png"));
                map.put(DuckVariant.RUNNER, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/runner_male.png"));
            });

    // Female textures by variant
    private static final Map<DuckVariant, ResourceLocation> FEMALE_TEXTURES =
            Util.make(Maps.newEnumMap(DuckVariant.class), (map) -> {
                map.put(DuckVariant.MALLARD, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/mallard_female.png"));
                map.put(DuckVariant.CALL, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/call_female.png"));
                map.put(DuckVariant.WOOD, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/wood_female.png"));
                map.put(DuckVariant.CRESTED, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/crested_female.png"));
                map.put(DuckVariant.RUNNER, ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/runner_female.png"));
            });

    public DuckRenderer(EntityRendererProvider.Context pContext) {
        // Use Mallard as the default model for the constructor
        super(pContext, (HierarchicalModel<DuckEntity>) new MallardDuckModel(pContext.bakeLayer(ModModelLayers.MALLARD_DUCK)), 0.25f);
        this.donaldModel = new DonaldDuckModel(pContext.bakeLayer(ModModelLayers.DONALD_DUCK));

        // Initialize models for each variant
        variantModels.put(DuckVariant.MALLARD, new MallardDuckModel(pContext.bakeLayer(ModModelLayers.MALLARD_DUCK)));
        variantModels.put(DuckVariant.CALL, new CallDuckModel(pContext.bakeLayer(ModModelLayers.CALL_DUCK)));
        variantModels.put(DuckVariant.WOOD, new WoodDuckModel(pContext.bakeLayer(ModModelLayers.WOOD_DUCK)));
        variantModels.put(DuckVariant.CRESTED, new CrestedDuckModel(pContext.bakeLayer(ModModelLayers.CRESTED_DUCK)));
        variantModels.put(DuckVariant.RUNNER, new RunnerDuckModel(pContext.bakeLayer(ModModelLayers.RUNNER_DUCK)));
    }

    @Override
    public ResourceLocation getTextureLocation(DuckEntity pEntity) {
        DuckVariant variant = pEntity.getVariant();

        if (variant == DuckVariant.CALL && pEntity.hasCustomName() && "Donald".equals(pEntity.getCustomName().getString())) {
            return DONALD_TEXTURE;
        }

        // Babies use the variant-specific baby texture
        if (pEntity.isBaby()) {
            return BABY_TEXTURE.get(variant);
        }

        // Adults use variant + gender specific textures
        return pEntity.isMale() ? MALE_TEXTURES.get(variant) : FEMALE_TEXTURES.get(variant);
    }

    @Override
    public void render(DuckEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {

        DuckVariant variant = pEntity.getVariant();

        if (variant == DuckVariant.CALL && pEntity.hasCustomName() && "Donald".equals(pEntity.getCustomName().getString())) {
            this.model = donaldModel;
        } else {
            // Switch to the appropriate model based on variant if it's NOT Donald
            HierarchicalModel<DuckEntity> variantModel = variantModels.get(variant);
            if (variantModel != null) {
                this.model = variantModel;
            }
        }

        if (pEntity.isBaby()) {
            pPoseStack.scale(0.5f, 0.5f, 0.5f);

            // Raise baby ducks by 2 pixels (0.125 blocks) when in water to prevent excessive sinking
            if (pEntity.isInWaterOrRain()) {
                pPoseStack.translate(0.0D, 0.125D, 0.0D);
            }
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}