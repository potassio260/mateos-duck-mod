package net.mateo.robomod.util;

import net.mateo.robomod.RoboMod;
import net.mateo.robomod.screen.AssemblerMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * FIX: ModItems.UNLOCKS_MODULE_SLOT must be a TagKey<Item>, not a RegistryObject<Item>.
 * ItemStack.is(TagKey<Item>) is the correct check in 1.20.1.
 *
 * Declare in ModItems (or here as a standalone constant — preferred):
 *   public static final TagKey<Item> UNLOCKS_MODULE_SLOT =
 *       ItemTags.create(new ResourceLocation(RoboMod.MOD_ID, "unlocks_module_slot"));
 *
 * Then add a JSON tag file at:
 *   src/main/resources/data/robomod/tags/items/unlocks_module_slot.json
 * listing whichever items unlock the extra module slot.
 */
public class ScreenUtil {

    /**
     * Items carrying this tag unlock the extra module slot in the Assembler.
     * The tag is defined in data/robomod/tags/items/unlocks_module_slot.json.
     */
    public static final TagKey<Item> UNLOCKS_MODULE_SLOT_TAG =
            ItemTags.create(new ResourceLocation(RoboMod.MOD_ID, "unlocks_module_slot"));

    public static boolean isUnlockExtraModule(Container inventory) {
        for (int i = 0; i < 6; i++) {
            // FIX: was ModItems.UNLOCKS_MODULE_SLOT (RegistryObject) — TagKey.is() requires a TagKey<Item>
            if (!inventory.getItem(i).is(UNLOCKS_MODULE_SLOT_TAG)) return false;
        }
        return true;
    }

    public static boolean isUnlockExtraModule(NonNullList<ItemStack> inventory) {
        for (int i = 0; i < 6; i++) {
            if (!inventory.get(i).is(UNLOCKS_MODULE_SLOT_TAG)) return false;
        }
        return true;
    }

    public static boolean haveExtraModuleStack(Container inventory) {
        for (var index : AssemblerMenu.EXTRA_MODULE_SLOTS) {
            if (!inventory.getItem(index).isEmpty()) return true;
        }
        return false;
    }
}