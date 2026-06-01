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

public class RunnerDuckModel extends HierarchicalModel<DuckEntity> {
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

    public RunnerDuckModel(ModelPart root) {
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

        PartDefinition neck = root.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -9.6F, 2.25F));

        PartDefinition cube_r1 = neck.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, -1.0321F, -1.9041F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.001F, -4.2952F, -1.5699F, -0.829F, 0.0F, 0.0F));

        PartDefinition cube_r2 = neck.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(22, 25).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(-0.009F, -0.0815F, 0.1871F, 0.3054F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(28, 12).addBox(-1.5549F, -0.9294F, -1.7584F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 5).addBox(-1.0549F, -1.4607F, -2.3677F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0559F, -4.9357F, -2.7277F));

        PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(28, 10).addBox(-0.999F, 0.1537F, -1.8453F, 2.0F, 0.0F, 2.0F, new CubeDeformation(-0.06F)), PartPose.offset(-0.0559F, 0.2857F, -1.7723F));

        PartDefinition top_bill = beak.addOrReplaceChild("top_bill", CubeListBuilder.create().texOffs(10, 27).addBox(-0.9593F, -0.4925F, -1.751F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.05F)), PartPose.offset(-0.0396F, -0.2538F, -0.0943F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0312F, -6.7314F, 2.9642F, -1.309F, 0.0F, 0.0F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(24, 21).addBox(-2.0F, -0.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.0325F, 1.1388F, -2.9377F, -2.8798F, 0.0F, -0.0015F));

        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -3.5F, -1.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-0.0312F, 2.397F, 4.4012F, -0.2182F, 0.0F, 0.0F));

        PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.341F, -4.4334F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.0312F, 0.8679F, 1.1882F, 0.0F, 0.0F, -0.0015F));

        PartDefinition wing_left = body.addOrReplaceChild("wing_left", CubeListBuilder.create().texOffs(0, 11).addBox(-0.3108F, -1.1344F, -1.6699F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(18, 27).addBox(-0.5946F, -1.1711F, 4.3292F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 29).addBox(-0.5946F, -1.1711F, 5.3292F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5634F, -0.4275F, -1.5782F, 0.0436F, 0.0F, 0.0F));

        PartDefinition wing_right = body.addOrReplaceChild("wing_right", CubeListBuilder.create().texOffs(14, 11).addBox(-0.8108F, -1.1344F, -1.6699F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(28, 16).addBox(-0.5946F, -1.1711F, 4.3292F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 29).addBox(-0.5946F, -1.1711F, 5.3292F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4366F, -0.4275F, -1.5782F, 0.0436F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-0.0312F, -1.277F, 2.3397F));

        PartDefinition cube_r4 = tail.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(28, 14).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.7348F, 3.0763F, -0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r5 = tail.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(10, 24).addBox(-2.0F, -0.5F, -1.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5091F, 1.7961F, -0.1745F, 0.0F, 0.0F));

        PartDefinition leg_left = root.addOrReplaceChild("leg_left", CubeListBuilder.create().texOffs(0, 21).addBox(-1.5F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(10, 30).addBox(-0.5F, -0.001F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -3.0F, 2.0F));

        PartDefinition leg_right = root.addOrReplaceChild("leg_right", CubeListBuilder.create().texOffs(12, 21).addBox(-1.5F, 3.0F, -3.0F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 29).addBox(-0.5F, -0.001F, 0.0F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -3.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(DuckEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // 1. HEAD ROTATION: Only rotate the head (skull), not the neck - only if NOT eating
        if (!entity.isEating()) {
            this.applyHeadRotation(netHeadYaw, headPitch);
        }

        // 2. SITTING VISUALS: Runner ducks sit higher due to their tall stance
        if (entity.isDuckSitting()) {
            this.root.y += 5.0F; // Higher adjustment for tall runner duck
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
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        // Clamp rotation values to realistic ranges
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        // Apply rotation ONLY to the head (skull), not the neck
        // This keeps the neck stationary and only moves the head itself
        this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
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