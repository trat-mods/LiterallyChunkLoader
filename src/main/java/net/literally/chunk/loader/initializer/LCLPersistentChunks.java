package net.literally.chunk.loader.initializer;

import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.literally.chunk.loader.saves.ChunksSerializeManager;
import net.literally.chunk.loader.saves.PersistentArea;
import net.literally.chunk.loader.saves.PersistentAreasSerializable;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;

public final class LCLPersistentChunks
{
    private static boolean firstTick = true;
    
    public static void initialize()
    {
        ServerTickCallback.EVENT.register((MinecraftServer server) ->
        {
            if(firstTick)
            {
                initializePersistentAreas(server);
                firstTick = false;
            }
        });
    }
    
    public static boolean removePersistentArea(MinecraftServer server, PersistentArea area)
    {
        PersistentAreasSerializable areasData = ChunksSerializeManager.deserialize(server.getLevelName());
        if(areasData == null)
        {
            return false;
        }
        else
        {
            areasData.removeArea(area);
            area.setForceLoaded(server, false);
            return ChunksSerializeManager.serialize(areasData, server.getLevelName());
        }
    }
    
    public static boolean addPersistentArea(MinecraftServer server, PersistentArea area)
    {
        PersistentAreasSerializable areasData = ChunksSerializeManager.deserialize(server.getLevelName());
        if(areasData == null)
        {
            areasData = new PersistentAreasSerializable();
        }
        areasData.addArea(area);
        area.setForceLoaded(server, true);
        return ChunksSerializeManager.serialize(areasData, server.getLevelName());
    }
    
    private static void initializePersistentAreas(MinecraftServer server)
    {
        PersistentAreasSerializable areasData = ChunksSerializeManager.deserialize(server.getLevelName());
        if(areasData != null)
        {
            ArrayList<PersistentArea> centralPos = areasData.getAreasCentrePos();
            for(int i = 0; i < centralPos.size(); i++)
            {
                centralPos.get(i).setForceLoaded(server, true);
            }
        }
    }
}
