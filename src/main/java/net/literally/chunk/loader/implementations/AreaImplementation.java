package net.literally.chunk.loader.implementations;

import net.literally.chunk.loader.data.AreaData;

public final class AreaImplementation
{
    public static boolean areAreasEqual(AreaData first, AreaData second)
    {
        return first.getFromChunkX() == second.getFromChunkX() && first.getFromChunkZ() == second.getFromChunkZ() && first.getToChunkX() == second.getToChunkX() && first.getToChunkZ() == second.getToChunkZ();
    }
    
    public static boolean areAreasOverlapping(AreaData first, AreaData second)
    {
        if(first.getFromChunkX() >= second.getToChunkX() || second.getFromChunkX() >= first.getToChunkX())
        {
            return false;
        }
        if(first.getToChunkZ() <= second.getFromChunkZ() || second.getToChunkZ() <= first.getFromChunkZ())
        {
            return false;
        }
        return true;
    }
}
