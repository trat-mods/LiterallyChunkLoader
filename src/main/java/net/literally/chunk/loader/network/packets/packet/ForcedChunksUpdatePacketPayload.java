package net.literally.chunk.loader.network.packets.packet;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.literally.chunk.loader.data.SerializableChunkPos;
import net.literally.chunk.loader.gui.handler.ChunkLoaderGUIHandler;
import net.literally.chunk.loader.initializer.LCLPersistentChunks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public record ForcedChunksUpdatePacketPayload(int x, int z, boolean state, List<SerializableChunkPos> chunksPos) implements CustomPayload {
    public static final CustomPayload.Id<ForcedChunksUpdatePacketPayload> ID = CustomPayload.id("lcl:packet");
    public static final PacketCodec<PacketByteBuf, ForcedChunksUpdatePacketPayload> CODEC = PacketCodec.of(ForcedChunksUpdatePacketPayload::write, ForcedChunksUpdatePacketPayload::read);

    public static ForcedChunksUpdatePacketPayload read(PacketByteBuf buf) {
        ArrayList<SerializableChunkPos> chunks = new ArrayList<>();
        int x = buf.readInt();
        int z = buf.readInt();
        boolean state = buf.readBoolean();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            chunks.add(SerializableChunkPos.read(buf));
        }
        return new ForcedChunksUpdatePacketPayload(x, z, state, chunks);
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(getX());
        buf.writeInt(getZ());
        buf.writeBoolean(isState());
        int size = getChunksPos().size();
        buf.writeInt(size);
        for (SerializableChunkPos chunkPos : getChunksPos()) {
            chunkPos.writeTo(buf);
        }
    }

    public void onClientReceive(MinecraftClient client) {
        client.execute(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            ScreenHandler screenHandler = player.currentScreenHandler;
            if (screenHandler != null) {
                if (screenHandler instanceof ChunkLoaderGUIHandler) {
                    ChunkLoaderGUIHandler clHandler = (ChunkLoaderGUIHandler) screenHandler;
                    clHandler.refreshGUI(this);
                }
            }
        });
    }

    public void sendTo(PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        write(buf);
        ServerPlayNetworking.send((ServerPlayerEntity) player, this);
    }


    public void onServerReceive(MinecraftServer server) {
        for (SerializableChunkPos chunksPo : getChunksPos()) {
            LCLPersistentChunks.forceLoadChunk(server, chunksPo, isState());
        }
        List<ServerPlayerEntity> targets = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity target : targets) {
            sendTo(target);
        }
    }

    public List<SerializableChunkPos> getChunksPos() {
        return chunksPos;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public boolean isState() {
        return state;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
