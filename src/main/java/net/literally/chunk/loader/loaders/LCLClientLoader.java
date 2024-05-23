package net.literally.chunk.loader.loaders;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.literally.chunk.loader.gui.handler.ChunkLoaderGUIHandler;
import net.literally.chunk.loader.gui.screen.ChunkLoaderScreen;
import net.literally.chunk.loader.initializer.LCLGUIHandlers;
import net.literally.chunk.loader.network.packets.packet.ForcedChunksUpdatePacketPayload;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class LCLClientLoader implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.<ChunkLoaderGUIHandler, ChunkLoaderScreen>register(LCLGUIHandlers.CHUNK_LOADER_SCREEN_HANDLER, (gui, inventory, title) -> new ChunkLoaderScreen(gui, inventory.player, title));
        PayloadTypeRegistry.playS2C().register(ForcedChunksUpdatePacketPayload.ID, ForcedChunksUpdatePacketPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ForcedChunksUpdatePacketPayload.ID, ((payload, context) -> context.client().execute(() -> {
            payload.onClientReceive(context.client());
        })));
    }
}
