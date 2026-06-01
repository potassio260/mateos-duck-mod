package net.mateo.robomod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class ControllerModel extends Model {
    public final ModelPart base;
    private final ModelPart screen;

    public ControllerModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.base   = root.getChild("base");
        this.screen = root.getChild("screen");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("base",
                CubeListBuilder.create()
                        .texOffs(0, 17).addBox(-5.0F, -14.0F, -4.0F, 10.0F, 14.0F, 8.0F, new CubeDeformation(0.01F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        partDefinition.addOrReplaceChild("screen",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-8.0F, -8.0F, -5.0F, 16.0F, 4.0F, 13.0F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        base.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        screen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}