package net.mateo.robomod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.ControllerBlock;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.block.entity.ControllerBlockEntity;
import net.mateo.robomod.client.ClientSetup;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class ControllerRenderer<T extends ControllerBlockEntity> implements BlockEntityRenderer<T> {

    ModelPart model;

    Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public ControllerRenderer(BlockEntityRendererProvider.Context ctx) {
        model = ctx.bakeLayer(ClientSetup.CONTROLLER_LAYER);
    }

    @Override
    public void render(T entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var level = entity.getLevel();

        if (level != null && !entity.isRemoved() && connected(entity)) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(ControllerBlock.FACING).toYRot()));

            var vertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucentEmissive(new ResourceLocation(RoboMod.MOD_ID, "textures/entity/controller_overlay.png")));

            model.render(poseStack, vertexConsumer, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    boolean connected(T entity) {
        for (Direction direction : directions) {
            var assemblerPos = entity.getBlockPos().offset(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            var blockEntity  = entity.getLevel().getBlockEntity(assemblerPos);

            if (blockEntity instanceof AssemblerBlockEntity) return true;
        }
        return false;
    }
}
