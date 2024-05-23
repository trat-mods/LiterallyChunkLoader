package net.literally.chunk.loader.initializer;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.literally.chunk.loader.block.ChunkLoaderBlock;
import net.literally.chunk.loader.gui.handler.ChunkLoaderGUIHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

public final class LCLGUIHandlers {
    public static ScreenHandlerType<ChunkLoaderGUIHandler> CHUNK_LOADER_SCREEN_HANDLER;

    public static void initialize() {
        CHUNK_LOADER_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, ChunkLoaderBlock.ID,
                                                        new ExtendedScreenHandlerType<>(((syncId, inventory, data) -> new ChunkLoaderGUIHandler(syncId, inventory, ScreenHandlerContext.create(inventory.player.getWorld(), data), data)),
                                                                                        BlockPos.PACKET_CODEC.cast()));
    }
}
