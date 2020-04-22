package net.literally.chunk.loader.saves;

import net.minecraft.util.math.BlockPos;

import java.io.Serializable;
import java.util.ArrayList;

public class PersistentChunksSerializable implements Serializable
{
    ArrayList<BlockPos> chunks;
    
    public boolean addChunk(BlockPos data)
    {
        return chunks.add(data);
    }
    
    public boolean removeChunk(BlockPos data)
    {
        return chunks.remove(data);
    }
}
