package net.literally.chunk.loader.mixin;


import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkLoadingManager.class)
public interface ThreadedAnvilChunkStorageInvoker {
    @Invoker("shouldTick")
    boolean invokeIsTooFarFromPlayersToSpawnMobs(ChunkPos arg);

    @Invoker("entryIterator")
    Iterable<ChunkHolder> invokeEntryIterator();

    @Invoker("getTicketManager")
    ChunkTicketManager invokeGetTicketManager();
}