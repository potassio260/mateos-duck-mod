package net.mateo.duckmod.block;

import java.util.function.Supplier;

import net.mateo.duckmod.DuckMod;
import net.mateo.duckmod.block.custom.NestBlock;
import net.mateo.duckmod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DuckMod.MOD_ID);

    public static final RegistryObject<Block> NEST_BLOCK = registerBlock("nest_block",  //set block attributes
    () -> new NestBlock(BlockBehaviour.Properties.of()
        .mapColor(MapColor.COLOR_BROWN)
        .strength(0.1f, 0.1f)
        .sound(SoundType.GRASS)
        .noOcclusion()
    ));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}