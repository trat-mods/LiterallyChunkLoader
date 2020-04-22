package net.literally.chunk.loader.saves;

import java.io.Serializable;
import java.util.ArrayList;

public class PersistentAreasSerializable implements Serializable
{
    private ArrayList<PersistentArea> persistentAreas;
    
    public boolean addArea(PersistentArea data)
    {
        if(persistentAreas == null)
        {
            persistentAreas = new ArrayList<>();
        }
        return persistentAreas.add(data);
    }
    
    public boolean removeArea(PersistentArea data)
    {
        if(persistentAreas == null)
        {
            persistentAreas = new ArrayList<>();
            return false;
        }
        return persistentAreas.remove(data);
    }
    
    public ArrayList<PersistentArea> getAreasCentrePos()
    {
        return this.persistentAreas;
    }
}
