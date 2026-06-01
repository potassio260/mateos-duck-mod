package net.mateo.duckmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.duckmod.DuckMod;
import net.mateo.duckmod.entity.client.model.GooseModel;
import net.mateo.duckmod.entity.custom.GooseEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GooseRenderer extends MobRenderer<GooseEntity, GooseModel> {

    // Single texture for all geese
    private static final ResourceLocation GOOSE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(DuckMod.MOD_ID, "textures/entity/goose.png");

    public GooseRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new GooseModel(pContext.bakeLayer(ModModelLayers.GOOSE)), 0.1f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooseEntity pEntity) {
        return GOOSE_TEXTURE;
    }

    @Override
    public void render(GooseEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.scale(1.0f, 1.0f, 1.0f);

        if (pEntity.isBaby()) {
            pPoseStack.scale(0.5f, 0.5f, 0.5f);

            // Raise baby geese when in water
            if (pEntity.isInWaterOrRain()) {
                pPoseStack.translate(0.0D, 0.125D, 0.0D);
            }
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}