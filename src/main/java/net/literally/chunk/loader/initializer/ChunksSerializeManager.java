package net.literally.chunk.loader.initializer;

import net.literally.chunk.loader.saves.PersistentChunksSerializable;

import java.io.*;

public final class ChunksSerializeManager
{
    public static final String NAME = "chunks.data";
    public static final String PATH = "mods/LCL";
    
    private static FileOutputStream outputStream;
    private static ObjectOutputStream objectOutputStream;
    private static FileInputStream inputStream;
    private static ObjectInputStream objectInputStream;
    
    public static boolean serialize(PersistentChunksSerializable chunksData)
    {
        try
        {
            if(!fileExists())
            {
                File file = new File(getCompletePath());
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            outputStream = new FileOutputStream(getCompletePath());
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(chunksData);
            return true;
        } catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public static PersistentChunksSerializable deserialize()
    {
        try
        {
            if(!fileExists())
            {
                return null;
            }
            inputStream = new FileInputStream(getCompletePath());
            objectInputStream = new ObjectInputStream(inputStream);
            Object chunksData = objectInputStream.readObject();
            if(chunksData instanceof PersistentChunksSerializable)
            {
                return (PersistentChunksSerializable) chunksData;
            }
            else
            {
                return null;
            }
        } catch(IOException e)
        {
            e.printStackTrace();
            return null;
        } catch(ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    private static boolean fileExists()
    {
        File file = new File(getCompletePath());
        return file.exists();
    }
    
    public static String getCompletePath()
    {
        return PATH + File.separator + NAME;
    }
}
