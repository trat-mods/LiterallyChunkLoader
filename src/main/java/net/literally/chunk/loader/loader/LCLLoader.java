package net.literally.chunk.loader.loader;

import net.fabricmc.api.ModInitializer;
import net.literally.chunk.loader.initializer.LCLBlocks;
import net.literally.chunk.loader.initializer.LCLItems;

public class LCLLoader implements ModInitializer
{
    public static final String MOD_ID = "lchunkloader";
    
    @Override public void onInitialize()
    {
        LCLBlocks.initialize();
        LCLItems.initialize();
    }
}
