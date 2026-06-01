package net.mateo.robomod.client.render.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.block.entity.AssemblerBlockEntity;
import net.mateo.robomod.client.ClientSetup;
import net.mateo.robomod.client.model.module.ExtraBatteryModuleModel;
import net.mateo.robomod.client.model.module.JetpackModuleModel;
import net.mateo.robomod.client.model.module.ModuleModel;
import net.mateo.robomod.item.*;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ModuleRenderer {
    private static boolean initialized = false;

    public String texture;
    public ModuleModel model;

    public ModuleRenderer(String texture, ModuleModel model) {
        this.texture = texture;
        this.model = model;
    }

    public abstract void render(PlayerModel<?> contextModel, PoseStack poseStack,
                                MultiBufferSource bufferSource, int packedLight, LivingEntity entity);

    public abstract void renderAssembler(AssemblerBlockEntity assembler, BlockState state, float tickDelta,
                                         PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay);

    public static void init(EntityRendererProvider.Context context) {
        if (!initialized) {
            ((JetpackModule) ModItems.JETPACK_MODULE.get()).setModuleRenderer(
                    new JetpackModuleRenderer("textures/entity/jetpack_module.png",
                            new JetpackModuleModel(context.bakeLayer(ClientSetup.JETPACK_MODULE_LAYER))));

            ((LongArmModule) ModItems.LONG_ARM_MODULE.get()).setModuleRenderer(new LongArmModuleRenderer());

            ((FlightModule) ModItems.FLIGHT_MODULE.get()).setModuleRenderer(new FlightModuleRenderer());

            ((BatteryModule) ModItems.EXTRA_BATTERY_MODULE.get()).setModuleRenderer(
                    new ExtraBatteryModuleRenderer("textures/entity/extra_battery_module.png",
                            new ExtraBatteryModuleModel(context.bakeLayer(ClientSetup.EXTRA_BATTERY_MODULE_LAYER))));

            initialized = true;
        }
    }
}