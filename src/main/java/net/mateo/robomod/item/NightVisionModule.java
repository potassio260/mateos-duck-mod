package net.mateo.robomod.item;

import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NightVisionModule extends CyborgModuleItem {

    static int nightVisionStrength = 0;

    public NightVisionModule(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ServerLevel level, Player player, PlayerExtension ex, ItemStack stack) {
        if (!player.isCreative() && !player.isSpectator() && ex.getEnergyStored() > 0) {
            ex.setEnergyStored(Math.max(ex.getEnergyStored() - 2, 0));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick(ClientLevel level, Player player, PlayerExtension extension) {
        if (extension.getEnergyStored() > 2) {
            if (nightVisionStrength < 100) {
                nightVisionStrength++;
            }
        }
    }

    @Override
    public void onModuleRemoved(Level level, Player player) {
        if (level.isClientSide()) nightVisionStrength = 0;
    }

    public static float getNightVisionStrength() {
        return (float) nightVisionStrength / 100F;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Grants night vision status effect."));
    }
}
