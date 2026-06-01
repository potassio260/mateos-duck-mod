package net.mateo.robomod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.client.model.CyborgPartsModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public abstract class CyborgPartRenderer {

    public String name;
    public String texture;
    public Supplier<CyborgPartsModel> model;

    public CyborgPartRenderer(String name, String texture, Supplier<CyborgPartsModel> model) {
        this.name    = name;
        this.texture = texture;
        this.model   = model;
    }

    abstract public void render(PlayerModel<?> contextModel, PoseStack poseStack,
                                MultiBufferSource bufferSource, int packedLight, LivingEntity entity);

    abstract public void renderAssembler(AssemblerBlockEntity assembler, BlockState state, float partialTick,
                                         PoseStack poseStack, MultiBufferSource bufferSource,
                                         int packedLight, int packedOverlay);
}
