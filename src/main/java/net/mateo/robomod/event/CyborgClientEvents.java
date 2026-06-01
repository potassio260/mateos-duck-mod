package net.mateo.robomod.event;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.entity.CyborgEntity;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RoboMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CyborgClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player instanceof PlayerExtension ext && ext.isCyborg()) {

                // THE GHOST MOUNT FIX:
                // If the client missed the server's mount packet, force it to mount
                // the closest Cyborg the moment it detects it in the world!
                if (player.getVehicle() == null) {
                    for (Entity entity : player.level().getEntitiesOfClass(CyborgEntity.class, player.getBoundingBox().inflate(10.0))) {
                        if (entity.getPassengers().isEmpty() || entity.hasPassenger(player)) {
                            player.startRiding(entity, true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        if (event.getEntity() instanceof PlayerExtension ext && ext.isCyborg()) {
            // Because you are physically riding the Cyborg vehicle now, we just
            // cancel your human body render so it doesn't clip through the metal.
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player instanceof PlayerExtension ext && ext.isCyborg()) {
            ResourceLocation overlayId = event.getOverlay().id();
            String path = overlayId.getPath().toLowerCase();
            String namespace = overlayId.getNamespace().toLowerCase();

            // Aggressively hide Vanilla Food/Armor, and wipe ALL Tough As Nails overlays
            if (path.contains("food") || path.contains("armor") || path.contains("thirst") || path.contains("temp") || namespace.contains("tough")) {
                event.setCanceled(true);
            }
        }
    }
}