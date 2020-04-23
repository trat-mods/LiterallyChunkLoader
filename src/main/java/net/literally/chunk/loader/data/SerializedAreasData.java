package net.literally.chunk.loader.data;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializedAreasData implements Serializable
{
    private ArrayList<AreaData> areas;
    
    public SerializedAreasData()
    {
        areas = new ArrayList<>();
    }
    
    public ArrayList<AreaData> getAreas() {return this.areas;}
}
