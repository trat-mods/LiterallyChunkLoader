package net.literally.chunk.loader.data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.Serializable;

public class SerializableChunkPos implements Serializable {
    private final int x;
    private final int z;
    private final String dimension;

    public SerializableChunkPos(BlockPos origin, String dimension) {
        this((float) origin.getX(), (float) origin.getZ(), dimension);
    }

    public SerializableChunkPos(long pos, String dimension) {
        this((int) pos, (int) (pos >> 32), dimension);
    }

    public SerializableChunkPos(float blockX, float blockZ, String dimension) {
        this((int) blockX >> 4, (int) blockZ >> 4, dimension);
    }

    public SerializableChunkPos(int x, int z, String dimension) {
        this.x = x;
        this.z = z;
        this.dimension = dimension;
    }

    public static SerializableChunkPos read(PacketByteBuf buf) {
        int x = buf.readInt();
        int z = buf.readInt();
        String dimension = buf.readString(32767);
        return new SerializableChunkPos(x, z, dimension);
    }

    public float distanceFrom(SerializableChunkPos pos) {
        float firstSq = Math.abs(pos.getX() - getX());
        float secSq = Math.abs(pos.getZ() - getZ());
        return (float) Math.sqrt(firstSq * firstSq + secSq * secSq);
    }

    @Override
    public String toString() {
        return "[" + getX() + ", " + getZ() + "] in " + dimension;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SerializableChunkPos) {
            SerializableChunkPos other = (SerializableChunkPos) obj;
            return this.getDimension().equals(other.getDimension()) && x == other.getX() && z == other.getZ();
        }
        return super.equals(obj);
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(z);
        buf.writeString(dimension);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public SerializableChunkPos getChunkAtRelativeOffset(int i, int j) {
        return new SerializableChunkPos(getWorldRelativeX(i), getWorldRelativeZ(j), getDimension());
    }

    public int getWorldRelativeX(int index) {
        return x - (LclData.SIZE / 2) + index;
    }

    public int getWorldRelativeZ(int index) {
        return z - (LclData.SIZE / 2) + index;
    }

    public String getDimension() {
        return dimension;
    }

    public RegistryKey<World> getDimensionRegistryKey() {
        if (dimension.equals(World.OVERWORLD.getValue().getPath())) {
            return World.OVERWORLD;
        }
        else if (dimension.equals(World.NETHER.getValue().getPath())) {
            return World.NETHER;
        }
        else if (dimension.equals(World.END.getValue().getPath())) {
            return World.END;
        }
        return null;
    }
}
