package net.literally.chunk.loader.loaders;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.literally.chunk.loader.initializer.*;
import net.literally.chunk.loader.network.packets.packet.ForcedChunksUpdatePacketPayload;

public class LCLLoader implements ModInitializer {
    public static final String MOD_ID = "lchunkloader";

    @Override
    public void onInitialize() {
        LCLTicker.initialize();
        LCLBlocks.initialize();
        LCLItems.initialize();
        LCLCommands.initialize();
        LCLGUIHandlers.initialize();
        LCLEntities.initialize();

        PayloadTypeRegistry.playC2S().register(ForcedChunksUpdatePacketPayload.ID, ForcedChunksUpdatePacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ForcedChunksUpdatePacketPayload.ID, ForcedChunksUpdatePacketPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ForcedChunksUpdatePacketPayload.ID, ((payload, context) -> context.player().server.execute(() -> {
            payload.onServerReceive(context.player().server);
        })));
    }
}
