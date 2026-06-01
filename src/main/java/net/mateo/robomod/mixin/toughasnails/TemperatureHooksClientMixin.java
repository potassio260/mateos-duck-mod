package net.mateo.robomod.mixin.toughasnails;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.temperature.TemperatureHooksClient;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.extension.PlayerExtension;

@Mixin(TemperatureHooksClient.class)
public class TemperatureHooksClientMixin {

    private static final ResourceLocation HEART_FULL =
            new ResourceLocation(RoboMod.MOD_ID, "hud/heart/cyborg_full");
    private static final ResourceLocation HEART_FULL_BLINKING =
            new ResourceLocation(RoboMod.MOD_ID, "hud/heart/cyborg_full_blinking");
    private static final ResourceLocation HEART_HALF =
            new ResourceLocation(RoboMod.MOD_ID, "hud/heart/cyborg_half");
    private static final ResourceLocation HEART_HALF_BLINKING =
            new ResourceLocation(RoboMod.MOD_ID, "hud/heart/cyborg_half_blinking");

    @Inject(method = "heartBlit", at = @At("HEAD"), cancellable = true, remap = false)
    private static void drawHeart(
            net.minecraft.client.gui.GuiGraphics guiGraphics,
            @Coerce Enum<?> type,
            int x, int y,
            boolean hardcore, boolean blinking, boolean half,
            CallbackInfo ci) {

        if (type != null && "NORMAL".equals(type.name())
                && Minecraft.getInstance().player instanceof PlayerExtension ex
                && ex.isCyborg()) {

            ResourceLocation texture = getHeartTexture(hardcore, half, blinking);

            RenderSystem.enableBlend();
            // FIX: GuiGraphics.blitSprite(ResourceLocation,int,int,int,int) was added in MC 1.20.2.
            // In 1.20.1 use blit() treating the heart texture as a standalone 9x9 texture.
            // These textures must be registered in the texture atlas via a sprites JSON, OR stored
            // as standalone PNGs at the given path. If using the atlas, switch to:
            //   Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture)
            // and use guiGraphics.blit() with the TextureAtlasSprite.
            //
            // For standalone textures (simplest approach):
            guiGraphics.blit(texture, x, y, 0, 0, 9, 9, 9, 9);
            RenderSystem.disableBlend();
            ci.cancel();
        }
    }

    private static ResourceLocation getHeartTexture(boolean hardcore, boolean half, boolean blinking) {
        if (half) {
            return blinking ? HEART_HALF_BLINKING : HEART_HALF;
        } else {
            return blinking ? HEART_FULL_BLINKING : HEART_FULL;
        }
    }
}