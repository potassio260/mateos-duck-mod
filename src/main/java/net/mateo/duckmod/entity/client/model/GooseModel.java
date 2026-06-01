package net.mateo.duckmod.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mateo.duckmod.entity.custom.GooseEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class GooseModel extends HierarchicalModel<GooseEntity> {
    private final ModelPart root;
    private final ModelPart pierna;
    private final ModelPart der2;
    private final ModelPart izq2;
    private final ModelPart cuerpo;
    private final ModelPart alas;
    private final ModelPart izq;
    private final ModelPart der;
    private final ModelPart cuello;
    private final ModelPart cabeza;
    private final ModelPart Bocaarriba;
    private final ModelPart cola;
    private final ModelPart punta_cola;
    private final ModelPart extra;

    public GooseModel(ModelPart root) {
        this.root = root.getChild("root");
        this.pierna = this.root.getChild("pierna");
        this.der2 = this.pierna.getChild("der2");
        this.izq2 = this.pierna.getChild("izq2");
        this.cuerpo = this.root.getChild("cuerpo");
        this.alas = this.cuerpo.getChild("alas");
        this.izq = this.alas.getChild("izq");
        this.der = this.alas.getChild("der");
        this.cuello = this.cuerpo.getChild("cuello");
        this.cabeza = this.cuello.getChild("cabeza");
        this.Bocaarriba = this.cabeza.getChild("Bocaarriba");
        this.cola = this.cuerpo.getChild("cola");
        this.punta_cola = this.cuerpo.getChild("punta_cola");
        this.extra = this.cuerpo.getChild("extra");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pierna = root.addOrReplaceChild("pierna", CubeListBuilder.create(), PartPose.offset(0.6142F, -0.7934F, 0.7098F));

        PartDefinition der2 = pierna.addOrReplaceChild("der2", CubeListBuilder.create().texOffs(62, 40).addBox(-1.81F, 2.716F, -4.074F, 4.0F, 0.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(78, 54).addBox(-0.712F, -1.884F, 0.376F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.4F)), PartPose.offset(-3.492F, -1.94F, 0.0F));

        PartDefinition izq2 = pierna.addOrReplaceChild("izq2", CubeListBuilder.create().texOffs(78, 61).addBox(-1.312F, -1.884F, 0.376F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.4F))
                .texOffs(62, 47).addBox(-2.31F, 2.716F, -4.074F, 4.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(2.328F, -1.94F, 0.0F));

        PartDefinition cuerpo = root.addOrReplaceChild("cuerpo", CubeListBuilder.create().texOffs(0, 0).addBox(-4.037F, -3.38F, -3.5625F, 8.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.037F, -7.5473F, -1.7416F));

        PartDefinition cuerpoarriba_r1 = cuerpo.addOrReplaceChild("cuerpoarriba_r1", CubeListBuilder.create().texOffs(30, 40).addBox(-4.0318F, -4.1197F, -3.8803F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0001F)), PartPose.offsetAndRotation(-0.0052F, -0.8261F, -2.0229F, -1.0908F, 0.0F, 0.0F));

        PartDefinition alas = cuerpo.addOrReplaceChild("alas", CubeListBuilder.create(), PartPose.offset(0.0146F, 0.8563F, 1.9665F));

        PartDefinition izq = alas.addOrReplaceChild("izq", CubeListBuilder.create().texOffs(0, 40).addBox(-1.697F, -3.9404F, -1.273F, 2.0F, 5.0F, 13.0F, new CubeDeformation(-0.4F))
                .texOffs(64, 18).addBox(-1.4378F, -1.5772F, 0.6018F, 2.0F, 6.0F, 6.0F, new CubeDeformation(-0.31F))
                .texOffs(70, 0).addBox(-1.5818F, -2.8852F, -0.8062F, 2.0F, 6.0F, 6.0F, new CubeDeformation(-0.36F))
                .texOffs(0, 20).addBox(-1.553F, -3.4384F, -0.447F, 2.0F, 6.0F, 14.0F, new CubeDeformation(-0.35F))
                .texOffs(30, 56).addBox(-1.409F, -1.5484F, 3.483F, 2.0F, 6.0F, 10.0F, new CubeDeformation(-0.3F)), PartPose.offset(4.7336F, 0.0F, -2.91F));

        PartDefinition der = alas.addOrReplaceChild("der", CubeListBuilder.create().texOffs(40, 0).addBox(-0.497F, -3.9404F, -1.273F, 2.0F, 5.0F, 13.0F, new CubeDeformation(-0.4F))
                .texOffs(0, 70).addBox(-0.7562F, -1.5772F, 0.6018F, 2.0F, 6.0F, 6.0F, new CubeDeformation(-0.31F))
                .texOffs(16, 72).addBox(-0.6122F, -2.8852F, -0.8062F, 2.0F, 6.0F, 6.0F, new CubeDeformation(-0.36F))
                .texOffs(32, 20).addBox(-0.641F, -3.4384F, -0.447F, 2.0F, 6.0F, 14.0F, new CubeDeformation(-0.35F))
                .texOffs(54, 56).addBox(-0.785F, -1.5484F, 3.483F, 2.0F, 6.0F, 10.0F, new CubeDeformation(-0.3F)), PartPose.offset(-4.6068F, 0.0F, -2.91F));

        PartDefinition cuello = cuerpo.addOrReplaceChild("cuello", CubeListBuilder.create(), PartPose.offset(-0.1548F, -1.2904F, -3.9267F));

        PartDefinition cuello2_r1 = cuello.addOrReplaceChild("cuello2_r1", CubeListBuilder.create().texOffs(80, 26).addBox(-2.8222F, -2.72F, 0.72F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.94F, -5.0634F, -1.228F, 0.0873F, 0.0F, 0.0F));

        PartDefinition cuello3_r1 = cuello.addOrReplaceChild("cuello3_r1", CubeListBuilder.create().texOffs(80, 22).addBox(-2.8222F, -2.72F, 0.72F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.94F, -5.983F, -0.9719F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cuello1_r1 = cuello.addOrReplaceChild("cuello1_r1", CubeListBuilder.create().texOffs(72, 72).addBox(-2.8222F, -4.78F, 0.72F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.94F, -2.5086F, -2.4102F, -0.2618F, 0.0F, 0.0F));

        PartDefinition cabeza = cuello.addOrReplaceChild("cabeza", CubeListBuilder.create().texOffs(70, 12).addBox(-1.3822F, -2.3709F, -2.8297F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.1F))
                .texOffs(78, 68).addBox(-0.3822F, 0.1911F, -5.6337F, 1.0F, 0.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 18).addBox(-1.5322F, -1.4289F, -2.0018F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 18).addBox(0.7678F, -1.4289F, -2.0018F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.4156F, 0.6702F, 0.1396F, 0.0F, 0.0F));

        PartDefinition Bocaarriba = cabeza.addOrReplaceChild("Bocaarriba", CubeListBuilder.create().texOffs(64, 30).addBox(-0.8822F, -1.5589F, -6.1337F, 2.0F, 2.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cola = cuerpo.addOrReplaceChild("cola", CubeListBuilder.create().texOffs(32, 72).addBox(-3.09F, -2.3109F, -2.254F, 6.0F, 6.0F, 2.0F, new CubeDeformation(-0.25F))
                .texOffs(48, 72).addBox(-0.7236F, -2.2245F, -1.8704F, 3.0F, 6.0F, 3.0F, new CubeDeformation(-0.22F))
                .texOffs(60, 72).addBox(-2.6636F, -2.2245F, -1.8704F, 3.0F, 6.0F, 3.0F, new CubeDeformation(-0.22F)), PartPose.offset(-0.0049F, -0.4488F, 9.6115F));

        PartDefinition punta_cola = cuerpo.addOrReplaceChild("punta_cola", CubeListBuilder.create(), PartPose.offset(0.0F, -1.94F, 9.7F));

        PartDefinition cube_r1 = punta_cola.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(84, 19).addBox(1.963F, 1.0F, -1.0F, -1.0F, -1.0F, 1.0F, new CubeDeformation(-1.0F)), PartPose.offsetAndRotation(-1.65F, 0.25F, 1.0F, -0.9163F, 0.0F, 0.0F));

        PartDefinition extra = cuerpo.addOrReplaceChild("extra", CubeListBuilder.create(), PartPose.offset(0.0F, 1.6F, 0.0F));

        PartDefinition cuerpoextra_r1 = extra.addOrReplaceChild("cuerpoextra_r1", CubeListBuilder.create().texOffs(0, 58).addBox(-1.8822F, -1.916F, -4.024F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.1548F, -2.5825F, -2.3115F, -1.0908F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(GooseEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Head look - convert degrees to radians properly
        this.cabeza.xRot = 0.1396F + (headPitch * ((float)Math.PI / 180F));
        this.cabeza.yRot = netHeadYaw * ((float)Math.PI / 180F);

        // Sitting pose (check first to avoid conflicts)
        if (entity.isDuckSitting()) {
            this.cuerpo.y = this.cuerpo.y + 2.0F; // Raise body significantly// Lower legs to ground
            this.der2.xRot = -1.5F;
            this.izq2.xRot = -1.5F;
            return; // Skip other animations when sitting
        }

        // Walking animation - legs
        this.der2.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.izq2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        // Wing flapping when walking/swimming
        if (entity.isInWater()) {
            this.izq.zRot = Mth.cos(ageInTicks * 0.3F) * 0.3F;
            this.der.zRot = -Mth.cos(ageInTicks * 0.3F) * 0.3F;
        } else {
            this.izq.zRot = Mth.cos(limbSwing * 0.6662F) * 0.2F * limbSwingAmount;
            this.der.zRot = -Mth.cos(limbSwing * 0.6662F) * 0.2F * limbSwingAmount;
        }

        // Tail wag
        this.cola.yRot = Mth.cos(limbSwing * 0.6662F) * 0.2F * limbSwingAmount;

        // Body bob when walking
        this.cuerpo.y = -7.5473F + Mth.cos(limbSwing * 0.6662F) * 0.3F * limbSwingAmount;

        // Neck sway slightly when walking
        this.cuello.yRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI / 2) * 0.1F * limbSwingAmount;

        // Eating animation (overrides normal head movement)
        if (entity.isEating()) {
            this.cabeza.xRot = 0.8F + Mth.cos(ageInTicks * 0.5F) * 0.2F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}