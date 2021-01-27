package net.literally.chunk.loader.initializer;

import net.literally.chunk.loader.block.ChunkLoaderBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public final class LCLItems
{
    public static Item CHUNKLOADERITEM;
    
    public static void initialize()
    {
        CHUNKLOADERITEM = Registry.register(Registry.ITEM, ChunkLoaderBlock.ID, new BlockItem(LCLBlocks.CHUNK_LOADER_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE)));
    }
}
