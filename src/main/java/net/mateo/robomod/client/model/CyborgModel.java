package net.mateo.robomod.client.model;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.entity.CyborgEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CyborgModel extends GeoModel<CyborgEntity> {

    @Override
    public ResourceLocation getModelResource(CyborgEntity animatable) {
        return new ResourceLocation(RoboMod.MOD_ID, "geo/entity/cyborg.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CyborgEntity animatable) {
        return new ResourceLocation(RoboMod.MOD_ID, "textures/entity/cyborg.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CyborgEntity animatable) {
        return new ResourceLocation(RoboMod.MOD_ID, "animations/entity/cyborg.animation.json");
    }

    @Override
    public void setCustomAnimations(CyborgEntity animatable, long instanceId, AnimationState<CyborgEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head     = getAnimationProcessor().getBone("head");
        CoreGeoBone body     = getAnimationProcessor().getBone("body");
        CoreGeoBone rightArm = getAnimationProcessor().getBone("right_arm");
        CoreGeoBone leftArm  = getAnimationProcessor().getBone("left_arm");
        CoreGeoBone rightLeg = getAnimationProcessor().getBone("right_leg");
        CoreGeoBone leftLeg  = getAnimationProcessor().getBone("left_leg");

        float headPitch  = 0;
        float netHeadYaw = 0;

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (entityData != null) {
            headPitch  = entityData.headPitch();
            netHeadYaw = entityData.netHeadYaw();
        }

        // Vampire Method — only runs for the dummy shell (ID >= 100000)
        if (animatable.getId() >= 100000) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                headPitch  = player.getXRot();
                netHeadYaw = Mth.wrapDegrees(player.yHeadRot - player.yBodyRot);

                try {
                    var dispatcher    = Minecraft.getInstance().getEntityRenderDispatcher();
                    var entityRenderer = dispatcher.getRenderer(player);

                    if (entityRenderer instanceof PlayerRenderer playerRenderer) {
                        PlayerModel<LocalPlayer> vanillaModel = playerRenderer.getModel();

                        // Force vanilla to compute the exact current pose
                        vanillaModel.setupAnim(
                                player,
                                player.walkAnimation.position(),
                                player.walkAnimation.speed(),
                                player.tickCount + animationState.getPartialTick(),
                                netHeadYaw,
                                headPitch
                        );

                        // Steal angles directly from the vanilla model parts
                        if (rightLeg != null) {
                            rightLeg.setRotX(vanillaModel.rightLeg.xRot);
                            rightLeg.setRotY(vanillaModel.rightLeg.yRot);
                            rightLeg.setRotZ(vanillaModel.rightLeg.zRot);
                        }
                        if (leftLeg != null) {
                            leftLeg.setRotX(vanillaModel.leftLeg.xRot);
                            leftLeg.setRotY(vanillaModel.leftLeg.yRot);
                            leftLeg.setRotZ(vanillaModel.leftLeg.zRot);
                        }
                        if (rightArm != null) {
                            rightArm.setRotX(vanillaModel.rightArm.xRot);
                            rightArm.setRotY(vanillaModel.rightArm.yRot);
                            rightArm.setRotZ(vanillaModel.rightArm.zRot);
                        }
                        if (leftArm != null) {
                            leftArm.setRotX(vanillaModel.leftArm.xRot);
                            leftArm.setRotY(vanillaModel.leftArm.yRot);
                            leftArm.setRotZ(vanillaModel.leftArm.zRot);
                        }
                        if (body != null) {
                            body.setRotX(vanillaModel.body.xRot);
                            body.setRotY(vanillaModel.body.yRot);
                            body.setRotZ(vanillaModel.body.zRot);
                        }
                    }
                } catch (Exception e) {
                    // Silent fallback — if vampire method fails, head tracking still applies below
                }
            }
        }

        // Always apply head tracking
        if (head != null) {
            head.setRotX(headPitch * Mth.DEG_TO_RAD);
            head.setRotY(netHeadYaw * Mth.DEG_TO_RAD);
        }
    }
}