package net.literally.chunk.loader.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.literally.chunk.loader.initializer.ChunksSerializeManager;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.saves.PersistentChunksSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkLoaderBlock extends Block
{
    public static final Identifier ID = new Identifier(LCLLoader.MOD_ID, "chunk_loader");
    
    public ChunkLoaderBlock()
    {
        super(FabricBlockSettings.of(Material.METAL).breakByHand((true)).sounds(BlockSoundGroup.METAL).strength(1F, 1F).nonOpaque().build());
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        PersistentChunksSerializable data = ChunksSerializeManager.deserialize();
        if(data == null)
        {
            ChunksSerializeManager.serialize(new PersistentChunksSerializable());
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
