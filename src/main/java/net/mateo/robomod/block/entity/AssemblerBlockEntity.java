package net.mateo.robomod.block.entity;

import net.mateo.robomod.item.BaseCyborgModuleItem;
import net.mateo.robomod.item.CyborgPartItem;
import net.mateo.robomod.screen.AssemblerMenu;
import net.mateo.robomod.screen.ModMenuTypes;
import net.mateo.robomod.util.CyborgPartType;
import net.mateo.robomod.util.ImplInventory;
import net.mateo.robomod.util.ScreenUtil;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Map;

public class AssemblerBlockEntity extends EnergyContainer {

    public int tickError;
    public boolean reverse;

    protected NonNullList<ItemStack> inventory = NonNullList.withSize(10, ItemStack.EMPTY);

    @Override
    protected Component getContainerName() {
        return Component.translatable("container.robomod.assembler");
    }

    @Override
    protected AbstractContainerMenu createScreenHandler(int syncId, Inventory playerInventory) {
        ImplInventory implInventory = ImplInventory.of(this.inventory);
        return new AssemblerMenu(
                ModMenuTypes.ASSEMBLER_MENU.get(),
                syncId,
                playerInventory,
                implInventory,
                this.energyData // Passes the split integers directly to the menu!
        );
    }

    @Override
    public IEnergyStorage.Type typeMachine() {
        return IEnergyStorage.Type.RECEIVER;
    }

    @Override
    public int getTransferRate() {
        return 64;
    }

    @Override
    public int getCapacity() {
        return 128_000;
    }

    @Override
    boolean canInsertEnergy(EnergyStorage source, IEnergyStorage.Type sourceType) {
        return true;
    }

    @Override
    boolean canExtractEnergy(EnergyStorage target, IEnergyStorage.Type sourceType) {
        return target.type() == IEnergyStorage.Type.CYBORG;
    }

    @Override
    public void getDirectionsIO(Map<Direction, BlockEnergyStorage.TypeIO> directionMap) {
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP) {
                directionMap.put(direction, BlockEnergyStorage.TypeIO.INPUT);
            }
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    public AssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ASSEMBLER.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        // Stretches the render box 2 blocks upwards to cover the tall antenna
        return new AABB(this.getBlockPos()).expandTowards(0, 2.0, 0);
    }

    // -----------------------------------------------------------------------
    // NBT
    // -----------------------------------------------------------------------
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, inventory);
    }

    // -----------------------------------------------------------------------
    // Assembler logic
    // -----------------------------------------------------------------------
    public boolean isComplete() {
        return !getHead().isEmpty()     && isValid(getHead(),     CyborgPartType.HEAD)
                && !getBody().isEmpty()     && isValid(getBody(),     CyborgPartType.BODY)
                && !getRightArm().isEmpty() && isValid(getRightArm(), CyborgPartType.RIGHT_ARM)
                && !getLeftArm().isEmpty()  && isValid(getLeftArm(),  CyborgPartType.LEFT_ARM)
                && !getRightLeg().isEmpty() && isValid(getRightLeg(), CyborgPartType.RIGHT_LEG)
                && !getLeftLeg().isEmpty()  && isValid(getLeftLeg(),  CyborgPartType.LEFT_LEG);
    }

    public boolean containsModule(Item moduleStack) {
        return moduleStack instanceof BaseCyborgModuleItem module
                && ((!getModule(1).isEmpty() && getModule(1).getItem().equals(module))
                ||  (!getModule(2).isEmpty() && getModule(2).getItem().equals(module))
                ||  (!getModule(3).isEmpty() && getModule(3).getItem().equals(module)));
    }

    public ItemStack getModule(int id) {
        return switch (id) {
            case 1 -> getItems().get(6);
            case 2 -> getItems().get(7);
            case 3 -> getItems().get(8);
            case 4 -> getItems().get(9);
            default -> ItemStack.EMPTY;
        };
    }

    public void setModule(int id, ItemStack module) {
        switch (id) {
            case 1 -> getItems().set(6, module);
            case 2 -> getItems().set(7, module);
            case 3 -> getItems().set(8, module);
            case 4 -> getItems().set(9, module);
        }
    }

    public boolean addModule(ItemStack module) {
        if (getModule(1).isEmpty())                                                        { setModule(1, module); return true; }
        else if (getModule(2).isEmpty())                                                   { setModule(2, module); return true; }
        else if (getModule(3).isEmpty())                                                   { setModule(3, module); return true; }
        else if (ScreenUtil.isUnlockExtraModule(inventory) && getModule(4).isEmpty())      { setModule(4, module); return true; }
        return false;
    }

    public boolean hasEmptyModuleSlot() {
        return getModule(1).isEmpty() || getModule(2).isEmpty()
                || getModule(3).isEmpty() || getModule(4).isEmpty();
    }

    public boolean isValid(ItemStack itemStack, CyborgPartType partType) {
        if (itemStack.getItem() instanceof CyborgPartItem partItem) {
            return partItem.getPartName(partType) != null;
        }
        return false;
    }

    public ItemStack getHead()     { return getItems().get(0); }
    public ItemStack getBody()     { return getItems().get(1); }
    public ItemStack getRightArm() { return getItems().get(2); }
    public ItemStack getLeftArm()  { return getItems().get(3); }
    public ItemStack getRightLeg() { return getItems().get(4); }
    public ItemStack getLeftLeg()  { return getItems().get(5); }

    public ItemStack getPartStack(CyborgPartType partType) {
        return switch (partType) {
            case HEAD      -> getHead();
            case BODY      -> getBody();
            case RIGHT_ARM -> getRightArm();
            case LEFT_ARM  -> getLeftArm();
            case RIGHT_LEG -> getRightLeg();
            case LEFT_LEG  -> getLeftLeg();
        };
    }

    public void setPartStack(CyborgPartType partType, ItemStack stack) {
        int id = switch (partType) {
            case HEAD      -> 0;
            case BODY      -> 1;
            case RIGHT_ARM -> 2;
            case LEFT_ARM  -> 3;
            case RIGHT_LEG -> 4;
            case LEFT_LEG  -> 5;
        };
        setItem(id, stack);
    }

    // -----------------------------------------------------------------------
    // Network sync
    // -----------------------------------------------------------------------

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        // Force the server to sync the new inventory to the client immediately
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void updateListeners() {
        setChanged();
    }

    // -----------------------------------------------------------------------
    // Error animation
    // -----------------------------------------------------------------------
    public static void animateError(Level level, BlockPos pos, BlockState state,
                                    AssemblerBlockEntity blockEntity) {
        if (blockEntity.tickError <= 255 && !blockEntity.reverse) {
            blockEntity.tickError += 16;
        } else {
            blockEntity.reverse = true;
        }
        if (blockEntity.tickError > 60 && blockEntity.reverse) {
            blockEntity.tickError -= 16;
        } else {
            blockEntity.reverse = false;
        }
    }
}