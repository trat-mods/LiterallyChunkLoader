package net.literally.chunk.loader.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.literally.chunk.loader.initializer.LCLPersistentChunks;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.saves.PersistentArea;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChunkLoaderBlock extends Block
{
    public static final Identifier ID = new Identifier(LCLLoader.MOD_ID, "chunk_loader");
    public static final DirectionProperty FACING;
    public static final BooleanProperty ACTIVE;
    
    static
    {
        ACTIVE = BooleanProperty.of("active");
        FACING = HorizontalFacingBlock.FACING;
    }
    
    public ChunkLoaderBlock()
    {
        super(FabricBlockSettings.of(Material.METAL).breakByHand((true)).sounds(BlockSoundGroup.METAL).strength(1F, 1F).nonOpaque().build());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(ACTIVE, false));
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if(!world.isClient)
        {
            boolean wasActive = state.get(ACTIVE);
            world.setBlockState(pos, world.getBlockState(pos).with(ACTIVE, !wasActive), 0B1011);
            if(wasActive)
            {
                MinecraftServer server = world.getWorld().getServer();
                PersistentArea newArea = new PersistentArea(pos.getX(), pos.getZ());
                LCLPersistentChunks.removePersistentArea(server, newArea);
            }
            else
            {
                MinecraftServer server = world.getWorld().getServer();
                PersistentArea newArea = new PersistentArea(pos.getX(), pos.getZ());
                LCLPersistentChunks.addPersistentArea(server, newArea);
            }
        }
        return ActionResult.SUCCESS;
    }
    
    @Override public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        if(!world.isClient)
        {
            MinecraftServer server = world.getWorld().getServer();
            PersistentArea newArea = new PersistentArea(pos.getX(), pos.getZ());
            LCLPersistentChunks.removePersistentArea(server, newArea);
        }
        super.onBreak(world, pos, state, player);
    }
    
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager)
    {
        stateManager.add(FACING).add(ACTIVE);
    }
    
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }
    
    public BlockState rotate(BlockState state, BlockRotation rotation)
    {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    
    public BlockState mirror(BlockState state, BlockMirror mirror)
    {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
