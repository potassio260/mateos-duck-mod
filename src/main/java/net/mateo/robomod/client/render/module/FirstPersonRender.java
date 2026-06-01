package net.mateo.robomod.client.render.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface FirstPersonRender {

    void renderLeftArm(ModelPart referenceModel, ItemStack stack, PlayerModel<?> contextModel, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float tickDelta);

    void renderRightArm(ModelPart referenceModel, ItemStack stack, PlayerModel<?> contextModel, PoseStack poseStack,
                        MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float tickDelta);
}