package net.mateo.robomod.item;

import net.mateo.robomod.block.ControllerBlock;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class BaseCyborgModuleItem extends Item {

    public BaseCyborgModuleItem(Properties properties) {
        super(properties);
    }

    public void tick(ServerLevel level, Player player, PlayerExtension extension, ItemStack stack) {
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick(net.minecraft.client.multiplayer.ClientLevel level, Player player, PlayerExtension extension) {
    }

    public void onModuleRemoved(Level level, Player player) {
    }

    public void controllerLogic(ControllerBlock controllerBlock, BlockPos pos, Level level, Player player, ItemStack stack) {
    }
}
