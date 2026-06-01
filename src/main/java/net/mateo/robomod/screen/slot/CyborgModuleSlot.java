package net.mateo.robomod.screen.slot;

import net.mateo.robomod.item.BaseCyborgModuleItem;
import net.mateo.robomod.util.ScreenUtil;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;

public class CyborgModuleSlot extends Slot {

    private final int[] slotsIndex;
    private final int index;
    private final boolean lockable;

    public CyborgModuleSlot(Container container, int index, int x, int y, int[] slotsIndex) {
        super(container, index, x, y);
        this.slotsIndex = slotsIndex;
        this.index      = index;
        this.lockable   = false;
    }

    public CyborgModuleSlot(Container container, int index, int x, int y, int[] slotsIndex, boolean lockable) {
        super(container, index, x, y);
        this.slotsIndex = slotsIndex;
        this.index      = index;
        this.lockable   = lockable;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.getItem() instanceof BaseCyborgModuleItem module
                && (!lockable || ScreenUtil.isUnlockExtraModule(container))) {
            for (int slotIdx : slotsIndex) {
                if (slotIdx != this.index) {
                    ItemStack existing = container.getItem(slotIdx);
                    if (!existing.isEmpty() && existing.getItem() == module) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
