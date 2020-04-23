package net.literally.chunk.loader.saves;

import net.literally.chunk.loader.data.SerializedAreasData;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.utils.ModLogger;

import java.io.*;

public final class ChunksSerializeManager
{
    public static final String NAME = "chunks.data";
    public static final String PATH = "literally_chunk_loader";
    
    private static FileOutputStream outputStream;
    private static ObjectOutputStream objectOutputStream;
    private static FileInputStream inputStream;
    private static ObjectInputStream objectInputStream;
    
    public static boolean serialize(SerializedAreasData areasData, String worldName)
    {
        ModLogger logger = new ModLogger(LCLLoader.MOD_ID);
        try
        {
            if(!fileExists(worldName))
            {
                File file = new File(getCompletePath(worldName));
                file.getParentFile().mkdirs();
                file.createNewFile();
                logger.logInfo("Persistent chunks data file still doesn't exist, generating a new one");
            }
            outputStream = new FileOutputStream(getCompletePath(worldName));
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(areasData);
            //logger.logInfo("Persistent chunks data successfully serialized");
            return true;
        } catch(IOException e)
        {
            logger.logError("Unable to serialize persistent chunks data, exception stack trace:");
            e.printStackTrace();
            return false;
        }
    }
    
    public static SerializedAreasData deserialize(String worldName)
    {
        ModLogger logger = new ModLogger(LCLLoader.MOD_ID);
        try
        {
            if(!fileExists(worldName))
            {
                logger.logWarning("Persistent chunks file does not exist, nothing to deserialize");
                return null;
            }
            inputStream = new FileInputStream(getCompletePath(worldName));
            objectInputStream = new ObjectInputStream(inputStream);
            Object areasData = objectInputStream.readObject();
            if(areasData instanceof SerializedAreasData)
            {
                //logger.logInfo("Persistent chunks file successfully deserialized");
                return (SerializedAreasData) areasData;
            }
            else
            {
                logger.logError("Unable to cast deserialized data to type class");
                return null;
            }
        } catch(Exception e)
        {
            logger.logError("Unable to deserialize persistent chunks data, exception stack trace:");
            e.printStackTrace();
            return null;
        }
    }
    
    private static boolean fileExists(String worldName)
    {
        File file = new File(getCompletePath(worldName));
        return file.exists();
    }
    
    public static String getCompletePath(String worldName)
    {
        return "mods" + File.separator + PATH + File.separator + worldName + File.separator + NAME;
    }
}
