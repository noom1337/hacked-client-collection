package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class SPacketMaps implements Packet<INetHandlerPlayClient> {
   private int mapId;
   private byte mapScale;
   private boolean trackingPosition;
   private MapDecoration[] icons;
   private int minX;
   private int minZ;
   private int columns;
   private int rows;
   private byte[] mapDataBytes;

   public SPacketMaps() {
   }

   public SPacketMaps(int mapIdIn, byte mapScaleIn, boolean trackingPositionIn, Collection<MapDecoration> iconsIn, byte[] p_i46937_5_, int minXIn, int minZIn, int columnsIn, int rowsIn) {
      this.mapId = mapIdIn;
      this.mapScale = mapScaleIn;
      this.trackingPosition = trackingPositionIn;
      this.icons = (MapDecoration[])((MapDecoration[])iconsIn.toArray(new MapDecoration[iconsIn.size()]));
      this.minX = minXIn;
      this.minZ = minZIn;
      this.columns = columnsIn;
      this.rows = rowsIn;
      this.mapDataBytes = new byte[columnsIn * rowsIn];

      for(int i = 0; i < columnsIn; ++i) {
         for(int j = 0; j < rowsIn; ++j) {
            this.mapDataBytes[i + j * columnsIn] = p_i46937_5_[minXIn + i + (minZIn + j) * 128];
         }
      }

   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.mapId = buf.readVarIntFromBuffer();
      this.mapScale = buf.readByte();
      this.trackingPosition = buf.readBoolean();
      this.icons = new MapDecoration[buf.readVarIntFromBuffer()];

      for(int i = 0; i < this.icons.length; ++i) {
         short short1 = (short)buf.readByte();
         this.icons[i] = new MapDecoration(MapDecoration.Type.func_191159_a((byte)(short1 >> 4 & 15)), buf.readByte(), buf.readByte(), (byte)(short1 & 15));
      }

      this.columns = buf.readUnsignedByte();
      if (this.columns > 0) {
         this.rows = buf.readUnsignedByte();
         this.minX = buf.readUnsignedByte();
         this.minZ = buf.readUnsignedByte();
         this.mapDataBytes = buf.readByteArray();
      }

   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.mapId);
      buf.writeByte(this.mapScale);
      buf.writeBoolean(this.trackingPosition);
      buf.writeVarIntToBuffer(this.icons.length);
      MapDecoration[] var2 = this.icons;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MapDecoration mapdecoration = var2[var4];
         buf.writeByte((mapdecoration.getType() & 15) << 4 | mapdecoration.getRotation() & 15);
         buf.writeByte(mapdecoration.getX());
         buf.writeByte(mapdecoration.getY());
      }

      buf.writeByte(this.columns);
      if (this.columns > 0) {
         buf.writeByte(this.rows);
         buf.writeByte(this.minX);
         buf.writeByte(this.minZ);
         buf.writeByteArray(this.mapDataBytes);
      }

   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMaps(this);
   }

   public int getMapId() {
      return this.mapId;
   }

   public void setMapdataTo(MapData mapdataIn) {
      mapdataIn.scale = this.mapScale;
      mapdataIn.trackingPosition = this.trackingPosition;
      mapdataIn.mapDecorations.clear();

      int j;
      for(j = 0; j < this.icons.length; ++j) {
         MapDecoration mapdecoration = this.icons[j];
         mapdataIn.mapDecorations.put("icon-" + j, mapdecoration);
      }

      for(j = 0; j < this.columns; ++j) {
         for(int k = 0; k < this.rows; ++k) {
            mapdataIn.colors[this.minX + j + (this.minZ + k) * 128] = this.mapDataBytes[j + k * this.columns];
         }
      }

   }
}
