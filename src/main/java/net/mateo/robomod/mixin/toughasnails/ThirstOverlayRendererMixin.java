package net.mateo.robomod.mixin.toughasnails;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.thirst.ThirstOverlayRenderer;
import net.mateo.robomod.extension.PlayerExtension;

// Fabric → Forge mapping notes:
//   DrawContext → net.minecraft.client.gui.GuiGraphics  (Forge 1.20.1)
//   MinecraftClient → net.minecraft.client.Minecraft
//   Everything else is identical in structure.
@Mixin(ThirstOverlayRenderer.class)
public class ThirstOverlayRendererMixin {

    @Inject(method = "drawThirst", at = @At("HEAD"), cancellable = true, remap = false)
    private static void drawThirst(
            GuiGraphics guiGraphics,
            int screenWidth,
            int rowTop,
            int thirstLevel,
            float thirstHydrationLevel,
            CallbackInfo ci) {

        if (Minecraft.getInstance().player instanceof PlayerExtension ex && ex.isCyborg()) {
            ci.cancel();
        }
    }
}
