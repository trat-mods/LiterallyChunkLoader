package net.literally.chunk.loader.loaders;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.literally.chunk.loader.GUI.handler.ChunkLoaderGUIHandler;
import net.literally.chunk.loader.GUI.screen.ChunkLoaderScreen;
import net.literally.chunk.loader.initializer.LCLGUIHandlers;
import net.literally.chunk.loader.network.packets.packet.ForcedChunksUpdatePacket;

public class LCLClientLoader implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.<ChunkLoaderGUIHandler, ChunkLoaderScreen>register(LCLGUIHandlers.CHUNK_LOADER_SCREEN_HANDLER, (gui, inventory, title) -> new ChunkLoaderScreen(gui, inventory.player, title));
        ClientPlayNetworking.registerGlobalReceiver(ForcedChunksUpdatePacket.PACKET_ID, (client, handler, buf, responseSender) -> ForcedChunksUpdatePacket.read(buf).onClientReceive(client, handler, buf, responseSender));
    }
}
