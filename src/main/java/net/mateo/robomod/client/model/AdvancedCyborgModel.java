package net.mateo.robomod.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class AdvancedCyborgModel extends CyborgPartsModel {
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart right_arm;
    public final ModelPart left_arm;
    public final ModelPart right_leg;
    public final ModelPart left_leg;

    public AdvancedCyborgModel(ModelPart root) {
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
                        .texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 48).addBox(-3.0F, 6.0F, -1.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 32).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(0, 51).addBox(0.0F, 1.0F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 51).addBox(-2.0F, 1.0F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 18).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 39).addBox(-3.0F, 5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(48, 52).addBox(1.0F, 1.0F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 53).addBox(-1.0F, 1.0F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 25).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 39).addBox(-1.0F, 5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 42).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 32).addBox(-2.0F, 5.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 0).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(48, 9).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 48).addBox(-2.0F, 5.0F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 16).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
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