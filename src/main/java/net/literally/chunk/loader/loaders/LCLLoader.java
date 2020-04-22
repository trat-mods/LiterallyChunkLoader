package net.literally.chunk.loader.loaders;

import net.fabricmc.api.ModInitializer;
import net.literally.chunk.loader.initializer.ChunksSerializeManager;
import net.literally.chunk.loader.initializer.LCLBlocks;
import net.literally.chunk.loader.initializer.LCLItems;
import net.literally.chunk.loader.saves.PersistentChunksSerializable;

public class LCLLoader implements ModInitializer
{
    public static final String MOD_ID = "lchunkloader";
    
    @Override public void onInitialize()
    {
        LCLBlocks.initialize();
        LCLItems.initialize();
        ChunksSerializeManager.serialize(new PersistentChunksSerializable());
    }
}
