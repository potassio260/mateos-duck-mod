package net.mateo.robomod.item;

import net.mateo.robomod.block.ControllerBlock;
import net.mateo.robomod.extension.PlayerExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// Replaces Fabric data components (RETREAT_MODULE_COMPONENT / RETREAT_MODULE_STATUS_COMPONENT)
// with plain ItemStack NBT tags, since data components don't exist in Forge 1.20.1
public class RetreatModule extends CyborgModuleItem {

    private static final String TAG_POS       = "RetreatPos";
    private static final String TAG_DIM       = "RetreatDim";
    private static final String TAG_ACTIVATED = "RetreatActivated";

    public RetreatModule(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ServerLevel level, Player player, PlayerExtension extension, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_POS) || !(player instanceof ServerPlayer serverPlayer)) return;

        boolean activated = tag.getBoolean(TAG_ACTIVATED);

        if (extension.getEnergyStored() <= 0) {
            if (!activated) {
                BlockPos pos = BlockPos.of(tag.getLong(TAG_POS));
                String dimStr = tag.getString(TAG_DIM);
                ResourceKey<Level> dimKey = ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION,
                        new ResourceLocation(dimStr));
                ServerLevel targetLevel = serverPlayer.server.getLevel(dimKey);

                if (targetLevel != null) {
                    BlockState blockState = targetLevel.getBlockState(pos);
                    if (blockState.getBlock() instanceof ControllerBlock) {
                        // Replaces serverPlayer.teleport(..., facing.asRotation(), 0)
                        serverPlayer.teleportTo(targetLevel,
                                pos.getX() + 0.5,
                                pos.getY() + 1,
                                pos.getZ() + 0.5,
                                blockState.getValue(ControllerBlock.FACING).toYRot(),
                                0);
                    }
                }
                tag.putBoolean(TAG_ACTIVATED, true);
            }
        } else if (activated) {
            tag.putBoolean(TAG_ACTIVATED, false);
        }
    }

    @Override
    public void controllerLogic(ControllerBlock controllerBlock, BlockPos pos, Level level, Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong(TAG_POS, pos.asLong());
        // Replaces world.getRegistryKey() → level.dimension().location()
        tag.putString(TAG_DIM, level.dimension().location().toString());
        tag.putBoolean(TAG_ACTIVATED, false);
    }
}
