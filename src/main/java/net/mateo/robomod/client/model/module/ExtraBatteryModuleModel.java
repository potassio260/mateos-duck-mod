package net.mateo.robomod.client.model.module;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ExtraBatteryModuleModel extends ModuleModel {
    public final ModelPart root;

    public ExtraBatteryModuleModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("battery",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.5F, 0.01F, 0.01F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 16, 16);
    }

    @Override
    public ModelPart getRoot() {
        return this.root;
    }
}