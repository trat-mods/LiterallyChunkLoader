package net.literally.chunk.loader.data;

import java.io.Serializable;
import java.util.ArrayList;

public class LclData implements Serializable {
    public static final int SIZE = 5;
    private final ArrayList<SerializableChunkPos> chunks;
    private final ArrayList<SerializableChunkPos> loadersChunks;
    private final boolean apiUpdate = false;

    public LclData() {
        chunks = new ArrayList<>();
        loadersChunks = new ArrayList<>();
    }

    public ArrayList<SerializableChunkPos> getLoadersChunks() {return this.loadersChunks;}

    public void addLoaderPos(SerializableChunkPos chunk) {
        if (!loadersChunks.contains(chunk)) {
            loadersChunks.add(chunk);
        }
    }

    public void removeLoaderPos(SerializableChunkPos chunk) {
        loadersChunks.remove(chunk);
    }

    public void chunkForceLoaded(SerializableChunkPos chunk, boolean state) {
        if (state) {
            if (!isChunkForceLoaded(chunk)) {
                chunks.add(chunk);
            }
        }
        else {
            chunks.remove(chunk);
        }
    }

    public ArrayList<SerializableChunkPos> getChunks() {return this.chunks;}

    public boolean isLoaderPresentAt(SerializableChunkPos chunk) {
        return loadersChunks.contains(chunk);
    }

    public boolean isChunkForceLoaded(SerializableChunkPos chunk) {
        return chunks.contains(chunk);
    }
}
