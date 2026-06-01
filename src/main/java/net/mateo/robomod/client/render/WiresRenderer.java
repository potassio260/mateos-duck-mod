package net.mateo.robomod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.block.EnergyWireBlock;
import net.mateo.robomod.block.entity.EnergyWireBlockEntity;
import net.mateo.robomod.client.ClientSetup;
import net.mateo.robomod.client.render.debug.DebugRender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;     // FIX: was missing import
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Map;

public class WiresRenderer<T extends EnergyWireBlockEntity> implements BlockEntityRenderer<T> {

    public final ModelPart mid;
    public final ModelPart up;
    public final ModelPart down;
    public final ModelPart north;
    public final ModelPart west;
    public final ModelPart east;
    public final ModelPart south;

    final Map<BooleanProperty, ModelPart> directions;

    public WiresRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart modelPart = ctx.bakeLayer(ClientSetup.WIRES_LAYER);
        this.mid   = modelPart.getChild("mid");
        this.up    = modelPart.getChild("up");
        this.down  = modelPart.getChild("down");
        this.north = modelPart.getChild("north");
        this.west  = modelPart.getChild("west");
        this.east  = modelPart.getChild("east");
        this.south = modelPart.getChild("south");

        this.directions = Map.of(
                EnergyWireBlock.UP,    up,    EnergyWireBlock.DOWN, down,
                EnergyWireBlock.NORTH, north, EnergyWireBlock.EAST, east,
                EnergyWireBlock.SOUTH, south, EnergyWireBlock.WEST, west
        );
    }

    /**
     * FIX: ClientSetup calls WiresRenderer::getTexturedModelData, so the method must be named
     * getTexturedModelData (not createBodyLayer).
     */
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("mid",   CubeListBuilder.create().texOffs(0,  0) .addBox(-3.0F, -11.0F, -3.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("up",    CubeListBuilder.create().texOffs(0,  12).addBox(-3.0F, -16.0F, -3.0F, 6.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("down",  CubeListBuilder.create().texOffs(0,  23).addBox(-3.0F, -5.0F,  -3.0F, 6.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("north", CubeListBuilder.create().texOffs(24, 24).addBox(-3.0F, -11.0F, -8.0F, 6.0F, 6.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("west",  CubeListBuilder.create().texOffs(24, 0) .addBox(3.0F,  -11.0F, -3.0F, 5.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("east",  CubeListBuilder.create().texOffs(24, 12).addBox(-8.0F, -11.0F, -3.0F, 5.0F, 6.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("south", CubeListBuilder.create().texOffs(0,  34).addBox(-3.0F, -11.0F,  3.0F, 6.0F, 6.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void render(T entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (entity.getLevel() != null && !entity.isRemoved()) {
            BlockState state = entity.getLevel().getBlockState(entity.getBlockPos());

            poseStack.pushPose();
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            var vertexConsumer = bufferSource.getBuffer(
                    RenderType.entityCutoutNoCull(new ResourceLocation(RoboMod.MOD_ID, "textures/entity/wires.png")));

            mid.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            directions.forEach((property, part) -> {
                if (state.getValue(property)) part.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            });

            poseStack.popPose();

            if (!FMLLoader.isProduction()) {
                DebugRender.DebugRenderWires(entity, state, poseStack, bufferSource, packedLight, packedOverlay);
            }
        }
    }
}