package net.mateo.robomod.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BasicCyborgModel extends CyborgPartsModel {
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart right_arm;
    public final ModelPart left_arm;
    public final ModelPart right_leg;
    public final ModelPart left_leg;

    public BasicCyborgModel(ModelPart root) {
        super();
        this.head      = root.getChild("head");
        this.body      = root.getChild("body");
        this.right_arm = root.getChild("right_arm");
        this.left_arm  = root.getChild("left_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg  = root.getChild("left_leg");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(36, 22).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 16).addBox(1.0F, 5.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 16).addBox(-3.0F, 5.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(36, 31).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 23).addBox(-2.0F, 7.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(0, 30).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(14, 30).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 37).addBox(-1.0F, 7.0F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 30).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 54).addBox(-0.9F, 6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 44).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(32, 54).addBox(-1.1F, 6.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 44).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 48).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override public ModelPart getHead()     { return head; }
    @Override public ModelPart getBody()     { return body; }
    @Override public ModelPart getRightArm() { return right_arm; }
    @Override public ModelPart getLeftArm()  { return left_arm; }
    @Override public ModelPart getRightLeg() { return right_leg; }
    @Override public ModelPart getLeftLeg()  { return left_leg; }
}