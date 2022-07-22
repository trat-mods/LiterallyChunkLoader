package net.literally.chunk.loader.loaders;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.literally.chunk.loader.initializer.*;
import net.literally.chunk.loader.network.packets.packet.ForcedChunksUpdatePacket;

public class LCLLoader implements ModInitializer
{
    public static final String MOD_ID = "lchunkloader";
    
    @Override public void onInitialize()
    {
        LCLTicker.initialize();
        LCLBlocks.initialize();
        LCLItems.initialize();
        LCLCommands.initialize();
        LCLGUIHandlers.initialize();
        LCLEntities.initialize();
        
        ServerPlayNetworking.registerGlobalReceiver(ForcedChunksUpdatePacket.PACKET_ID, (server, servPlayer, handler, buf, sender) -> ForcedChunksUpdatePacket.read(buf).onServerReceive(server, servPlayer, handler, buf, sender));
    }
}
