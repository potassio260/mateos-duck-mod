package net.mateo.robomod.block.entity;

import net.mateo.robomod.util.ImplInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for energy machines that also have an inventory + GUI.
 */
public abstract class EnergyContainer extends EnergyBlockEntity
        implements Nameable, MenuProvider, ImplInventory, WorldlyContainer {

    @Nullable
    private Component customName;

    // =======================================================================
    // VANILLA GUI SYNC: Splits 32-bit energy integers into 16-bit chunks
    // =======================================================================
    protected final ContainerData energyData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> getEnergyStored() & 0xFFFF;
                case 1 -> (getEnergyStored() >> 16) & 0xFFFF;
                case 2 -> getCapacity() & 0xFFFF;
                case 3 -> (getCapacity() >> 16) & 0xFFFF;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Usually handled server-side by the machine logic, empty is fine here
        }

        @Override
        public int getCount() {
            return 4; // 2 slots for current energy, 2 slots for max capacity
        }
    };

    public ContainerData getEnergyData() {
        return energyData;
    }

    public EnergyContainer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // -----------------------------------------------------------------------
    // WorldlyContainer
    // -----------------------------------------------------------------------
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    // -----------------------------------------------------------------------
    // Container
    // -----------------------------------------------------------------------
    @Override
    public boolean stillValid(Player player) {
        return net.minecraft.world.Container.stillValidBlockEntity(this, player);
    }

    // -----------------------------------------------------------------------
    // Nameable / MenuProvider
    // -----------------------------------------------------------------------
    @Override
    public Component getName() {
        return customName != null ? customName : getContainerName();
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return customName;
    }

    protected abstract Component getContainerName();

    public void openScreen(net.minecraft.server.level.ServerPlayer serverPlayer) {
        NetworkHooks.openScreen(serverPlayer, this,
                buf -> buf.writeBlockPos(getBlockPos()));
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
                                            Player player) {
        return createScreenHandler(containerId, playerInventory);
    }

    protected abstract AbstractContainerMenu createScreenHandler(int syncId,
                                                                 Inventory playerInventory);

    // -----------------------------------------------------------------------
    // NBT
    // -----------------------------------------------------------------------
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(customName));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CustomName")) {
            customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }
}