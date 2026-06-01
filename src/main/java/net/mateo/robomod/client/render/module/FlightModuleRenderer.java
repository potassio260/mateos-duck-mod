package net.mateo.robomod.client.render.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.client.model.DefaultedModuleGeoModel;
import net.mateo.robomod.item.FlightModule;
import net.mateo.robomod.item.ModItems;
import net.mateo.robomod.util.RenderUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FlightModuleRenderer extends AnimatableModuleRenderer<FlightModule> {

    public FlightModuleRenderer() {
        super(new DefaultedModuleGeoModel<>(new ResourceLocation(RoboMod.MOD_ID, "flight_module")));
    }

    @Override
    public void renderModule(ItemStack stack, PlayerModel<?> contextModel, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, LivingEntity entity, float tickDelta) {
        this.animatable = (FlightModule) stack.getItem();
        RenderUtils.setPositionGeoBone(this.getGeoModel().getBone("root"), 0f, 2.5f, 2, 24,
                contextModel.crouching ? 3.2f : 0f, contextModel.body, 180, 180, 0,
                this.getGeoModel().getBone("local_root"));

        super.renderModule(stack, contextModel, poseStack, bufferSource, packedLight, entity, tickDelta);
    }

    @Override
    public void renderModuleAssembler(AssemblerBlockEntity assembler, BlockState state, float tickDelta,
                                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay) {
        this.animatable = (FlightModule) ModItems.FLIGHT_MODULE.get();
        RenderUtils.setPositionGeoBoneAssembler(this.getGeoModel().getBone("root"), 0, 24 - 3, 2, 0, 0, 0);
        super.renderModuleAssembler(assembler, state, tickDelta, poseStack, bufferSource, packedLight, overlay);
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(RoboMod.MOD_ID, "textures/module/flight_module.png");
    }
}