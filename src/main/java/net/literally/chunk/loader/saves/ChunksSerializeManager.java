package net.literally.chunk.loader.saves;

import net.literally.chunk.loader.data.LclData;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.utils.ModLogger;

import java.io.*;

public final class ChunksSerializeManager {
    public static final String NAME = "chunks.data";
    public static final String PATH = "literally_chunk_loader";

    public static boolean serialize(LclData areasData, String worldName) {
        FileOutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        ModLogger logger = new ModLogger(LCLLoader.MOD_ID);
        try {
            if (nonexistentFile(worldName)) {
                File file = new File(getCompletePath(worldName));
                if (file.getParentFile().mkdirs() && file.createNewFile()) {
                    logger.logInfo("Persistent chunks data file still doesn't exist, generating a new one");
                }
                else {
                    logger.logError("Unable to create chunks data file");
                }
            }
            outputStream = new FileOutputStream(getCompletePath(worldName));
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(areasData);
            outputStream.close();
            objectOutputStream.close();
            return true;
        } catch (IOException e) {
            logger.logError("Unable to serialize persistent chunks data, exception stack trace:");
            try {
                assert outputStream != null;
                outputStream.close();
                assert objectOutputStream != null;
                objectOutputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public static LclData deserialize(String worldName) {
        FileInputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        ModLogger logger = new ModLogger(LCLLoader.MOD_ID);
        try {
            if (nonexistentFile(worldName)) {
                return null;
            }
            inputStream = new FileInputStream(getCompletePath(worldName));
            objectInputStream = new ObjectInputStream(inputStream);
            Object areasData = objectInputStream.readObject();
            inputStream.close();
            objectInputStream.close();
            if (areasData instanceof LclData) {
                return (LclData) areasData;
            }
            else {
                logger.logError("Unable to cast deserialized data to type class");
                return null;
            }
        } catch (Exception e) {
            logger.logError("Unable to deserialize persistent chunks data, exception stack trace:");
            try {
                assert inputStream != null;
                inputStream.close();
                assert objectInputStream != null;
                objectInputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
            return null;
        }
    }

    private static boolean nonexistentFile(String worldName) {
        File file = new File(getCompletePath(worldName));
        return !file.exists();
    }

    public static String getCompletePath(String worldName) {
        return "mods" + File.separator + PATH + File.separator + worldName + File.separator + NAME;
    }
}
