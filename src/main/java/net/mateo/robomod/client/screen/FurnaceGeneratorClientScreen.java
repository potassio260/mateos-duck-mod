package net.mateo.robomod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.client.util.EnergySynchronization;
import net.mateo.robomod.screen.FurnaceGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class FurnaceGeneratorClientScreen extends AbstractContainerScreen<FurnaceGeneratorMenu> {

    public FurnaceGeneratorClientScreen(FurnaceGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    private static final ResourceLocation TEXTURE      = new ResourceLocation(RoboMod.MOD_ID, "textures/gui/container/furnace_generator.png");
    private static final ResourceLocation ENERGY_BAR   = new ResourceLocation(RoboMod.MOD_ID, "textures/gui/container/energy_bar.png");
    private static final ResourceLocation LIT_PROGRESS = new ResourceLocation(RoboMod.MOD_ID, "textures/gui/container/furnace_generator_lit_progress.png");

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, 179, imageHeight);

        if (this.menu.isBurning()) {
            int litHeight = Mth.ceil(this.menu.getFuelProgress() * 13.0F) + 1;
            guiGraphics.blit(LIT_PROGRESS,
                    this.leftPos + 80, this.topPos + 36 + 14 - litHeight,
                    0, 14 - litHeight,
                    14, litHeight,
                    14, 14);
        }

        int energyBarHeight = (int)(68 * Mth.clamp(
                (float) EnergySynchronization.getEnergy()[0] / EnergySynchronization.getEnergy()[1],
                0.0F, 1.0F)) + 1;
        guiGraphics.blit(ENERGY_BAR,
                this.leftPos + 156, this.topPos + 78 - energyBarHeight,
                0, 69 - energyBarHeight,
                12, energyBarHeight,
                12, 69);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.isHovering(156, 9, 12, 68, mouseX, mouseY)) {
            // FIX: renderTooltip does not accept List<MutableComponent> — convert to FormattedCharSequence
            List<FormattedCharSequence> lines = List.of(
                    Component.literal("Energy Stored: ").getVisualOrderText(),
                    Component.literal(EnergySynchronization.getEnergy()[0] + "/" + EnergySynchronization.getEnergy()[1]).getVisualOrderText()
            );
            guiGraphics.renderTooltip(this.font, lines, mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }
}