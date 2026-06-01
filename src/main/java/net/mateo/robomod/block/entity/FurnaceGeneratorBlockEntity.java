package net.mateo.robomod.block.entity;

import net.mateo.robomod.screen.FurnaceGeneratorMenu;
import net.mateo.robomod.screen.ModMenuTypes;
import net.mateo.robomod.util.transfer.BlockEnergyStorage;
import net.mateo.robomod.util.transfer.EnergyStorage;
import net.mateo.robomod.util.transfer.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FurnaceGeneratorBlockEntity extends EnergyContainer {

    private int burnTime;
    private int fuelTime;

    protected NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    protected final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> fuelTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> fuelTime = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public FurnaceGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLID_FUEL_GENERATOR.get(), pos, state);
    }

    @Override
    protected Component getContainerName() {
        return Component.translatable("container.robomod.solid_fuel_generator");
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
                            FurnaceGeneratorBlockEntity blockEntity) {
        if (blockEntity.isBurning()) {
            blockEntity.burnTime--;
            if (!blockEntity.isFull()) {
                blockEntity.setEnergyStored(
                        Math.min(blockEntity.getCapacity(),
                                blockEntity.getEnergyStored() + 16));
            }
        }

        ItemStack inputFuel  = blockEntity.inventory.get(0);
        ItemStack outputFuel = blockEntity.inventory.get(1);

        if (!blockEntity.isBurning() && !blockEntity.isFull()) {
            blockEntity.burnTime = blockEntity.getFuelTime(inputFuel);
            blockEntity.fuelTime = blockEntity.burnTime;

            if (blockEntity.isBurning()) {
                blockEntity.setChanged();

                if (!inputFuel.isEmpty()) {
                    Item remainder = inputFuel.getItem().getCraftingRemainingItem();

                    inputFuel.shrink(1);

                    if (remainder != null) {
                        ItemStack remainderStack = new ItemStack(remainder);
                        if (outputFuel.isEmpty()) {
                            blockEntity.inventory.set(1, remainderStack);
                        } else if (outputFuel.getItem() == remainder
                                && outputFuel.getCount() < outputFuel.getMaxStackSize()) {
                            outputFuel.grow(1);
                        } else if (inputFuel.isEmpty()) {
                            blockEntity.inventory.set(0, remainderStack);
                        } else {
                            Block.popResource(level, pos, remainderStack);
                        }
                    }
                }
            }
        }

        state = state.setValue(AbstractFurnaceBlock.LIT, blockEntity.isBurning());
        level.setBlock(pos, state, Block.UPDATE_ALL);

        EnergyBlockEntity.BatteryTick(level, pos, state, blockEntity);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0;
    }

    // -----------------------------------------------------------------------
    // FIX: was passing (syncId, playerInventory, this, containerData, getBlockPos())
    // which matched no constructor. Now uses the full server-side constructor that
    // accepts Container (the block entity itself) directly.
    // The block's use() method must call NetworkHooks.openScreen with a buf writer
    // that writes getBlockPos() so the client constructor can read it back.
    // -----------------------------------------------------------------------
    @Override
    protected AbstractContainerMenu createScreenHandler(int syncId, Inventory playerInventory) {
        return new FurnaceGeneratorMenu(
                ModMenuTypes.FURNACE_GENERATOR_MENU.get(),
                syncId,
                playerInventory,
                this,           // FurnaceGeneratorBlockEntity implements Container via EnergyContainer chain
                containerData,
                getBlockPos(),
                playerInventory.player instanceof ServerPlayer sp ? sp : null
        );
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) return 0;
        return ForgeHooks.getBurnTime(fuel, net.minecraft.world.item.crafting.RecipeType.SMELTING) / 10;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.GENERATOR;
    }

    @Override
    public int getTransferRate() {
        return 64;
    }

    @Override
    public int getCapacity() {
        return 64_000;
    }

    @Override
    boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType) {
        return false;
    }

    @Override
    boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType) {
        return true;
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        for (Direction direction : Direction.values()) {
            directionMap.put(direction, BlockEnergyStorage.TypeIO.OUTPUT);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putShort("BurnTime", (short) burnTime);
        tag.putShort("FuelTime", (short) fuelTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, inventory);
        burnTime = tag.getShort("BurnTime");
        fuelTime = tag.getShort("FuelTime");
    }
}