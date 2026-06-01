package net.mateo.robomod.item;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.client.util.ClientOreHighlightData;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XrayVisionModule extends CyborgModuleItem {

    // Replaces TagKey.of(RegistryKeys.BLOCK, ...) → TagKey.create(Registries.BLOCK, ...)
    public static final TagKey<Block> XRAY_VISIBLE_ORE =
            TagKey.create(net.minecraft.core.registries.Registries.BLOCK, RoboMod.id("xray_visible_ore"));

    private static final int RADIUS = 8;
    static int alpha = 55;

    public XrayVisionModule(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ServerLevel level, Player player, PlayerExtension extension, ItemStack stack) {
        // Replaces player.age → player.tickCount
        if (player.tickCount % 100L == 0L && extension.getEnergyStored() > 0
                && !player.isCreative() && !player.isSpectator()) {
            extension.setEnergyStored(Math.max(extension.getEnergyStored() - 500, 0));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(ClientLevel level, Player player, PlayerExtension extension) {
        if (alpha > 0) alpha--;

        if (player.tickCount % 100L == 0L) {
            if (extension.getEnergyStored() > 0) {
                highlightNearbyOres(level, player.blockPosition());
            }
        }
        if (extension.getEnergyStored() <= 0 && !ClientOreHighlightData.isEmpty()) {
            ClientOreHighlightData.clearHighlights();
        }
    }

    public static float getAlpha() {
        return (float) alpha / 100F;
    }

    @Override
    public void onModuleRemoved(Level level, Player player) {
        if (level.isClientSide()) {
            if (!ClientOreHighlightData.isEmpty()) ClientOreHighlightData.clearHighlights();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Highlights nearby ores through"));
        tooltip.add(Component.literal("walls every §f5 §7seconds."));
    }

    @OnlyIn(Dist.CLIENT)
    private void highlightNearbyOres(ClientLevel level, BlockPos center) {
        ClientOreHighlightData.clearHighlights();
        alpha = 55;

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    BlockPos pos = center.offset(x, y, z); // Replaces center.add(x,y,z)
                    if (isVisibleOre(level.getBlockState(pos))) {
                        ClientOreHighlightData.addHighlight(pos);
                    }
                }
            }
        }
    }

    private boolean isVisibleOre(BlockState state) {
        // Replaces state.isIn(tag) → state.is(tag)
        return state.is(XRAY_VISIBLE_ORE);
    }
}
