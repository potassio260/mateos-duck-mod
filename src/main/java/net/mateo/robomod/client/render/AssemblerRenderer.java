package net.mateo.robomod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.AssemblerBlock;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.client.ClientSetup;
import net.mateo.robomod.client.render.debug.DebugRender;
import net.mateo.robomod.item.AnimatableCyborgModule;
import net.mateo.robomod.item.CyborgModuleItem;
import net.mateo.robomod.util.CyborgPartType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Map;

public class AssemblerRenderer<T extends AssemblerBlockEntity> implements BlockEntityRenderer<T> {

    ModelPart model;
    ModelPart overlayPart;
    ModelPart basePart;
    ModelPart errorModel;

    final Map<CyborgPartType, Vec3> errorPosForPart = Map.of(
            CyborgPartType.HEAD,      new Vec3(0.5, 4.3, 0.5),
            CyborgPartType.BODY,      new Vec3(0.5, 3.7, 0.5),
            CyborgPartType.RIGHT_ARM, new Vec3(1,   3.8, 0.5),
            CyborgPartType.LEFT_ARM,  new Vec3(0,   3.8, 0.5),
            CyborgPartType.RIGHT_LEG, new Vec3(0.7, 3.1, 0.5),
            CyborgPartType.LEFT_LEG,  new Vec3(0.3, 3.1, 0.5)
    );

    public AssemblerRenderer(BlockEntityRendererProvider.Context ctx) {
        model       = ctx.bakeLayer(ClientSetup.ASSEMBLER_LAYER);
        overlayPart = model.getChild("overlay");
        basePart    = model.getChild("base");
        errorModel  = ctx.bakeLayer(ClientSetup.ERROR_LAYER);
    }

    @Override
    public void render(T entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var level = entity.getLevel();

        if (level != null && !entity.isRemoved()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(AssemblerBlock.FACING).toYRot()));

            var vertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucentCull(new ResourceLocation(RoboMod.MOD_ID, "textures/entity/assembler.png")));

            // Calculate light specifically for the block area directly above the pedestal.
            // This prevents the entire block entity from evaluating to 0 light if the base block is solid.
            int lightAbove = LevelRenderer.getLightColor(level, entity.getBlockPos().above());

            basePart.visible    = false;
            overlayPart.visible = false;

            // Use lightAbove instead of packedLight for the main assembler model
            model.render(poseStack, vertexConsumer, lightAbove, packedOverlay);

            poseStack.popPose();

            CyborgPartType.forEach(partType -> {
                CyborgPartRenderer renderer = CyborgPartRenderers.get(entity.getPartStack(partType), partType);
                if (renderer != null) {
                    renderer.renderAssembler(entity, entity.getBlockState(), partialTick, poseStack, bufferSource, lightAbove, packedOverlay);
                } else {
                    if (!entity.getPartStack(partType).isEmpty()) {
                        renderError(poseStack, partialTick, bufferSource, lightAbove, packedOverlay, errorPosForPart.get(partType), entity.tickError);
                    }
                }
            });

            for (ItemStack stack : entity.getItems()) {
                if (stack.getItem() instanceof CyborgModuleItem module) {
                    if (module.getModuleRenderer() != null)
                        module.getModuleRenderer().renderAssembler(entity, entity.getBlockState(), partialTick, poseStack, bufferSource, lightAbove, packedOverlay);
                }
                if (stack.getItem() instanceof AnimatableCyborgModule module) {
                    if (module.getModuleRenderer() != null)
                        module.getModuleRenderer().renderModuleAssembler(entity, entity.getBlockState(), partialTick, poseStack, bufferSource, lightAbove, packedOverlay);
                }
            }

            renderOverlay(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);

            // Dev-only debug render
            if (!FMLLoader.isProduction()) {
                DebugRender.DebugRender(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            }
        }
    }

    public void renderError(PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                            int packedLight, int packedOverlay, Vec3 pos, int tickError) {
        poseStack.pushPose();
        poseStack.translate(pos.x, pos.y, pos.z);
        poseStack.scale(1.6f, 1.6f, 1.6f);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(tickError * 5));

        var vertexConsumer = bufferSource.getBuffer(
                RenderType.entityCutout(new ResourceLocation(RoboMod.MOD_ID, "textures/entity/error_tex.png")));

        errorModel.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    public void renderOverlay(T entity, float partialTick, PoseStack poseStack,
                              MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(AssemblerBlock.FACING).toYRot()));

        var vertexConsumer = bufferSource.getBuffer(
                RenderType.entityTranslucentEmissive(new ResourceLocation(RoboMod.MOD_ID, "textures/entity/assembler_overlay.png")));

        basePart.visible    = false;
        overlayPart.visible = true;
        overlayPart.render(poseStack, vertexConsumer, 15728880, packedOverlay);

        poseStack.popPose();
    }
}