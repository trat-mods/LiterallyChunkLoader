package net.literally.chunk.loader.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.literally.chunk.loader.loader.LCLLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ChunkLoaderBlock extends Block
{
    public static final Identifier ID = new Identifier(LCLLoader.MOD_ID, "chunk_loader");
    
    public ChunkLoaderBlock()
    {
        super(FabricBlockSettings.of(Material.METAL).breakByHand((true)).sounds(BlockSoundGroup.METAL).strength(1F, 1F).nonOpaque().build());
    }
}
