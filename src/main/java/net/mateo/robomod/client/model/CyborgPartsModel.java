package net.mateo.robomod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public abstract class CyborgPartsModel extends Model {

    public CyborgPartsModel() {
        super(RenderType::entityCutoutNoCull);
    }

    public abstract ModelPart getHead();
    public abstract ModelPart getBody();
    public abstract ModelPart getRightArm();
    public abstract ModelPart getLeftArm();
    public abstract ModelPart getRightLeg();
    public abstract ModelPart getLeftLeg();

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // intentionally empty
    }
}