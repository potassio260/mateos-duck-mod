package net.mateo.robomod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mateo.robomod.RoboMod;
import net.mateo.robomod.client.render.CyborgPartRenderer;
import net.mateo.robomod.client.render.CyborgPartRenderers;
import net.mateo.robomod.item.ModItems;
import net.mateo.robomod.screen.AssemblerMenu;
import net.mateo.robomod.util.CyborgPartType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import com.mojang.blaze3d.platform.Lighting;
import org.joml.Vector3f;

import java.util.List;

public class AssemblerClientScreen extends AbstractContainerScreen<AssemblerMenu> {

    public AssemblerClientScreen(AssemblerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    private static final ResourceLocation TEXTURE     = new ResourceLocation(RoboMod.MOD_ID, "textures/gui/container/assembler.png");
    private static final ResourceLocation ENERGY_BAR  = new ResourceLocation(RoboMod.MOD_ID, "textures/gui/sprites/container/energy_bar.png");

    private static final ResourceLocation SLOT        = new ResourceLocation(RoboMod.MOD_ID, "textures/gui/sprites/container/slot.png");
    private static final ResourceLocation LOCKED_SLOT = new ResourceLocation(RoboMod.MOD_ID, "textures/gui/sprites/container/locked_slot.png");

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawCyborg(guiGraphics, x + 26, y + 8, x + 20, y + 78);

        guiGraphics.blit(TEXTURE, x, y, 20, 0, 0, 179, imageHeight, 256, 256);
        guiGraphics.pose().translate(0, 0, 100);

        blitTexture(guiGraphics, SLOT, this.leftPos + 79,  this.topPos + 12, 18, 18);
        blitTexture(guiGraphics, SLOT, this.leftPos + 79,  this.topPos + 34, 18, 18);
        blitTexture(guiGraphics, SLOT, this.leftPos + 101, this.topPos + 24, 18, 18);
        blitTexture(guiGraphics, SLOT, this.leftPos + 57,  this.topPos + 24, 18, 18);
        blitTexture(guiGraphics, SLOT, this.leftPos + 95,  this.topPos + 56, 18, 18);
        blitTexture(guiGraphics, SLOT, this.leftPos + 63,  this.topPos + 56, 18, 18);
        blitTexture(guiGraphics, SLOT, this.leftPos + 29,  this.topPos + 11, 18, 18);

        int energy = this.menu.getEnergy();
        int capacity = this.menu.getCapacity();
        float ratio = capacity > 0 ? (float) energy / capacity : 0.0F;

        int energyBarHeight = (int)(68 * Mth.clamp(ratio, 0.0F, 1.0F)) + 1;
        guiGraphics.blit(ENERGY_BAR, this.leftPos + 156, this.topPos + 78 - energyBarHeight,
                0, 69 - energyBarHeight, 12, energyBarHeight, 12, 69);

        if (this.getMenu().isBlockedExtraModuleSlots()) {
            blitTexture(guiGraphics, LOCKED_SLOT, this.leftPos + 29, this.topPos + 11, 18, 18);
        }
        if (this.getMenu().isBlockedPartsSlots()) {
            blitTexture(guiGraphics, LOCKED_SLOT, this.leftPos + 79,  this.topPos + 12, 18, 18);
            blitTexture(guiGraphics, LOCKED_SLOT, this.leftPos + 79,  this.topPos + 34, 18, 18);
            blitTexture(guiGraphics, LOCKED_SLOT, this.leftPos + 101, this.topPos + 24, 18, 18);
            blitTexture(guiGraphics, LOCKED_SLOT, this.leftPos + 57,  this.topPos + 24, 18, 18);
            blitTexture(guiGraphics, LOCKED_SLOT, this.leftPos + 95,  this.topPos + 56, 18, 18);
            blitTexture(guiGraphics, LOCKED_SLOT, this.leftPos + 63,  this.topPos + 56, 18, 18);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.isHovering(154, 9, 12, 68, mouseX, mouseY)) {
            List<FormattedCharSequence> lines = List.of(
                    Component.literal("Energy Stored: ").getVisualOrderText(),
                    Component.literal(this.menu.getEnergy() + "/" + this.menu.getCapacity()).getVisualOrderText()
            );
            guiGraphics.renderTooltip(this.font, lines, mouseX, mouseY);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY = 4;
    }

    public void drawCyborg(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        float x = (float)(x1 + x2) / 2.0F;
        float y = (float)(y1 + y2) / 2.0F;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y + 31.6, 50);
        poseStack.scale(30, 30, -30);

        Lighting.setupForEntityInInventory();

        CyborgPartRenderer renderer = CyborgPartRenderers.get(ModItems.BASIC_HEAD.get().getDefaultInstance(), CyborgPartType.HEAD);

        MultiBufferSource.BufferSource bufferSource = guiGraphics.bufferSource();

        if (renderer != null && renderer.model != null && renderer.model.get() != null) {
            renderPart(poseStack, renderer.model.get().getHead(),     guiGraphics, bufferSource, 0,     0,  new Vector3f(0, 0, 0));
            renderPart(poseStack, renderer.model.get().getBody(),     guiGraphics, bufferSource, 0,     0,  new Vector3f());
            renderPart(poseStack, renderer.model.get().getLeftArm(),  guiGraphics, bufferSource, 5,     2,  new Vector3f(0, 0, (float) Math.toRadians(-125)));
            renderPart(poseStack, renderer.model.get().getLeftArm(),  guiGraphics, bufferSource, 5,     2,  new Vector3f(0, 0, (float) Math.toRadians(-90)));
            renderPart(poseStack, renderer.model.get().getRightArm(), guiGraphics, bufferSource, -5,    2,  new Vector3f(0, 0, (float) Math.toRadians(125)));
            renderPart(poseStack, renderer.model.get().getRightArm(), guiGraphics, bufferSource, -5,    2,  new Vector3f(0, 0, (float) Math.toRadians(90)));
            renderPart(poseStack, renderer.model.get().getLeftLeg(),  guiGraphics, bufferSource, 1.9f,  10, new Vector3f(0, 0, (float) Math.toRadians(-35)));
            renderPart(poseStack, renderer.model.get().getLeftLeg(),  guiGraphics, bufferSource, 1.9f,  10, new Vector3f());
            renderPart(poseStack, renderer.model.get().getRightLeg(), guiGraphics, bufferSource, -1.9f, 10, new Vector3f(0, 0, (float) Math.toRadians(35)));
            renderPart(poseStack, renderer.model.get().getRightLeg(), guiGraphics, bufferSource, -1.9f, 10, new Vector3f());
        }

        guiGraphics.flush();
        poseStack.popPose();

        Lighting.setupFor3DItems();
    }

    private static void renderPart(PoseStack poseStack, ModelPart model, GuiGraphics guiGraphics,
                                   MultiBufferSource bufferSource, float x2, float y2, Vector3f rotate) {
        poseStack.pushPose();
        model.resetPose();
        poseStack.translate(2.17, -1.51, 0.5);
        model.offsetPos(new Vector3f(x2, y2, 0));
        model.offsetRotation(rotate);
        poseStack.scale(1.3f, 1.3f, 1.3f);

        var vertexConsumer = bufferSource.getBuffer(
                RenderType.entityCutoutNoCull(new ResourceLocation(RoboMod.MOD_ID, "textures/entity/cyborg_gui.png")));

        model.render(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static void blitTexture(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int w, int h) {
        RenderSystem.setShaderTexture(0, texture);
        guiGraphics.blit(texture, x, y, 0, 0, w, h, w, h);
    }
}