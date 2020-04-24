package net.literally.chunk.loader.implementations;

import net.literally.chunk.loader.data.AreaData;
import net.minecraft.world.dimension.DimensionType;

public final class AreaImplementation
{
    public static final String OVERWORLD_ID = "overworld";
    public static final String NETHER_ID = "nether";
    public static final String END_ID = "end";
    
    public static boolean areAreasEqual(AreaData first, AreaData second)
    {
        if(!first.getDimensionID().equals(second.getDimensionID())) return false;
        return first.getFromChunkX() == second.getFromChunkX() && first.getFromChunkZ() == second.getFromChunkZ() && first.getToChunkX() == second.getToChunkX() && first.getToChunkZ() == second.getToChunkZ();
    }
    
    public static boolean areAreasOverlapping(AreaData first, AreaData second)
    {
        if(!first.getDimensionID().equals(second.getDimensionID())) return false;
        
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
    
    public static DimensionType getDimensionFromID(String id)
    {
        switch(id)
        {
            case OVERWORLD_ID:
                return DimensionType.OVERWORLD;
            case NETHER_ID:
                return DimensionType.THE_NETHER;
            case END_ID:
                return DimensionType.THE_END;
            default:
                return null;
        }
    }
    
    public static String getIDFromDimension(DimensionType type)
    {
        if(type.equals(DimensionType.OVERWORLD))
        {
            return OVERWORLD_ID;
        }
        else if(type.equals(DimensionType.THE_NETHER))
        {
            return NETHER_ID;
        }
        else if(type.equals(DimensionType.THE_END))
        {
            return END_ID;
        }
        else
        {
            return "";
        }
    }
}
