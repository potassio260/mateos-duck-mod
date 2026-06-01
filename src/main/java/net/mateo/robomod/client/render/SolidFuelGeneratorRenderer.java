package net.mateo.robomod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.FurnaceGeneratorBlock;
import net.mateo.robomod.block.entity.FurnaceGeneratorBlockEntity;
import net.mateo.robomod.client.ClientSetup;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SolidFuelGeneratorRenderer<T extends FurnaceGeneratorBlockEntity> implements BlockEntityRenderer<T> {

    ModelPart model;

    public SolidFuelGeneratorRenderer(BlockEntityRendererProvider.Context ctx) {
        model = ctx.bakeLayer(ClientSetup.SOLID_FUEL_GENERATOR_LAYER);
    }

    @Override
    public void render(T entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var level = entity.getLevel();

        if (level != null && !entity.isRemoved() && isLit(entity)) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(FurnaceGeneratorBlock.FACING).toYRot()));

            var vertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucentEmissive(new ResourceLocation(RoboMod.MOD_ID, "textures/entity/solid_fuel_generator_overlay.png")));

            model.render(poseStack, vertexConsumer, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    boolean isLit(T entity) {
        return entity.getBlockState().getValue(FurnaceGeneratorBlock.LIT);
    }
}
