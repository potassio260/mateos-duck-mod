package net.mateo.robomod.client.render.module;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mateo.robomod.block.AssemblerBlock;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.item.AnimatableCyborgModule;
import net.mateo.robomod.item.ModItems;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;

public abstract class AnimatableModuleRenderer<M extends AnimatableCyborgModule> extends GeoObjectRenderer<M> {

    protected ItemStack currentItemStack;

    public abstract ResourceLocation getTexture();

    public AnimatableModuleRenderer(GeoModel<M> model) {
        super(model);
    }

    // NOTE: getInstanceId / doPostRenderCleanup @Override removed — the installed GeckoLib
    // version does not expose these as overridable from GeoObjectRenderer in this way.
    // If your GeckoLib version DOES have them, add @Override back.
    public long getInstanceId(M animatable) {
        return GeoItem.getId(this.currentItemStack);
    }

    public void doPostRenderCleanup() {
        this.animatable = null;
        this.currentItemStack = null;
    }

    /**
     * FIX: GeckoLib 4.4.x uses 4 float color params (r, g, b, a) instead of packed int colour.
     * Also: handleAnimations does NOT take a partialTick 4th argument.
     * Also: renderRecursively takes float r,g,b,a instead of int colour.
     */
    @Override
    public void actuallyRender(PoseStack poseStack, M animatable, BakedGeoModel model,
                               @Nullable RenderType renderType, MultiBufferSource bufferSource,
                               @Nullable VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {

        if (!isReRender) {
            AnimationState<M> animationState = new AnimationState<>(animatable, 0, 0, partialTick, false);
            long instanceId = getInstanceId(animatable);
            GeoModel<M> currentModel = getGeoModel();

            animationState.setData(DataTickets.TICK, animatable.getTick(animatable));
            animationState.setData(DataTickets.ITEMSTACK, this.currentItemStack);
            currentModel.addAdditionalStateData(animatable, instanceId, animationState::setData);
            // FIX: handleAnimations takes 3 args (no partialTick) in this GeckoLib version
            currentModel.handleAnimations(animatable, instanceId, animationState);
        }

        this.modelRenderTranslations = new Matrix4f(poseStack.last().pose());

        if (buffer != null) {
            if (renderType == null) return;
            buffer = bufferSource.getBuffer(renderType);
        }

        updateAnimatedTextureFrame(animatable);

        for (GeoBone group : model.topLevelBones()) {
            // FIX: renderRecursively expects float r,g,b,a — not packed int colour
            renderRecursively(poseStack, animatable, group, renderType, bufferSource, buffer, isReRender,
                    partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public void renderModule(ItemStack stack, PlayerModel<?> contextModel, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float tickDelta) {
        this.currentItemStack = stack;
        poseStack.pushPose();

        // FIX: render() takes 6 args — (PoseStack, M, MultiBufferSource, RenderType, VertexConsumer, int)
        // There is NO partialTick (tickDelta) parameter in this GeckoLib version's render().
        super.render(poseStack, animatable, bufferSource, null,
                bufferSource.getBuffer(RenderType.entityCutout(this.getTexture())), packedLight);

        poseStack.popPose();
    }

    public void renderModuleAssembler(AssemblerBlockEntity assembler, BlockState state, float tickDelta,
                                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        this.currentItemStack = ModItems.LONG_ARM_MODULE.get().getDefaultInstance();
        poseStack.pushPose();

        poseStack.translate(0.5, 1, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(state.getValue(AssemblerBlock.FACING).toYRot() + 180));
        poseStack.scale(0.95f, 0.95f, 0.95f);

        // FIX: same — no partialTick trailing arg
        super.render(poseStack, animatable, bufferSource, null,
                bufferSource.getBuffer(RenderType.entityCutout(this.getTexture())), packedLight);

        poseStack.popPose();
    }
}