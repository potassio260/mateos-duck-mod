package net.mateo.robomod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class BatteryModel extends Model {
    public final ModelPart base;

    public BatteryModel(ModelPart root) {
        super(RenderType::entityCutout);
        this.base = root.getChild("base");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("base",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 16.0F, 10.0F, new CubeDeformation(0.005F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        base.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}