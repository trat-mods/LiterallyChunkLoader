package net.literally.chunk.loader.saves;

import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.utils.ModLogger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;

import java.io.Serializable;

public class PersistentArea implements Serializable
{
    private Centre centre;
    
    public Centre getCentre() {return this.centre;}
    
    public PersistentArea(Centre centre)
    {
        this.centre = centre;
    }
    
    public PersistentArea(float x, float z)
    {
        this(new Centre(x, z));
    }
    
    public void setForceLoaded(MinecraftServer server, boolean forceLoaded)
    {
        ModLogger logger = new ModLogger(LCLLoader.MOD_ID);
        
        int i = (int) centre.getX() - 32;
        int j = (int) centre.getZ() - 32;
        int k = (int) centre.getX() + 32;
        int l = (int) centre.getZ() + 32;
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
                DimensionType dimensionType = DimensionType.OVERWORLD;
                ServerWorld serverWorld = server.getWorld(dimensionType);
                ChunkPos chunkPos = null;
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
                    logger.logInfo("Chunks from: [x,y] => [" + i + ", " + j + "] to [x,y] => [" + k + ", " + l + "], forceload = " + forceLoaded);
                }
            }
        }
        else
        {
            logger.logError("Location out of world, unable to set forceload");
            return;
        }
    }
    
    @Override public String toString()
    {
        return "Centre: " + centre.toString();
    }
}
