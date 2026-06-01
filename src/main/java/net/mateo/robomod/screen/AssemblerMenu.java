package net.mateo.robomod.screen;

import net.mateo.robomod.screen.slot.CyborgModuleSlot;
import net.mateo.robomod.screen.slot.CyborgPartSlot;
import net.mateo.robomod.util.CyborgPartType;
import net.mateo.robomod.util.ImplInventory;
import net.mateo.robomod.util.ScreenUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import static net.mateo.robomod.screen.ModMenuTypes.ASSEMBLER_MENU;

public class AssemblerMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;

    public static final int[] MODULE_SLOTS       = {6, 7, 8, 9};
    public static final int[] EXTRA_MODULE_SLOTS = {9};

    // -----------------------------------------------------------------------
    // CLIENT-SIDE constructor
    // -----------------------------------------------------------------------
    public AssemblerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        // We pass a SimpleContainerData on the client side to receive the synced ints
        this(ASSEMBLER_MENU.get(), containerId, playerInventory, ImplInventory.ofSize(10), new SimpleContainerData(4));
    }

    // -----------------------------------------------------------------------
    // SERVER-SIDE constructor
    // -----------------------------------------------------------------------
    public AssemblerMenu(MenuType<?> type, int containerId, Inventory playerInventory,
                         Container inventory, ContainerData data) {
        super(type, containerId);
        checkContainerSize(inventory, 10);
        checkContainerDataCount(data, 4); // Ensures we have our 4 energy integers
        this.container = inventory;
        this.data = data;

        this.addSlot(new CyborgPartSlot(inventory, 0,  80, 13,  CyborgPartType.HEAD));
        this.addSlot(new CyborgPartSlot(inventory, 1,  80, 35,  CyborgPartType.BODY));
        this.addSlot(new CyborgPartSlot(inventory, 2, 102, 25,  CyborgPartType.RIGHT_ARM));
        this.addSlot(new CyborgPartSlot(inventory, 3,  58, 25,  CyborgPartType.LEFT_ARM));
        this.addSlot(new CyborgPartSlot(inventory, 4,  96, 57,  CyborgPartType.RIGHT_LEG));
        this.addSlot(new CyborgPartSlot(inventory, 5,  64, 57,  CyborgPartType.LEFT_LEG));

        this.addSlot(new CyborgModuleSlot(inventory, 6,  8, 12, MODULE_SLOTS));
        this.addSlot(new CyborgModuleSlot(inventory, 7,  8, 30, MODULE_SLOTS));
        this.addSlot(new CyborgModuleSlot(inventory, 8,  8, 48, MODULE_SLOTS));
        this.addSlot(new CyborgModuleSlot(inventory, 9, 30, 12, MODULE_SLOTS, true));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        // CRITICAL: Tells vanilla Minecraft to automatically sync these integers to the client
        this.addDataSlots(data);
    }

    // --- RECONSTRUCT 32-BIT INTEGERS ---
    public int getEnergy() {
        return (this.data.get(1) << 16) | (this.data.get(0) & 0xFFFF);
    }

    public int getCapacity() {
        return (this.data.get(3) << 16) | (this.data.get(2) & 0xFFFF);
    }

    public boolean isBlockedPartsSlots() {
        return ScreenUtil.haveExtraModuleStack(container);
    }

    public boolean isBlockedExtraModuleSlots() {
        return !ScreenUtil.isUnlockExtraModule(container);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}