package net.mateo.robomod.client.render.parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.AssemblerBlock;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.client.model.CyborgPartsModel;
import net.mateo.robomod.client.render.CyborgPartRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class LeftLegPartRenderer extends CyborgPartRenderer {

    public LeftLegPartRenderer(String name, String texture, Supplier<CyborgPartsModel> model) {
        super(name, texture, model);
    }

    @Override
    public void render(PlayerModel<?> contextModel, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, LivingEntity entity) {
        this.model.get().getLeftLeg().copyFrom(contextModel.leftLeg);
        contextModel.leftLeg.visible = false;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderType.entityCutoutNoCull(new ResourceLocation(RoboMod.MOD_ID, this.texture)));
        this.model.get().getLeftLeg().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public void renderAssembler(AssemblerBlockEntity assembler, BlockState state, float tickDelta,
                                PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        poseStack.pushPose();
        this.model.get().getLeftLeg().resetPose();
        this.model.get().getLeftLeg().x = 1.9F;
        this.model.get().getLeftLeg().y = -12;

        poseStack.translate(0.5, 1, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(AssemblerBlock.FACING).toYRot()));
        poseStack.scale(0.95f, 0.95f, 0.95f);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderType.entityCutoutNoCull(new ResourceLocation(RoboMod.MOD_ID, this.texture)));
        this.model.get().getLeftLeg().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}