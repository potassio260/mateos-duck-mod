package net.mateo.robomod.client.model.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class ErrorModel extends Model {
    private final ModelPart error;

    public ErrorModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.error = root.getChild("error");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("error",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(4.5838F, -2.0217F, -1.0F, 0.7296F, 4.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(3.8542F, -2.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(2.7598F, -2.0217F, -1.0F, 0.7296F, 4.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(2.0302F, -2.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(0.571F,  -2.0217F, -1.0F, 0.7296F, 4.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-0.1586F, -2.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-2.7122F, -2.0217F, -1.0F, 1.4592F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-4.1714F, -2.0217F, -1.0F, 0.7296F, 4.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-4.901F,  -2.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(1.6654F,  -1.5217F, -1.0F, 0.7296F, 1.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-0.5234F, -1.5217F, -1.0F, 0.7296F, 1.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-1.6178F, -1.5217F, -1.0F, 0.7296F, 3.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-3.077F,  -1.5217F, -1.0F, 0.7296F, 3.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-5.2658F, -1.5217F, -1.0F, 0.7296F, 1.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(3.8542F,  -0.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(2.0302F,  -0.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-0.1586F, -0.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-4.901F,  -0.0217F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(1.6654F,   0.4783F, -1.0F, 0.7296F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-0.5234F,  0.4783F, -1.0F, 0.7296F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-5.2658F,  0.4783F, -1.0F, 0.7296F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(3.8542F,   1.9783F, -1.0F, 0.7296F, 0.5F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-2.7122F,  1.9783F, -1.0F, 1.4592F, 0.5F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.0326F, 15.0217F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        error.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}