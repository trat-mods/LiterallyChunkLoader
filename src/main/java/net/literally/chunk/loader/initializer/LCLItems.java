package net.literally.chunk.loader.initializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.literally.chunk.loader.block.ChunkLoaderBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class LCLItems {
    public static Item CHUNKLOADERITEM;

    public static void initialize() {
        CHUNKLOADERITEM = Registry.register(Registries.ITEM, ChunkLoaderBlock.ID, new BlockItem(LCLBlocks.CHUNK_LOADER_BLOCK, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.add(CHUNKLOADERITEM));
    }
}
