package net.mateo.robomod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.client.model.CyborgModel;
import net.mateo.robomod.entity.CyborgEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class TestCyborgEntityRenderer extends GeoEntityRenderer<CyborgEntity> {

    public TestCyborgEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new CyborgModel());

        // FIX: Add the GeckoLib layer that renders swords/blocks in the robot's hands!
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, CyborgEntity animatable) {
                if (bone.getName().equals("right_arm")) {
                    return animatable.getMainHandItem();
                } else if (bone.getName().equals("left_arm")) {
                    return animatable.getOffhandItem();
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, CyborgEntity animatable) {
                return bone.getName().equals("right_arm") ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, CyborgEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.pushPose();
                // Adjust this downward so the sword is in the hand, not stuck in the shoulder pivot!
                poseStack.translate(0.0D, -0.6D, -0.1D);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                poseStack.popPose();
            }
        });
    }

    @Override
    protected int getBlockLightLevel(CyborgEntity entity, BlockPos pos) {
        try {
            if (entity.level() != null) {
                return super.getBlockLightLevel(entity, pos);
            }
        } catch (Exception ignored) {
        }
        return 15;
    }
}