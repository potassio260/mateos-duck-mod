package net.mateo.robomod.client.render.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.client.model.DefaultedModuleGeoModel;
import net.mateo.robomod.item.LongArmModule;
import net.mateo.robomod.item.ModItems;
import net.mateo.robomod.util.RenderUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class LongArmModuleRenderer extends AnimatableModuleRenderer<LongArmModule> implements FirstPersonRender {

    public LongArmModuleRenderer() {
        super(new DefaultedModuleGeoModel<>(new ResourceLocation(RoboMod.MOD_ID, "long_arm")));
    }

    @Override
    public void renderModule(ItemStack stack, PlayerModel<?> contextModel, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float tickDelta) {
        this.animatable = (LongArmModule) stack.getItem();
        if (entity instanceof Player player) {
            if (player.getMainArm() == HumanoidArm.RIGHT) {
                RenderUtils.setPositionGeoBone(this.getGeoModel().getBone("root"), 0, 0, 0, 22,
                        contextModel.crouching ? 3.2f : 0, contextModel.rightArm, 0, 90, 0,
                        this.getGeoModel().getBone("local_root"));
            } else {
                RenderUtils.setPositionGeoBone(this.getGeoModel().getBone("root"), 0, 0, 0, 22,
                        contextModel.crouching ? 3.2f : 0, contextModel.leftArm, 0, 90, 0,
                        this.getGeoModel().getBone("local_root"));
            }
            super.renderModule(stack, contextModel, poseStack, bufferSource, packedLight, entity, tickDelta);
        }
    }

    @Override
    public void renderModuleAssembler(AssemblerBlockEntity assembler, BlockState state, float tickDelta,
                                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        this.animatable = (LongArmModule) ModItems.LONG_ARM_MODULE.get();
        RenderUtils.setPositionGeoBoneAssembler(this.getGeoModel().getBone("root"), 5.5f, 22, 0, 180, 90, 0);
        super.renderModuleAssembler(assembler, state, tickDelta, poseStack, bufferSource, packedLight, overlay);
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(RoboMod.MOD_ID, "textures/module/long_arm.png");
    }

    @Override
    public void renderLeftArm(ModelPart referenceModel, ItemStack stack, PlayerModel<?> contextModel, PoseStack poseStack,
                              MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float tickDelta) {
        poseStack.pushPose();
        this.animatable = (LongArmModule) stack.getItem();
        this.currentItemStack = stack;
        RenderUtils.setPositionGeoBone(this.getGeoModel().getBone("root"), 0f, 0, 0, 22, 0,
                referenceModel, this.getGeoModel().getBone("local_root"));
        // FIX: render() does not have a trailing tickDelta param in this GeckoLib version
        this.render(poseStack, animatable, bufferSource, null,
                bufferSource.getBuffer(RenderType.entityCutout(this.getTexture())), packedLight);
        poseStack.popPose();
    }

    @Override
    public void renderRightArm(ModelPart referenceModel, ItemStack stack, PlayerModel<?> contextModel, PoseStack poseStack,
                               MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float tickDelta) {
        poseStack.pushPose();
        this.animatable = (LongArmModule) stack.getItem();
        this.currentItemStack = stack;
        RenderUtils.setPositionGeoBone(this.getGeoModel().getBone("root"), 0f, 0, 0f, 22, 0,
                referenceModel, 0, 180, 0, this.getGeoModel().getBone("local_root"));
        // FIX: same — no tickDelta
        this.render(poseStack, animatable, bufferSource, null,
                bufferSource.getBuffer(RenderType.entityCutout(this.getTexture())), packedLight);
        poseStack.popPose();
    }
}