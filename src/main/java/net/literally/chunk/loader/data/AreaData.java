package net.literally.chunk.loader.data;

import java.io.Serializable;

public class AreaData implements Serializable
{
    private CentreData centre;
    private String dimensionID;
    private int fromChunkX;
    private int toChunkX;
    private int fromChunkZ;
    private int toChunkZ;
    private boolean active;
    
    public AreaData(CentreData centre, String dimensionID)
    {
        this.dimensionID = dimensionID;
        this.centre = centre;
        this.fromChunkX = centre.getChunkX() - 2;
        this.toChunkX = centre.getChunkX() + 2;
        this.fromChunkZ = centre.getChunkZ() - 2;
        this.toChunkZ = centre.getChunkZ() + 2;
    }
    
    public AreaData(float x, float y, String dimensionID)
    {
        this(new CentreData(x, y), dimensionID);
    }
    
    public String getDimensionID() {return dimensionID;}
    
    public CentreData getCentreData()
    {
        return this.centre;
    }
    
    public int getFromChunkX()
    {
        return this.fromChunkX;
    }
    
    public int getToChunkX()
    {
        return this.toChunkX;
    }
    
    public int getFromChunkZ()
    {
        return this.fromChunkZ;
    }
    
    public int getToChunkZ()
    {
        return this.toChunkZ;
    }
    
    public boolean getActive()
    {
        return this.active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    @Override public String toString()
    {
        return "Dimension: " + dimensionID + ", from [" + fromChunkX + ", " + fromChunkZ + "], to [" + toChunkX + ", " + toChunkZ + "]";
    }
}
