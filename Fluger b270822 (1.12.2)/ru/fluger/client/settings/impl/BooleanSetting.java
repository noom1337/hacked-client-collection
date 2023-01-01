package ru.fluger.client.settings.impl;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import ru.fluger.client.settings.Setting;

public class BooleanSetting extends Setting {
   private boolean state;
   private Block block;
   private String desc;

   public BooleanSetting(String name, Block block, boolean state, Supplier visible) {
      this.name = name;
      this.block = block;
      this.state = state;
      this.setVisible(visible);
   }

   public BooleanSetting(String name, String desc, boolean state, Supplier visible) {
      this.name = name;
      this.desc = desc;
      this.state = state;
      this.setVisible(visible);
   }

   public BooleanSetting(String name, boolean state) {
      this.name = name;
      this.state = state;
      this.setVisible(() -> {
         return true;
      });
   }

   public BooleanSetting(String name, boolean state, Supplier visible) {
      this.name = name;
      this.state = state;
      this.setVisible(visible);
   }

   public Block getBlock() {
      return this.block;
   }

   public void setBlock(Block block) {
      this.block = block;
   }

   public String getDesc() {
      return this.desc;
   }

   public void setDesc(String desc) {
      this.desc = desc;
   }

   public boolean getCurrentValue() {
      return this.state;
   }

   public void setValue(boolean state) {
      this.state = state;
   }
}
