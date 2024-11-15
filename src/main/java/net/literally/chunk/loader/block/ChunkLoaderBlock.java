package net.literally.chunk.loader.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.literally.chunk.loader.data.LCLData;
import net.literally.chunk.loader.data.SerializableChunkPos;
import net.literally.chunk.loader.entity.ChunkLoaderBlockEntity;
import net.literally.chunk.loader.initializer.LCLItems;
import net.literally.chunk.loader.initializer.LCLPersistentChunks;
import net.literally.chunk.loader.loaders.LCLLoader;
import net.literally.chunk.loader.network.packets.packet.ForcedChunksUpdatePacketPayload;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
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

public class ChunkLoaderBlock extends BlockWithEntity {
    public static final Identifier ID = Identifier.of(LCLLoader.MOD_ID, "chunk_loader");
    public static final EnumProperty<Direction> FACING;

    static {
        FACING = HorizontalFacingBlock.FACING;
    }

    public ChunkLoaderBlock() {
        super(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, ID)).sounds(BlockSoundGroup.METAL).strength(1F, 1F).nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ePos) {
        return Block.createCuboidShape(1F, 0F, 1F, 15F, 16F, 15F);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            LongSet set = serverWorld.getForcedChunks();
            ArrayList<SerializableChunkPos> longs = new ArrayList<>();
            SerializableChunkPos chunk = new SerializableChunkPos(pos, world.getRegistryKey().getValue().getPath());
            for (long longPos : set) {
                int fromX = chunk.getX() - (LCLData.SIZE / 2);
                int toX = chunk.getX() + (LCLData.SIZE / 2);
                int fromZ = chunk.getZ() - (LCLData.SIZE / 2);
                int toZ = chunk.getZ() + (LCLData.SIZE / 2);
                int x = ChunkPos.getPackedX(longPos);
                int z = ChunkPos.getPackedZ(longPos);
                if (x >= fromX && x <= toX && z >= fromZ && z <= toZ) {
                    longs.add(new SerializableChunkPos(longPos, chunk.getDimension()));
                }
            }
            ForcedChunksUpdatePacketPayload pack = new ForcedChunksUpdatePacketPayload(chunk.getX(), chunk.getZ(), true, longs);
            pack.sendTo(player);
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            SerializableChunkPos chunk = new SerializableChunkPos(pos, world.getRegistryKey().getValue().getPath());
            LCLPersistentChunks.loaderRemoved(world.getServer(), chunk);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            MinecraftServer server = world.getServer();
            SerializableChunkPos chunk = new SerializableChunkPos(pos, world.getRegistryKey().getValue().getPath());
            boolean canPlace = LCLPersistentChunks.canPlaceLoaderAt(chunk);
            if (canPlace) {
                LCLPersistentChunks.loaderAdded(chunk);
            }
            else {
                PlayerEntity player = (PlayerEntity) placer;
                ItemScatterer.spawn(world, pos, new SimpleInventory(new ItemStack(LCLItems.CHUNKLOADERITEM, 1)));
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                if (server == null) return;
                var manager = server.getPlayerManager().getPlayer(player.getUuid());
                if (manager == null) return;
                manager.sendMessage(Text.of("Can't place a Loader in the same chunk of another Loader"));
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
        double g = 0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F;
        if (random.nextInt(6) == 0) {
            world.addParticle(ParticleTypes.END_ROD, d + 0.1F * g, e + 0.1F * g, f + 0.1F * g, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D,
                              random.nextGaussian() * 0.005D);
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
        return this.getDefaultState().with(FACING, Direction.fromHorizontal(ctx.getPlayerLookDirection().getOpposite().getHorizontal()));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
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
