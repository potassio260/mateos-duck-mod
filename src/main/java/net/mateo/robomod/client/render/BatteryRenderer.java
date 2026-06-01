package net.mateo.robomod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.EnergyBatteryBlock;
import net.mateo.robomod.block.entity.EnergyBatteryBlockEntity;
import net.mateo.robomod.client.ClientSetup;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class BatteryRenderer<T extends EnergyBatteryBlockEntity> implements BlockEntityRenderer<T> {

    ModelPart model;

    public BatteryRenderer(BlockEntityRendererProvider.Context ctx) {
        model = ctx.bakeLayer(ClientSetup.BATTERY_BLOCK_LAYER);
    }

    @Override
    public void render(T entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var level = entity.getLevel();

        if (level != null && !entity.isRemoved() && entity.getBlockState().getValue(EnergyBatteryBlock.LEVEL) > 0) {
            poseStack.pushPose();
            BlockState state = entity.getBlockState();

            poseStack.translate(0.5, 0.5, 0.5);

            Direction facing = state.getValue(EnergyBatteryBlock.FACING);

            if (facing.getAxis() == Direction.Axis.Y) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
            }
            if (facing.getAxis() == Direction.Axis.Z) {
                poseStack.mulPose(Axis.XN.rotationDegrees(180));
            }
            if (facing.getAxis() == Direction.Axis.X) {
                poseStack.mulPose(Axis.YN.rotationDegrees(180));
            }

            // Fabric's Direction.getRotationQuaternion() rotates "down" to point toward the facing direction.
            // Equivalent Forge rotations per direction:
            poseStack.mulPose(switch (facing) {
                case DOWN  -> Axis.XP.rotationDegrees(0);    // identity
                case UP    -> Axis.XP.rotationDegrees(180);
                case NORTH -> Axis.XP.rotationDegrees(90);
                case SOUTH -> Axis.XP.rotationDegrees(-90);
                case EAST  -> Axis.ZN.rotationDegrees(90);
                case WEST  -> Axis.ZP.rotationDegrees(90);
            });

            int level2 = state.getValue(EnergyBatteryBlock.LEVEL);
            var vertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucentEmissive(new ResourceLocation(RoboMod.MOD_ID,
                            "textures/entity/battery_block_overlay_" + level2 + ".png")));

            model.render(poseStack, vertexConsumer, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }
}
