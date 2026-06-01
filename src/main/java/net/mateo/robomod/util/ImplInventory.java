package net.mateo.robomod.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric → Forge translation notes:
 *  - Inventory            → Container
 *  - DefaultedList        → NonNullList
 *  - getStack / setStack  → getItem / setItem
 *  - removeStack          → removeItem / removeItemNoUpdate
 *  - Inventories.*        → ContainerHelper.*
 *  - markDirty()          → setChanged()
 *  - canPlayerUse()       → stillValid()
 */
public interface ImplInventory extends Container {

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    NonNullList<ItemStack> getItems();

    /** Creates an inventory backed by an existing list. */
    static ImplInventory of(NonNullList<ItemStack> items) {
        return () -> items;
    }

    /** Creates a new inventory with the specified size. */
    static ImplInventory ofSize(int size) {
        return of(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    @Override
    default int getContainerSize() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    default ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    /**
     * Removes up to {@code count} items from {@code slot}.
     * Returns whatever was actually removed.
     */
    @Override
    default ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, count);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    /** Removes and returns the entire stack in {@code slot}. */
    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(getItems(), slot);
    }

    @Override
    default void setItem(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > stack.getMaxStackSize()) {
            stack.setCount(stack.getMaxStackSize());
        }
    }

    @Override
    default void clearContent() {
        getItems().clear();
    }

    /** Override in your block entity to save data and notify neighbors. */
    @Override
    default void setChanged() {
        // override in concrete class
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }
}
