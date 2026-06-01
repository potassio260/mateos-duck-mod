package net.mateo.robomod.client.model.module;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class FlightModuleModel extends ModuleModel {
    public final ModelPart root;

    public FlightModuleModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition wings = partDefinition.addOrReplaceChild("wings",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        wings.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(0, 8).addBox(-16.0F, -4.0F, 0.0F, 16.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 3.0F, 2.0F, 0.0F, 0.6981F, 0.0F));

        wings.addOrReplaceChild("cube_r2",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(0.0F, -4.0F, 0.0F, 16.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 3.0F, 2.0F, 0.0F, -0.6981F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public ModelPart getRoot() {
        return this.root;
    }
}