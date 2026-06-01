package net.mateo.robomod.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.entity.CyborgEntity;
import net.mateo.robomod.entity.ModEntities;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CyborgRenderEvent {

    private static final Map<UUID, CyborgEntity> dummyCyborgs = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (!(player instanceof PlayerExtension ext) || !ext.isCyborg()) return;

        event.setCanceled(true);

        Minecraft mc = Minecraft.getInstance();
        UUID playerId = player.getUUID();

        CyborgEntity dummyCyborg = dummyCyborgs.get(playerId);
        if (dummyCyborg == null || dummyCyborg.level() != player.level()) {
            dummyCyborg = ModEntities.CYBORG_ENTITY.get().create(player.level());
            if (dummyCyborg == null) return;
            dummyCyborg.setId(player.getId() + 100000);
            dummyCyborg.setNoAi(true);
            dummyCyborgs.put(playerId, dummyCyborg);
        }

        dummyCyborg.copyPosition(player);
        dummyCyborg.setDeltaMovement(player.getDeltaMovement());

        // FIX: Hand the real player's items to the Dummy!
        dummyCyborg.setItemSlot(EquipmentSlot.MAINHAND, player.getMainHandItem());
        dummyCyborg.setItemSlot(EquipmentSlot.OFFHAND, player.getOffhandItem());

        dummyCyborg.setHealth(player.getHealth());
        dummyCyborg.deathTime = player.deathTime;
        dummyCyborg.hurtTime = player.hurtTime;
        dummyCyborg.setPose(player.getPose());

        dummyCyborg.yRotO = player.yRotO;
        dummyCyborg.setYRot(player.getYRot());
        dummyCyborg.yHeadRotO = player.yHeadRotO;
        dummyCyborg.yHeadRot = player.yHeadRot;
        dummyCyborg.yBodyRotO = player.yBodyRotO;
        dummyCyborg.yBodyRot = player.yBodyRot;

        EntityRenderer<? super CyborgEntity> renderer = mc.getEntityRenderDispatcher().getRenderer(dummyCyborg);
        if (renderer == null) return;

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        float partialTick = event.getPartialTick();
        float entityYaw = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot);

        try {
            renderer.render(dummyCyborg, entityYaw, partialTick, poseStack, event.getMultiBufferSource(), event.getPackedLight());
        } catch (Exception e) {
            RoboMod.LOGGER.error("[CyborgRender] renderer.render() threw an exception!", e);
        }

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player instanceof PlayerExtension ext && ext.isCyborg()) {
            ResourceLocation overlayId = event.getOverlay().id();
            String path = overlayId.getPath().toLowerCase();
            String namespace = overlayId.getNamespace().toLowerCase();
            if (path.contains("food") || path.contains("armor") || path.contains("thirst")
                    || path.contains("temp") || namespace.contains("tough")) {
                event.setCanceled(true);
            }
        }
    }
}