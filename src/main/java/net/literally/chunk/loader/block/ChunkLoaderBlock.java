package net.literally.chunk.loader.block;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.literally.chunk.loader.data.LclData;
import net.literally.chunk.loader.data.SerializableChunkPos;
import net.literally.chunk.loader.entity.ChunkLoaderBlockEntity;
import net.literally.chunk.loader.initializer.LCLItems;
import net.literally.chunk.loader.initializer.LCLPersistentChunks;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.network.packets.packet.ForcedChunksUpdatePacket;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

public class ChunkLoaderBlock extends BlockWithEntity {
    public static final Identifier ID = new Identifier(LCLLoader.MOD_ID, "chunk_loader");
    public static final DirectionProperty FACING;

    static {
        FACING = HorizontalFacingBlock.FACING;
    }

    public ChunkLoaderBlock() {
        super(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(1F, 1F).nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ePos) {
        return Block.createCuboidShape(1F, 0F, 1F, 15F, 16F, 15F);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            LongSet set = serverWorld.getForcedChunks();
            ArrayList<SerializableChunkPos> longs = new ArrayList<>();
            SerializableChunkPos chunk = new SerializableChunkPos(pos, world.getRegistryKey().getValue().getPath());
            Iterator<Long> it = set.stream().iterator();
            while (it.hasNext()) {
                long longPos = it.next();
                int fromX = chunk.getX() - (LclData.SIZE / 2);
                int toX = chunk.getX() + (LclData.SIZE / 2);
                int fromZ = chunk.getZ() - (LclData.SIZE / 2);
                int toZ = chunk.getZ() + (LclData.SIZE / 2);
                int x = ChunkPos.getPackedX(longPos);
                int z = ChunkPos.getPackedZ(longPos);
                if (x >= fromX && x <= toX && z >= fromZ && z <= toZ) {
                    longs.add(new SerializableChunkPos(longPos, chunk.getDimension()));
                }
            }
            ForcedChunksUpdatePacket pack = new ForcedChunksUpdatePacket(chunk.getX(), chunk.getZ(), true, longs);
            pack.sendTo(player);
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            SerializableChunkPos chunk = new SerializableChunkPos(pos, world.getRegistryKey().getValue().getPath());
            LCLPersistentChunks.loaderRemoved(world.getServer(), chunk);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            MinecraftServer server = world.getServer();
            SerializableChunkPos chunk = new SerializableChunkPos(pos, world.getRegistryKey().getValue().getPath());
            boolean canPlace = LCLPersistentChunks.canPlaceLoaderAt(chunk);
            if (canPlace) {
                boolean added = LCLPersistentChunks.loaderAdded(chunk);
            }
            else {
                PlayerEntity player = (PlayerEntity) placer;
                ItemScatterer.spawn(world, pos, new SimpleInventory(new ItemStack(LCLItems.CHUNKLOADERITEM, 1)));
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                assert server != null;
                server.getPlayerManager().getPlayer(player.getUuid()).sendMessage(Text.of("Can't place a Loader in the same chunk of another Loader"));
                return;
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        double d = (double) pos.getX() + 0.65D - (double) (random.nextFloat() * 0.3F);
        double e = (double) pos.getY() + 1F - (double) (random.nextFloat() * 0.5F);
        double f = (double) pos.getZ() + 0.65D - (double) (random.nextFloat() * 0.3F);
        double g = (double) (0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
        if (random.nextInt(6) == 0) {
            world.addParticle(ParticleTypes.END_ROD, d + 0.1F * g, e + 0.1F * g, f + 0.1F * g, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
        }
        double x = (double) pos.getX() + 0.65D - (double) (random.nextFloat() * 0.3F);
        double y = (double) pos.getY() + 2.75D;
        double z = (double) pos.getZ() + 0.65D - (double) (random.nextFloat() * 0.3F);
        world.addParticle(ParticleTypes.PORTAL, x, y, z, 0, -3D, 0);
    }


    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChunkLoaderBlockEntity(pos, state);
    }
}
