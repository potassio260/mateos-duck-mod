package net.mateo.robomod.item;

import net.mateo.robomod.block.entity.EnergyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugEnergyStick extends Item {

    // NBT key used to store the tracked block position — replaces DataComponentTypes.LODESTONE_TRACKER
    private static final String TAG_POS = "TrackedPos";
    private static final String TAG_DIM = "TrackedDim";

    public DebugEnergyStick(Properties properties) {
        super(properties);
    }

    private static void sendMessage(Player player, Component message) {
        // Replaces ServerPlayerEntity.sendMessageToClient(..., true) → action bar message
        ((ServerPlayer) player).displayClientMessage(message, true);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();

        if (level.getBlockEntity(blockPos) instanceof EnergyBlockEntity) {
            level.playSound(null, blockPos, SoundEvents.AMETHYST_CLUSTER_BREAK,
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            // Store tracked pos in NBT — replaces LodestoneTrackerComponent / DataComponentTypes
            ItemStack stack = context.getItemInHand();
            CompoundTag tag = stack.getOrCreateTag();
            tag.putLong(TAG_POS, blockPos.asLong());
            tag.putString(TAG_DIM, level.dimension().location().toString());

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && stack.hasTag() && entity instanceof ServerPlayer player) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(TAG_POS)) {
                BlockPos trackedPos = BlockPos.of(tag.getLong(TAG_POS));
                if (level.getBlockEntity(trackedPos) instanceof EnergyBlockEntity energy) {
                    sendMessage(player, Component.literal(
                            energy.typeMachine().toString() + "§a Stored:" + energy.getEnergyStored()));
                }
            }
        }
    }
}
