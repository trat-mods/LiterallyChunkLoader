package net.literally.chunk.loader.implementations;

import net.literally.chunk.loader.data.AreaData;
import net.literally.chunk.loader.data.SerializedAreasData;

import java.util.ArrayList;

public final class SerializedAreasImplementation
{
    public static boolean addArea(SerializedAreasData areasData, AreaData data)
    {
        ArrayList<AreaData> areas = areasData.getAreas();
        if(areas == null)
        {
            areas = new ArrayList<>();
        }
        return areas.add(data);
    }
    
    public static boolean removeArea(SerializedAreasData areasData, AreaData data)
    {
        ArrayList<AreaData> areas = areasData.getAreas();
        if(areas == null)
        {
            areas = new ArrayList<>();
            return false;
        }
        for(int i = 0; i < areas.size(); i++)
        {
            if(AreaImplementation.areAreasEqual(areas.get(i), data))
            {
                areas.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public static boolean toggleArea(SerializedAreasData areasData, AreaData data, boolean state)
    {
        ArrayList<AreaData> areas = areasData.getAreas();
        if(areas == null)
        {
            return false;
        }
        else
        {
            for(int i = 0; i < areas.size(); i++)
            {
                if(AreaImplementation.areAreasEqual(areas.get(i), data))
                {
                    areas.get(i).setActive(state);
                    return true;
                }
            }
            return false;
        }
    }
}
