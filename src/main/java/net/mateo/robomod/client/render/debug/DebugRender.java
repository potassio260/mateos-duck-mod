package net.mateo.robomod.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mateo.robomod.block.entity.EnergyBlockEntity;
import net.mateo.robomod.block.entity.EnergyWireBlockEntity;
import net.mateo.robomod.packet.DebugCablePacket;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;

import java.awt.Color;

public class DebugRender {

    /**
     * FIX: LevelRenderer.renderShape() has private access in 1.20.1.
     * Replicated here by iterating VoxelShape edges directly (same logic, just accessible).
     */
    private static void renderVoxelShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape,
                                         double offsetX, double offsetY, double offsetZ,
                                         float r, float g, float b, float a) {
        Matrix4f matrix = poseStack.last().pose();
        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            consumer.vertex(matrix, (float)(offsetX + x1), (float)(offsetY + y1), (float)(offsetZ + z1))
                    .color(r, g, b, a).normal(poseStack.last().normal(), 0, 1, 0).endVertex();
            consumer.vertex(matrix, (float)(offsetX + x2), (float)(offsetY + y2), (float)(offsetZ + z2))
                    .color(r, g, b, a).normal(poseStack.last().normal(), 0, 1, 0).endVertex();
        });
    }

    public static void DebugRenderWires(EnergyWireBlockEntity entity, BlockState state, PoseStack poseStack,
                                        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            PoseStack matrixStack = new PoseStack();
            matrixStack.pushPose();

            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            if (entity == entity.getLevel().getBlockEntity(blockPos)) {
                Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
                VertexConsumer lineBuffer = bufferSource.getBuffer(RenderType.lines());

                renderVoxelShape(matrixStack, lineBuffer,
                        state.getShape(entity.getLevel(), blockPos, CollisionContext.of(Minecraft.getInstance().player)),
                        entity.getBlockPos().getX() - camera.getPosition().x,
                        entity.getBlockPos().getY() - camera.getPosition().y,
                        entity.getBlockPos().getZ() - camera.getPosition().z,
                        1.0F, 0.0F, 0.0F, 1.0F);

                if (!DebugCablePacket.debugCables.isEmpty()) {
                    for (BlockPos cable : DebugCablePacket.debugCables) {
                        BlockState blockState = entity.getLevel().getBlockState(cable);
                        renderVoxelShape(matrixStack, lineBuffer,
                                blockState.getShape(entity.getLevel(), cable, CollisionContext.of(Minecraft.getInstance().player)),
                                cable.getX() - camera.getPosition().x,
                                cable.getY() - camera.getPosition().y,
                                cable.getZ() - camera.getPosition().z,
                                0.0F, 1.0F, 0.0F, 1.0F);
                    }

                    if (DebugCablePacket.ownerCable != null) {
                        renderVoxelShape(matrixStack, lineBuffer,
                                entity.getLevel().getBlockState(DebugCablePacket.ownerCable)
                                        .getShape(entity.getLevel(), DebugCablePacket.ownerCable,
                                                CollisionContext.of(Minecraft.getInstance().player)),
                                DebugCablePacket.ownerCable.getX() - camera.getPosition().x,
                                DebugCablePacket.ownerCable.getY() - camera.getPosition().y,
                                DebugCablePacket.ownerCable.getZ() - camera.getPosition().z,
                                0.0F, 0.0F, 1.0F, 1.0F);
                    }
                }
                matrixStack.popPose();
            }
        }

        assert Minecraft.getInstance().player != null;
        if (Minecraft.getInstance().player.tickCount % 21 == 20) {
            DebugCablePacket.debugCables.clear();
            DebugCablePacket.ownerCable = null;
        }
    }

    public static void DebugRender(EnergyBlockEntity entity, float partialTick, PoseStack poseStack,
                                   MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);

        String text = "Stored:" + entity.energyStorage.storedEnergy;
        int width = Minecraft.getInstance().font.width(text);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(camera.rotation());

        float f = (text.length() > 6) ? 0.010F : 0.014F;
        poseStack.scale(f, -f, f);

        Matrix4f matrix4f = poseStack.last().pose();

        long seed = BlockPos.asLong(entity.getBlockPos().getX(), entity.getBlockPos().getY(), entity.getBlockPos().getZ());
        RandomSource random  = RandomSource.create(seed);
        RandomSource random1 = RandomSource.create(seed * 2);
        RandomSource random2 = RandomSource.create(seed * 3);

        Minecraft.getInstance().font.drawInBatch(
                text,
                (float) -width / 2,
                0.0F,
                new Color(random.nextInt(256), random1.nextInt(256), random2.nextInt(256)).getRGB(),
                true,
                matrix4f,
                bufferSource,
                Font.DisplayMode.SEE_THROUGH,
                new Color(0, 0, 0, 0).getRGB(),
                15728880);

        poseStack.popPose();
    }
}