package net.mateo.robomod.client.render.debug;

import net.mateo.robomod.block.entity.EnergyBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;

public class DebugEnergyRenderer implements BlockEntityRenderer<EnergyBlockEntity> {

    public DebugEnergyRenderer(Block batteryTestTech, BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(EnergyBlockEntity entity, float partialTick, com.mojang.blaze3d.vertex.PoseStack poseStack,
                       net.minecraft.client.renderer.MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        DebugRender.DebugRender(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}