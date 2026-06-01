package net.mateo.robomod.client.model.module;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class JetpackModuleModel extends ModuleModel {
    public final ModelPart root;

    public JetpackModuleModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("jetpack",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox( 1.5F, 0.0F, 2.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-4.5F, 0.0F, 2.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(10, 0).addBox(-1.5F, 2.0F, 2.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public ModelPart getRoot() {
        return this.root;
    }
}