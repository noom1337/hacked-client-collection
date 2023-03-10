package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;

public class SPacketUpdateTileEntity implements Packet<INetHandlerPlayClient>
{
    private BlockPos blockPos;

    /** Used only for vanilla tile entities */
    private int metadata;
    private NBTTagCompound nbt;

    public SPacketUpdateTileEntity()
    {
    }

    public SPacketUpdateTileEntity(BlockPos blockPosIn, int metadataIn, NBTTagCompound compoundIn)
    {
        blockPos = blockPosIn;
        metadata = metadataIn;
        nbt = compoundIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        blockPos = buf.readBlockPos();
        metadata = buf.readUnsignedByte();
        nbt = buf.readNBTTagCompoundFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(blockPos);
        buf.writeByte((byte) metadata);
        buf.writeNBTTagCompoundToBuffer(nbt);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleUpdateTileEntity(this);
    }

    public BlockPos getPos()
    {
        return blockPos;
    }

    public int getTileEntityType()
    {
        return metadata;
    }

    public NBTTagCompound getNbtCompound()
    {
        return nbt;
    }
}
