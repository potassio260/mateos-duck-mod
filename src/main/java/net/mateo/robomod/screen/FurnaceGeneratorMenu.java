package net.mateo.robomod.screen;

import net.mateo.robomod.block.entity.FurnaceGeneratorBlockEntity;
import net.mateo.robomod.network.ModPackets;
import net.mateo.robomod.network.packet.EnergyGuiUpdatePacket;
import net.mateo.robomod.screen.slot.BucketOutputSlot;
import net.mateo.robomod.screen.slot.FurnaceFuelSlot;
import net.mateo.robomod.util.ImplInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.server.level.ServerPlayer;

import static net.mateo.robomod.screen.ModMenuTypes.FURNACE_GENERATOR_MENU;

public class FurnaceGeneratorMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData containerData;
    private final ServerPlayer serverPlayer;
    private FurnaceGeneratorBlockEntity furnaceGeneratorBlock;

    // -----------------------------------------------------------------------
    // CLIENT-SIDE constructor — called by IForgeMenuType (network open).
    // IForgeMenuType.create() always passes (containerId, playerInventory, FriendlyByteBuf).
    // We write the BlockPos into the buf server-side (in the block's use() via
    // NetworkHooks.openScreen) and read it back here.
    // FIX: was (int, Inventory, BlockPos) — FriendlyByteBuf cannot be converted to BlockPos.
    // -----------------------------------------------------------------------
    public FurnaceGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(FURNACE_GENERATOR_MENU.get(), containerId, playerInventory,
                ImplInventory.ofSize(2), new SimpleContainerData(2), buf.readBlockPos(), null);
    }

    // -----------------------------------------------------------------------
    // SERVER-SIDE constructor — called from FurnaceGeneratorBlockEntity.createScreenHandler.
    // FIX: accepts Container (supertype) instead of ImplInventory so the block
    // entity can pass `this` directly (block entities implement Container).
    // -----------------------------------------------------------------------
    public FurnaceGeneratorMenu(MenuType<?> type, int containerId, Inventory playerInventory,
                                Container inventory, ContainerData containerData,
                                BlockPos pos, ServerPlayer serverPlayer) {
        super(type, containerId);
        this.serverPlayer = serverPlayer;
        if (serverPlayer != null && !serverPlayer.level().isClientSide()) {
            this.furnaceGeneratorBlock = (FurnaceGeneratorBlockEntity) serverPlayer.level().getBlockEntity(pos);
        }
        checkContainerSize(inventory, 2);
        checkContainerDataCount(containerData, 2);
        this.container     = inventory;
        this.containerData = containerData;

        this.addSlot(new FurnaceFuelSlot(inventory,  0, 80, 17));
        this.addSlot(new BucketOutputSlot(inventory, 1, 80, 53));

        // Player inventory (3 rows)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addDataSlots(this.containerData);
    }

    public boolean isBurning() {
        return this.containerData.get(0) > 0;
    }

    public float getFuelProgress() {
        int burnTime = this.containerData.get(1);
        if (burnTime == 0) burnTime = 200;
        return Mth.clamp((float) this.containerData.get(0) / burnTime, 0.0F, 1.0F);
    }

    @Override
    public void broadcastChanges() {
        if (serverPlayer != null && furnaceGeneratorBlock != null) {
            ModPackets.sendToPlayer(serverPlayer,
                    new EnergyGuiUpdatePacket(
                            furnaceGeneratorBlock.getEnergyStored(),
                            furnaceGeneratorBlock.getCapacity()));
        }
        super.broadcastChanges();
    }

    @Override
    public void removed(Player player) {
        if (player instanceof ServerPlayer sp) {
            ModPackets.sendToPlayer(sp, new EnergyGuiUpdatePacket(0, 0));
        }
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 1) {
                if (!this.moveItemStackTo(itemStack2, 3, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot2.onQuickCraft(itemStack2, itemStack);
            } else if (slot != 0) {
                if (AbstractFurnaceBlockEntity.isFuel(itemStack2)) {
                    if (!this.moveItemStackTo(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= 2 && slot < 29) {
                    if (!this.moveItemStackTo(itemStack2, 30, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= 29 && slot < 38 && !this.moveItemStackTo(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.set(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTake(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}