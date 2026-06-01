package net.mateo.robomod.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.extension.PlayerExtension;
import net.mateo.robomod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RoboMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RoboModHud {

    public static final ResourceLocation ENERGY_BACKGROUND =
            new ResourceLocation(RoboMod.MOD_ID, "textures/gui/energy_bar_background.png");
    public static final ResourceLocation BLUE_ENERGY_OVERLAY =
            new ResourceLocation(RoboMod.MOD_ID, "textures/gui/energy_bar_overlay.png");
    public static final ResourceLocation GREEN_ENERGY_OVERLAY =
            new ResourceLocation(RoboMod.MOD_ID, "textures/gui/energy_bar_overlay_green.png");
    public static final ResourceLocation YELLOW_ENERGY_OVERLAY =
            new ResourceLocation(RoboMod.MOD_ID, "textures/gui/energy_bar_overlay_yellow.png");

    /**
     * Called from ClientSetup to force class loading and register the @Mod.EventBusSubscriber.
     * The annotation handles actual registration; this method just ensures the class is loaded.
     */
    public static void init() {
        // No-op: @Mod.EventBusSubscriber registers the FORGE bus listener on class load.
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.PLAYER_HEALTH.type()) return;

        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;

        LocalPlayer player = client.player;
        GuiGraphics context = event.getGuiGraphics();

        renderDebug(player, context);
        renderHud(player, context, client);
    }

    private static void renderDebug(LocalPlayer player, GuiGraphics context) {
        if (!net.minecraftforge.fml.loading.FMLLoader.isProduction()) {
            if (player instanceof PlayerExtension cyborg && cyborg.isCyborg()) {
                context.drawString(
                        Minecraft.getInstance().font,
                        "Cyborg Energy: " + cyborg.getEnergyStored(),
                        10,
                        context.guiHeight() / 2,
                        0xFFFFFFFF
                );
            }
        }
    }

    private static void renderHud(LocalPlayer player, GuiGraphics context, Minecraft client) {
        int x = context.guiWidth() / 2 + 10;
        int y = context.guiHeight() - 39;

        if (!(player instanceof PlayerExtension ex) || !ex.isCyborg()) return;
        if (client.gameMode == null || !client.gameMode.hasExperience()) return;
        if (client.options.hideGui) return;

        RenderSystem.enableBlend();

        context.blit(ENERGY_BACKGROUND, x, y, 0, 0, 81, 8, 81, 8);

        // FIX: Math.clamp() is Java 21 — use Mth.clamp() from Minecraft (works on Java 17)
        int width = (int)(80.0F * Mth.clamp(
                (float) ex.getEnergyStored() / (float) ex.getCapacity(), 0.0F, 1.0F));

        ResourceLocation overlay;
        if (ex.containsModule(ModItems.EXTRA_BATTERY_MODULE.get())) {
            overlay = GREEN_ENERGY_OVERLAY;
        } else if (ex.containsModule(ModItems.LARGE_BATTERY_MODULE.get())) {
            overlay = YELLOW_ENERGY_OVERLAY;
        } else {
            overlay = BLUE_ENERGY_OVERLAY;
        }

        context.blit(overlay, x, y, 0, 0, width, 8, 81, 8);

        RenderSystem.disableBlend();
    }
}