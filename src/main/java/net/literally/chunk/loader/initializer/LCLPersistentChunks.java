package net.literally.chunk.loader.initializer;

import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.literally.chunk.loader.data.AreaData;
import net.literally.chunk.loader.data.SerializedAreasData;
import net.literally.chunk.loader.implementations.AreaImplementation;
import net.literally.chunk.loader.implementations.SerializedAreasImplementation;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.saves.ChunksSerializeManager;
import net.literally.chunk.loader.utils.ModLogger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;

public final class LCLPersistentChunks
{
    private static boolean firstTick;
    public static String CURRENT_LEVEL_NAME;
    
    public static void initialize()
    {
        firstTick = true;
        ServerTickCallback.EVENT.register((MinecraftServer server) ->
        {
            if(firstTick)
            {
                initializePersistentAreas(server);
                CURRENT_LEVEL_NAME = server.getLevelName();
                firstTick = false;
            }
        });
    }
    
    public static boolean toggleAreaState(MinecraftServer server, AreaData data, boolean state)
    {
        SerializedAreasData areasData = ChunksSerializeManager.deserialize(server.getLevelName());
        if(areasData == null)
        {
            return false;
        }
        else
        {
            SerializedAreasImplementation.toggleArea(areasData, data, state);
            setAreaForceLoaded(server, data, state);
            return ChunksSerializeManager.serialize(areasData, server.getLevelName());
        }
    }
    
    public static boolean removePersistentArea(MinecraftServer server, AreaData area)
    {
        SerializedAreasData areasData = ChunksSerializeManager.deserialize(server.getLevelName());
        if(areasData == null)
        {
            return false;
        }
        else
        {
            SerializedAreasImplementation.removeArea(areasData, area);
            setAreaForceLoaded(server, area, false);
            return ChunksSerializeManager.serialize(areasData, server.getLevelName());
        }
    }
    
    public static boolean addPersistentArea(MinecraftServer server, AreaData area)
    {
        SerializedAreasData areasData = ChunksSerializeManager.deserialize(server.getLevelName());
        if(areasData == null)
        {
            areasData = new SerializedAreasData();
        }
        SerializedAreasImplementation.addArea(areasData, area);
        return ChunksSerializeManager.serialize(areasData, server.getLevelName());
    }
    
    public static boolean canPlaceLoaderAt(AreaData data)
    {
        SerializedAreasData areasData = ChunksSerializeManager.deserialize(CURRENT_LEVEL_NAME);
        if(areasData == null)
        {
            return true;
        }
        else
        {
            ArrayList<AreaData> areas = areasData.getAreas();
            for(int i = 0; i < areas.size(); i++)
            {
                if(AreaImplementation.areAreasOverlapping(areas.get(i), data))
                {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static void initializePersistentAreas(MinecraftServer server)
    {
        SerializedAreasData areasData = ChunksSerializeManager.deserialize(server.getLevelName());
        if(areasData != null)
        {
            ArrayList<AreaData> areas = areasData.getAreas();
            for(int i = 0; i < areas.size(); i++)
            {
                setAreaForceLoaded(server, areas.get(i), areas.get(i).getActive());
            }
        }
    }
    
    public static void setAreaForceLoaded(MinecraftServer server, AreaData area, boolean forceLoaded)
    {
        ModLogger logger = new ModLogger(LCLLoader.MOD_ID);
        
        int i = (int) area.getCentreData().getX() - 32;
        int j = (int) area.getCentreData().getZ() - 32;
        int k = (int) area.getCentreData().getX() + 32;
        int l = (int) area.getCentreData().getZ() + 32;
        if(i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000)
        {
            int m = i >> 4;
            int n = j >> 4;
            int o = k >> 4;
            int p = l >> 4;
            long q = ((long) (o - m) + 1L) * ((long) (p - n) + 1L);
            if(q > 256L)
            {
                logger.logError("Area is too big to be forceloaded");
                return;
            }
            else
            {
                DimensionType dimensionType = AreaImplementation.getDimensionFromID(area.getDimensionID());
                ServerWorld serverWorld = server.getWorld(dimensionType);
                int r = 0;
                
                for(int s = m; s <= o; ++s)
                {
                    for(int t = n; t <= p; ++t)
                    {
                        boolean bl = serverWorld.setChunkForced(s, t, forceLoaded);
                        if(bl)
                        {
                            ++r;
                        }
                    }
                }
                if(r > 0)
                {
                    logger.logInfo("Dimension: " + area.getDimensionID() + ", from: [x,y] => [" + i + ", " + j + "] to [x,y] => [" + k + ", " + l + "], forceload = " + forceLoaded);
                }
                else
                {
                    logger.logInfo("No chunks were affected (duplicate state)");
                }
                area.setActive(forceLoaded);
            }
        }
        else
        {
            logger.logError("Location out of world, unable to set forceload");
            return;
        }
    }
}
