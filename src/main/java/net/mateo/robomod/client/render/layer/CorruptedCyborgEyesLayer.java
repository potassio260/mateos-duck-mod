package net.mateo.robomod.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.entity.CyborgEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;

public class CorruptedCyborgEyesLayer<T extends CyborgEntity> extends GeoRenderLayer<T> {

    public CorruptedCyborgEyesLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    protected @Nullable RenderType getRenderType(T animatable, @Nullable MultiBufferSource bufferSource) {
        Entity entity = animatable;
        boolean invisible = entity.isInvisible();

        if (invisible && !entity.isInvisibleTo(Minecraft.getInstance().player)) {
            return RenderType.itemEntityTranslucentCull(this.getTextureResource(animatable));
        } else if (Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
            return invisible
                    ? RenderType.outline(this.getTextureResource(animatable))
                    : AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
        } else {
            return invisible ? null : AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
        }
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel,
                       @Nullable RenderType renderType, MultiBufferSource bufferSource,
                       @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        renderType = this.getRenderType(animatable, bufferSource);
        if (renderType == null) return;

        // CyborgEntity doesn't have isOff()/tickEye, so just use the renderer's color directly
        int argb = this.getRenderer().getRenderColor(animatable, partialTick, packedLight).argbInt();

        float alpha = ((argb >> 24) & 0xFF) / 255.0f;
        float red   = ((argb >> 16) & 0xFF) / 255.0f;
        float green = ((argb >>  8) & 0xFF) / 255.0f;
        float blue  = ( argb        & 0xFF) / 255.0f;
        if (alpha == 0.0f) alpha = 1.0f;

        this.getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                bufferSource.getBuffer(renderType), partialTick, LightTexture.FULL_BRIGHT, packedOverlay,
                red, green, blue, alpha);
    }
}