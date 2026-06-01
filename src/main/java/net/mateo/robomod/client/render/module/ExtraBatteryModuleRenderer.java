package net.mateo.robomod.client.render.module;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.AssemblerBlock;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.client.model.module.ModuleModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.math.Axis;
import org.joml.Vector3f;

public class ExtraBatteryModuleRenderer extends ModuleRenderer {

    public ExtraBatteryModuleRenderer(String texture, ModuleModel model) {
        super(texture, model);
    }

    @Override
    public void render(PlayerModel<?> contextModel, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, LivingEntity entity) {
        this.model.getRoot().copyFrom(contextModel.body);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderType.entityCutout(new ResourceLocation(RoboMod.MOD_ID, this.texture)));
        this.model.getRoot().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public void renderAssembler(AssemblerBlockEntity assembler, BlockState state, float tickDelta,
                                PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        poseStack.pushPose();
        this.model.getRoot().resetPose();
        this.model.getRoot().y = -24;

        poseStack.translate(0.5, 1, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.getValue(AssemblerBlock.FACING).toYRot()));
        poseStack.scale(0.95f, 0.95f, 0.95f);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderType.entityCutout(new ResourceLocation(RoboMod.MOD_ID, this.texture)));
        this.model.getRoot().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}