package net.literally.chunk.loader.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.literally.chunk.loader.initializer.LCLPersistentChunks;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.saves.PersistentArea;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChunkLoaderBlock extends Block
{
    public static final Identifier ID = new Identifier(LCLLoader.MOD_ID, "chunk_loader");
    
    public ChunkLoaderBlock()
    {
        super(FabricBlockSettings.of(Material.METAL).breakByHand((true)).sounds(BlockSoundGroup.METAL).strength(1F, 1F).nonOpaque().build());
    }
    
    @Override public void onBroken(IWorld world, BlockPos pos, BlockState state)
    {
        if(!world.isClient())
        {
            MinecraftServer server = world.getWorld().getServer();
            PersistentArea newArea = new PersistentArea(pos.getX(), pos.getZ());
            LCLPersistentChunks.removePersistentArea(server, newArea);
        }
        super.onBroken(world, pos, state);
    }
    
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack)
    {
        if(!world.isClient)
        {
            MinecraftServer server = world.getServer();
            PersistentArea newArea = new PersistentArea(pos.getX(), pos.getZ());
            LCLPersistentChunks.addPersistentArea(server, newArea);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }
}
