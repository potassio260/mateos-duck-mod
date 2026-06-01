package net.mateo.robomod.screen.slot;

import net.mateo.robomod.item.CyborgPartItem;
import net.mateo.robomod.util.CyborgPartType;
import net.mateo.robomod.util.ScreenUtil;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;

public class CyborgPartSlot extends Slot {

    private final CyborgPartType partType;

    public CyborgPartSlot(Container container, int index, int x, int y, CyborgPartType partType) {
        super(container, index, x, y);
        this.partType = partType;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof CyborgPartItem partItem
                && partItem.getPartName(partType) != null;
    }

    @Override
    public boolean mayPickup(Player player) {
        return !ScreenUtil.isUnlockExtraModule(container) || !ScreenUtil.haveExtraModuleStack(container);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}