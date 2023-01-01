package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
   private static final AxisAlignedBB[] NETHER_WART_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.6875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D)};

   protected BlockNetherWart() {
      super(Material.PLANTS, MapColor.RED);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      this.setTickRandomly(true);
      this.setCreativeTab((CreativeTabs)null);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return NETHER_WART_AABB[(Integer)state.getValue(AGE)];
   }

   protected boolean canSustainBush(IBlockState state) {
      return state.getBlock() == Blocks.SOUL_SAND;
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return this.canSustainBush(worldIn.getBlockState(pos.down()));
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int i = (Integer)state.getValue(AGE);
      if (i < 3 && rand.nextInt(10) == 0) {
         state = state.withProperty(AGE, i + 1);
         worldIn.setBlockState(pos, state, 2);
      }

      super.updateTick(worldIn, pos, state, rand);
   }

   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         int i = 1;
         if ((Integer)state.getValue(AGE) >= 3) {
            i = 2 + worldIn.rand.nextInt(3);
            if (fortune > 0) {
               i += worldIn.rand.nextInt(fortune + 1);
            }
         }

         for(int j = 0; j < i; ++j) {
            spawnAsEntity(worldIn, pos, new ItemStack(Items.NETHER_WART));
         }
      }

   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.field_190931_a;
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Items.NETHER_WART);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta);
   }

   public int getMetaFromState(IBlockState state) {
      return (Integer)state.getValue(AGE);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE});
   }
}
