package net.literally.chunk.loader.saves;

import java.io.Serializable;

public class Centre implements Serializable
{
    private float x;
    private float z;
    
    public float getX() {return this.x;}
    
    public float getZ() {return this.z;}
    
    Centre(float x, float z)
    {
        this.x = x;
        this.z = z;
    }
    
    @Override public String toString()
    {
        return "( " + x + ", " + z + " )";
    }
}
