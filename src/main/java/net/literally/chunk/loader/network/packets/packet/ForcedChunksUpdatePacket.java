package net.literally.chunk.loader.network.packets.packet;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.literally.chunk.loader.GUI.handler.ChunkLoaderGUIHandler;
import net.literally.chunk.loader.data.SerializableChunkPos;
import net.literally.chunk.loader.initializer.LCLPersistentChunks;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForcedChunksUpdatePacket {
    public static Identifier PACKET_ID = new Identifier(LCLLoader.MOD_ID, "fc_packet");
    private final int x;
    private final int z;
    private final boolean state;
    private ArrayList<SerializableChunkPos> chunksPos;

    private ForcedChunksUpdatePacket(int x, int z, boolean state) {
        this.x = x;
        this.z = z;
        this.state = state;
    }

    public ForcedChunksUpdatePacket(int x, int z, boolean state, ArrayList<SerializableChunkPos> chunksPos) {
        this(x, z, state);
        this.chunksPos = chunksPos;
    }

    public ForcedChunksUpdatePacket(BlockPos pos, boolean state, ArrayList<SerializableChunkPos> chunksPos) {
        this(pos.getX() >> 4, pos.getZ() >> 4, state);
        this.chunksPos = chunksPos;
    }

    public ForcedChunksUpdatePacket(BlockPos pos, boolean state, SerializableChunkPos... chunksPos) {
        this(pos.getX() >> 4, pos.getZ() >> 4, state);
        this.chunksPos = new ArrayList<>();
        Collections.addAll(this.chunksPos, chunksPos);
    }

    public static ForcedChunksUpdatePacket read(PacketByteBuf buf) {
        ArrayList<SerializableChunkPos> chunks = new ArrayList<>();
        int x = buf.readInt();
        int z = buf.readInt();
        boolean state = buf.readBoolean();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            chunks.add(SerializableChunkPos.read(buf));
        }
        return new ForcedChunksUpdatePacket(x, z, state, chunks);
    }

    public void sendToServer() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        write(buf);
        ClientPlayNetworking.send(PACKET_ID, buf);
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(getX());
        buf.writeInt(getZ());
        buf.writeBoolean(isState());
        int size = getChunksPos().size();
        buf.writeInt(size);
        for (SerializableChunkPos chunksPo : getChunksPos()) {
            chunksPo.write(buf);
        }
    }

    public void onClientReceive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
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
        ServerPlayNetworking.send((ServerPlayerEntity) player, PACKET_ID, buf);
    }

    public void onServerReceive(MinecraftServer server, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        for (SerializableChunkPos chunksPo : getChunksPos()) {
            LCLPersistentChunks.forceLoadChunk(server, chunksPo, isState());
        }
        List<ServerPlayerEntity> targets = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity target : targets) {
            sendTo(target);
        }
    }

    public ArrayList<SerializableChunkPos> getChunksPos() {
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
}
