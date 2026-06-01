package net.mateo.robomod.client.model.module;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public abstract class ModuleModel extends Model {

    public ModuleModel() {
        super(RenderType::entityCutoutNoCull);
    }

    public abstract ModelPart getRoot();

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // intentionally empty — subclasses handle rendering
    }
}