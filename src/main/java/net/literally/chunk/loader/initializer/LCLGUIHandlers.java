package net.literally.chunk.loader.initializer;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.literally.chunk.loader.GUI.handler.ChunkLoaderGUIHandler;
import net.literally.chunk.loader.block.ChunkLoaderBlock;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

public final class LCLGUIHandlers {
    public static ScreenHandlerType CHUNK_LOADER_SCREEN_HANDLER;

    public static void initialize() {
        CHUNK_LOADER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(ChunkLoaderBlock.ID,
                                                                             (syncId, inventory, buf) -> new ChunkLoaderGUIHandler(syncId, inventory, ScreenHandlerContext.create(inventory.player.world, buf.readBlockPos())));
    }
}
