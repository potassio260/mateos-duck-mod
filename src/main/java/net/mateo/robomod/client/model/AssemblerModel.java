package net.mateo.robomod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class AssemblerModel extends Model {
    public final ModelPart bone;
    private final ModelPart handle;
    private final ModelPart overlay;

    public AssemblerModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.bone    = root.getChild("bone");
        this.handle  = root.getChild("handle");
        this.overlay = root.getChild("overlay");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition handle = partDefinition.addOrReplaceChild("handle",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-1.0F, -28.0F, 4.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 32).addBox(-2.0F, -31.0F, 3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 32).addBox(-5.0F, -40.0F, 1.0F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        handle.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -30.0F, 5.0F, 0.2182F, 0.0F, 0.0F));

        partDefinition.addOrReplaceChild("base",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-16.0F, -16.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.0F, 24.0F, -8.0F));

        partDefinition.addOrReplaceChild("overlay",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-16.0F, -16.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F)),
                PartPose.offset(8.0F, 24.0F, -8.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        handle.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}