package net.literally.chunk.loader.initializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.literally.chunk.loader.mixin.ChunkTicketManagerInvoker;
import net.literally.chunk.loader.mixin.ChunkTicketTypeAccessor;
import net.literally.chunk.loader.mixin.ThreadedAnvilChunkStorageInvoker;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Optional;

public final class LCLTicker {
    public static void initialize() {
        ServerTickEvents.START_WORLD_TICK.register(LCLTicker::tick);
    }

    //The MIT License (MIT)
    //
    //Copyright (c) 2020 Techdoodle
    //
    //Permission is hereby granted, free of charge, to any person obtaining a copy
    //of this software and associated documentation files (the "Software"), to deal
    //in the Software without restriction, including without limitation the rights
    //to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    //copies of the Software, and to permit persons to whom the Software is
    //furnished to do so, subject to the following conditions:
    //
    //The above copyright notice and this permission notice shall be included in
    //all copies or substantial portions of the Software.
    //
    //THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    //IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    //FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    //AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    //LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    //OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    //THE SOFTWARE.
    private static void tick(ServerWorld world) {
        ThreadedAnvilChunkStorageInvoker storage = (ThreadedAnvilChunkStorageInvoker) world.getChunkManager().threadedAnvilChunkStorage;
        ChunkTicketManagerInvoker ticketManager = (ChunkTicketManagerInvoker) storage.invokeGetTicketManager();
        int randomTickSpeed = world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);
        if (randomTickSpeed == 0) {
            return;
        }
        storage.invokeEntryIterator().forEach(chunkHolder -> {
            // Ensure the chunk is force loaded rather than a "regular" chunk outside the 128-block radius
            boolean forced = ticketManager.invokeGetTicketSet(chunkHolder.getPos().toLong()).stream().anyMatch(chunkTicket -> ((ChunkTicketTypeAccessor) chunkTicket.getType()).getName().equals("forced"));
            if (!forced) {
                return;
            }

            Optional<WorldChunk> optionalWorldChunk = chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
            if (optionalWorldChunk.isEmpty()) {
                return;
            }
            // Make sure it's too far to get regular random ticks
            if (storage.invokeIsTooFarFromPlayersToSpawnMobs(chunkHolder.getPos())) {
                WorldChunk chunk = optionalWorldChunk.get();
                Profiler profiler = world.getProfiler();
                int startX = chunk.getPos().getStartX();
                int startZ = chunk.getPos().getStartZ();
                int startY = chunk.getPos().getStartPos().getY();
                for (ChunkSection chunkSection : chunk.getSectionArray()) {
                    if (!chunkSection.isEmpty() && chunkSection.hasRandomTicks()) {
                        for (int m = 0; m < randomTickSpeed; m++) {
                            BlockPos randomPosInChunk = world.getRandomPosInChunk(startX, startY, startZ, 15);
                            profiler.push("randomTick");
                            BlockState blockState = chunkSection.getBlockState(randomPosInChunk.getX() - startX, randomPosInChunk.getY() - startY, randomPosInChunk.getZ() - startZ);
                            if (blockState.hasRandomTicks()) {
                                blockState.randomTick(world, randomPosInChunk, world.random);
                            }
                            FluidState fluidState = blockState.getFluidState();
                            if (fluidState.hasRandomTicks()) {
                                fluidState.onRandomTick(world, randomPosInChunk, world.random);
                            }
                            profiler.pop();
                        }
                    }
                }
            }
        });
    }
}