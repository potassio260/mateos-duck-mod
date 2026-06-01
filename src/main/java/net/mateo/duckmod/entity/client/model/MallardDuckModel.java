package net.mateo.duckmod.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mateo.duckmod.entity.animations.ModAnimationDefinitions;
import net.mateo.duckmod.entity.custom.DuckEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MallardDuckModel extends HierarchicalModel<DuckEntity> {
    private final ModelPart root;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart top_bill;
    private final ModelPart body;
    private final ModelPart wing_left;
    private final ModelPart wing_right;
    private final ModelPart tail;
    private final ModelPart leg_left;
    private final ModelPart leg_right;

    public MallardDuckModel(ModelPart root) {
        this.root = root.getChild("root");
        this.neck = this.root.getChild("neck");
        this.head = this.neck.getChild("head");
        this.beak = this.head.getChild("beak");
        this.top_bill = this.beak.getChild("top_bill");
        this.body = this.root.getChild("body");
        this.wing_left = this.body.getChild("wing_left");
        this.wing_right = this.body.getChild("wing_right");
        this.tail = this.body.getChild("tail");
        this.leg_left = this.root.getChild("leg_left");
        this.leg_right = this.root.getChild("leg_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition neck = root.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -5.6F, -1.5F, 0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r1 = neck.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-1.0F, -5.3613F, -1.2463F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(20, 0).addBox(-1.5F, -2.2116F, -1.4997F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, 27).addBox(-2.0F, -1.3586F, -0.6322F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.2602F, 1.4915F, -0.1309F, 0.0F, 0.0F));

        PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(28, 11).addBox(-1.0F, 0.25F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0611F, -0.1648F, -2.0931F));

        PartDefinition top_bill = beak.addOrReplaceChild("top_bill", CubeListBuilder.create().texOffs(0, 28).addBox(-0.9604F, -1.7712F, -0.7573F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0215F, 1.0212F, -0.2427F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.9899F, -1.641F, -3.4334F, 4.0F, 5.0F, 6.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.0101F, -5.359F, 1.4334F, -0.3054F, 0.0F, 0.0F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(8, 21).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0101F, 0.3289F, -3.8957F, 0.0436F, 0.0F, 0.0F));

        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 6).addBox(-2.0F, -3.5F, -1.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0101F, 2.7791F, 3.213F, -0.3054F, 0.0F, 0.0F));

        PartDefinition wing_left = body.addOrReplaceChild("wing_left", CubeListBuilder.create().texOffs(0, 11).addBox(-0.5608F, -1.1344F, -1.6699F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(28, 13).addBox(-0.8446F, -1.1711F, 4.3292F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 29).addBox(-0.8446F, -1.1711F, 5.3292F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6047F, -0.2954F, -1.7664F, 0.0436F, 0.0F, 0.0F));

        PartDefinition wing_right = body.addOrReplaceChild("wing_right", CubeListBuilder.create().texOffs(14, 11).addBox(-0.3108F, -1.1344F, -1.6699F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(28, 17).addBox(-0.0946F, -1.1711F, 4.3292F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 6).addBox(-0.0946F, -1.1711F, 5.3292F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.6453F, -0.2954F, -1.7664F, 0.0436F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0101F, -1.6449F, 1.1515F));

        PartDefinition cube_r4 = tail.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(20, 29).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.7348F, 3.0763F, -0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r5 = tail.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(18, 21).addBox(-2.0F, -0.5F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5091F, 1.7961F, -0.1745F, 0.0F, 0.0F));

        PartDefinition leg_left = root.addOrReplaceChild("leg_left", CubeListBuilder.create().texOffs(18, 24).addBox(-1.5F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 30).addBox(-0.5F, 0.999F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -3.0F, 2.0F));

        PartDefinition leg_right = root.addOrReplaceChild("leg_right", CubeListBuilder.create().texOffs(8, 27).addBox(-1.5F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(30, 8).addBox(-0.5F, 0.999F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -3.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    @Override
    public void setupAnim(DuckEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // 1. HEAD ROTATION: Only look at player if NOT eating
        if (!entity.isEating()) {
            this.applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
        }

        // 2. SITTING VISUALS: Lower the body if sitting
        if (entity.isDuckSitting()) {
            this.body.y += 3.0F;
            this.neck.y += 3.0F;
        }

        // 3. ANIMATIONS - Access through animation behavior
        var animBehavior = entity.getAnimationBehavior();

        // Walking animation
        this.animateWalk(ModAnimationDefinitions.DUCK_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);

        // All other animations from ModAnimationDefinitions
        this.animate(animBehavior.jumpAnimationState, ModAnimationDefinitions.DUCK_JUMP, ageInTicks, 1f);
        this.animate(animBehavior.gobbleAnimationState, ModAnimationDefinitions.DUCK_GOBBLE, ageInTicks, 1f);
        this.animate(animBehavior.tailWagAnimationState, ModAnimationDefinitions.DUCK_TAILWAG, ageInTicks, 1f);
        this.animate(animBehavior.quackAnimationState, ModAnimationDefinitions.DUCK_CUACK, ageInTicks, 1f);
        this.animate(animBehavior.attackAnimationState, ModAnimationDefinitions.DUCK_BITE, ageInTicks, 1.5f);
        this.animate(animBehavior.downAnimationState, ModAnimationDefinitions.DUCK_DUCK, ageInTicks, 1f);
        this.animate(animBehavior.runAnimationState, ModAnimationDefinitions.DUCK_RUN, ageInTicks, 1f);
        this.animate(animBehavior.idleAnimationState, ModAnimationDefinitions.DUCK_IDLE, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch, float pAgeInTicks) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.neck.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.neck.xRot = pHeadPitch * ((float) Math.PI / 180F);
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