package net.mateo.robomod.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.mateo.robomod.client.util.ClientOreHighlightData;
import net.mateo.robomod.item.XrayVisionModule;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;

/**
 * Subscribe renderOutlines(RenderLevelStageEvent) on the FORGE bus,
 * filtering on RenderLevelStageEvent.Stage.AFTER_PARTICLES (or whichever
 * stage matches the original Fabric WorldRenderEvents hook you used).
 *
 * Example:
 *   @SubscribeEvent
 *   public static void onRenderLevel(RenderLevelStageEvent event) {
 *       if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
 *           OreHighlightRenderer.renderOutlines(event);
 *       }
 *   }
 */
public class OreHighlightRenderer {

    private static final float OUTLINE_RED       = 1.0f;
    private static final float OUTLINE_GREEN      = 0.6f;
    private static final float OUTLINE_BLUE       = 0.2f;
    private static final float OUTLINE_THICKNESS  = 0.01f;

    public static void renderOutlines(RenderLevelStageEvent event) {
        ArrayList<net.minecraft.core.BlockPos> highlightedBlocks = ClientOreHighlightData.getHighlightedBlocks();
        if (highlightedBlocks.isEmpty()) return;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer  = tesselator.getBuilder();

        PoseStack poseStack = event.getPoseStack();
        if (poseStack == null) return;

        poseStack.pushPose();

        Camera camera = event.getCamera();
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        Matrix4f positionMatrix = poseStack.last().pose();

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for (net.minecraft.core.BlockPos pos : highlightedBlocks) {
            renderBlockOutline(buffer, positionMatrix, pos);
        }

        poseStack.popPose();

        setupRenderState();
        BufferUploader.drawWithShader(buffer.end());
        cleanupRenderState();
    }

    private static void setupRenderState() {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(3.0f);
    }

    private static void cleanupRenderState() {
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1.0f);
    }

    private static void renderBlockOutline(BufferBuilder buffer, Matrix4f matrix, net.minecraft.core.BlockPos pos) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        double offset = OUTLINE_THICKNESS;

        float r = OUTLINE_RED;
        float g = OUTLINE_GREEN;
        float b = OUTLINE_BLUE;
        float a = XrayVisionModule.getAlpha();

        // Bottom face
        vertex(buffer, matrix, x - offset,     y - offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y - offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y - offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y - offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y - offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y - offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y - offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y - offset, z - offset,     r, g, b, a);

        // Top face
        vertex(buffer, matrix, x - offset,     y + 1 + offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y + 1 + offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y + 1 + offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y + 1 + offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y + 1 + offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y + 1 + offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y + 1 + offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y + 1 + offset, z - offset,     r, g, b, a);

        // Vertical edges
        vertex(buffer, matrix, x - offset,     y - offset,     z - offset,     r, g, b, a);
        vertex(buffer, matrix, x - offset,     y + 1 + offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y - offset,     z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y + 1 + offset, z - offset,     r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y - offset,     z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x + 1 + offset, y + 1 + offset, z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y - offset,     z + 1 + offset, r, g, b, a);
        vertex(buffer, matrix, x - offset,     y + 1 + offset, z + 1 + offset, r, g, b, a);
    }

    private static void vertex(BufferBuilder buffer, Matrix4f matrix,
                                double x, double y, double z,
                                float r, float g, float b, float a) {
        buffer.vertex(matrix, (float) x, (float) y, (float) z).color(r, g, b, a).endVertex();
    }
}
