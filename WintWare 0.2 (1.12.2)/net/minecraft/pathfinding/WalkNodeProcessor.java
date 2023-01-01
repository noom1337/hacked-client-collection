package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessor extends NodeProcessor {
   protected float avoidsWater;

   public void initProcessor(IBlockAccess sourceIn, EntityLiving mob) {
      super.initProcessor(sourceIn, mob);
      this.avoidsWater = mob.getPathPriority(PathNodeType.WATER);
   }

   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
      super.postProcess();
   }

   public PathPoint getStart() {
      int i;
      BlockPos blockpos;
      if (this.getCanSwim() && this.entity.isInWater()) {
         i = (int)this.entity.getEntityBoundingBox().minY;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ));

         for(Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock(); block == Blocks.FLOWING_WATER || block == Blocks.WATER; block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock()) {
            ++i;
            blockpos$mutableblockpos.setPos(MathHelper.floor(this.entity.posX), i, MathHelper.floor(this.entity.posZ));
         }
      } else if (this.entity.onGround) {
         i = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D);
      } else {
         for(blockpos = new BlockPos(this.entity); (this.blockaccess.getBlockState(blockpos).getMaterial() == Material.AIR || this.blockaccess.getBlockState(blockpos).getBlock().isPassable(this.blockaccess, blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down()) {
         }

         i = blockpos.up().getY();
      }

      blockpos = new BlockPos(this.entity);
      PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, blockpos.getX(), i, blockpos.getZ());
      if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().maxZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().maxZ));
         Iterator var5 = set.iterator();

         while(var5.hasNext()) {
            BlockPos blockpos1 = (BlockPos)var5.next();
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, blockpos1);
            if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
               return this.openPoint(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
         }
      }

      return this.openPoint(blockpos.getX(), i, blockpos.getZ());
   }

   public PathPoint getPathPointToCoords(double x, double y, double z) {
      return this.openPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
   }

   public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int i = 0;
      int j = 0;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord);
      if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
         j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
      }

      BlockPos blockpos = (new BlockPos(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord)).down();
      double d0 = (double)currentPoint.yCoord - (1.0D - this.blockaccess.getBlockState(blockpos).getBoundingBox(this.blockaccess, blockpos).maxY);
      PathPoint pathpoint = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);
      PathPoint pathpoint1 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, j, d0, EnumFacing.WEST);
      PathPoint pathpoint2 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, j, d0, EnumFacing.EAST);
      PathPoint pathpoint3 = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);
      if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint;
      }

      if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint1;
      }

      if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint2;
      }

      if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathpoint3;
      }

      boolean flag = pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.nodeType == PathNodeType.OPEN || pathpoint1.costMalus != 0.0F;
      PathPoint pathpoint7;
      if (flag && flag3) {
         pathpoint7 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);
         if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint7;
         }
      }

      if (flag && flag2) {
         pathpoint7 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);
         if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint7;
         }
      }

      if (flag1 && flag3) {
         pathpoint7 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);
         if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint7;
         }
      }

      if (flag1 && flag2) {
         pathpoint7 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);
         if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint7;
         }
      }

      return i;
   }

   @Nullable
   private PathPoint getSafePoint(int x, int y, int z, int p_186332_4_, double p_186332_5_, EnumFacing facing) {
      PathPoint pathpoint = null;
      BlockPos blockpos = new BlockPos(x, y, z);
      BlockPos blockpos1 = blockpos.down();
      double d0 = (double)y - (1.0D - this.blockaccess.getBlockState(blockpos1).getBoundingBox(this.blockaccess, blockpos1).maxY);
      if (d0 - p_186332_5_ > 1.125D) {
         return null;
      } else {
         PathNodeType pathnodetype = this.getPathNodeType(this.entity, x, y, z);
         float f = this.entity.getPathPriority(pathnodetype);
         double d1 = (double)this.entity.width / 2.0D;
         if (f >= 0.0F) {
            pathpoint = this.openPoint(x, y, z);
            pathpoint.nodeType = pathnodetype;
            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         }

         if (pathnodetype == PathNodeType.WALKABLE) {
            return pathpoint;
         } else {
            if (pathpoint == null && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
               pathpoint = this.getSafePoint(x, y + 1, z, p_186332_4_ - 1, p_186332_5_, facing);
               if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0F) {
                  double d2 = (double)(x - facing.getFrontOffsetX()) + 0.5D;
                  double d3 = (double)(z - facing.getFrontOffsetZ()) + 0.5D;
                  AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, (double)y + 0.001D, d3 - d1, d2 + d1, (double)((float)y + this.entity.height), d3 + d1);
                  AxisAlignedBB axisalignedbb1 = this.blockaccess.getBlockState(blockpos).getBoundingBox(this.blockaccess, blockpos);
                  AxisAlignedBB axisalignedbb2 = axisalignedbb.addCoord(0.0D, axisalignedbb1.maxY - 0.002D, 0.0D);
                  if (this.entity.world.collidesWithAnyBlock(axisalignedbb2)) {
                     pathpoint = null;
                  }
               }
            }

            if (pathnodetype == PathNodeType.OPEN) {
               AxisAlignedBB axisalignedbb3 = new AxisAlignedBB((double)x - d1 + 0.5D, (double)y + 0.001D, (double)z - d1 + 0.5D, (double)x + d1 + 0.5D, (double)((float)y + this.entity.height), (double)z + d1 + 0.5D);
               if (this.entity.world.collidesWithAnyBlock(axisalignedbb3)) {
                  return null;
               }

               if (this.entity.width >= 1.0F) {
                  PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, x, y - 1, z);
                  if (pathnodetype1 == PathNodeType.BLOCKED) {
                     pathpoint = this.openPoint(x, y, z);
                     pathpoint.nodeType = PathNodeType.WALKABLE;
                     pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                     return pathpoint;
                  }
               }

               int var25 = 0;

               while(y > 0 && pathnodetype == PathNodeType.OPEN) {
                  --y;
                  if (var25++ >= this.entity.getMaxFallHeight()) {
                     return null;
                  }

                  pathnodetype = this.getPathNodeType(this.entity, x, y, z);
                  f = this.entity.getPathPriority(pathnodetype);
                  if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                     pathpoint = this.openPoint(x, y, z);
                     pathpoint.nodeType = pathnodetype;
                     pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                     break;
                  }

                  if (f < 0.0F) {
                     return null;
                  }
               }
            }

            return pathpoint;
         }
      }
   }

   public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
      double d0 = (double)entitylivingIn.width / 2.0D;
      BlockPos blockpos = new BlockPos(entitylivingIn);
      pathnodetype = this.func_193577_a(blockaccessIn, x, y, z, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn, enumset, pathnodetype, blockpos);
      if (enumset.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType pathnodetype1 = PathNodeType.BLOCKED;
         Iterator var17 = enumset.iterator();

         while(var17.hasNext()) {
            PathNodeType pathnodetype2 = (PathNodeType)var17.next();
            if (entitylivingIn.getPathPriority(pathnodetype2) < 0.0F) {
               return pathnodetype2;
            }

            if (entitylivingIn.getPathPriority(pathnodetype2) >= entitylivingIn.getPathPriority(pathnodetype1)) {
               pathnodetype1 = pathnodetype2;
            }
         }

         if (pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype1) == 0.0F) {
            return PathNodeType.OPEN;
         } else {
            return pathnodetype1;
         }
      }
   }

   public PathNodeType func_193577_a(IBlockAccess p_193577_1_, int p_193577_2_, int p_193577_3_, int p_193577_4_, int p_193577_5_, int p_193577_6_, int p_193577_7_, boolean p_193577_8_, boolean p_193577_9_, EnumSet<PathNodeType> p_193577_10_, PathNodeType p_193577_11_, BlockPos p_193577_12_) {
      for(int i = 0; i < p_193577_5_; ++i) {
         for(int j = 0; j < p_193577_6_; ++j) {
            for(int k = 0; k < p_193577_7_; ++k) {
               int l = i + p_193577_2_;
               int i1 = j + p_193577_3_;
               int j1 = k + p_193577_4_;
               PathNodeType pathnodetype = this.getPathNodeType(p_193577_1_, l, i1, j1);
               if (pathnodetype == PathNodeType.DOOR_WOOD_CLOSED && p_193577_8_ && p_193577_9_) {
                  pathnodetype = PathNodeType.WALKABLE;
               }

               if (pathnodetype == PathNodeType.DOOR_OPEN && !p_193577_9_) {
                  pathnodetype = PathNodeType.BLOCKED;
               }

               if (pathnodetype == PathNodeType.RAIL && !(p_193577_1_.getBlockState(p_193577_12_).getBlock() instanceof BlockRailBase) && !(p_193577_1_.getBlockState(p_193577_12_.down()).getBlock() instanceof BlockRailBase)) {
                  pathnodetype = PathNodeType.FENCE;
               }

               if (i == 0 && j == 0 && k == 0) {
                  p_193577_11_ = pathnodetype;
               }

               p_193577_10_.add(pathnodetype);
            }
         }
      }

      return p_193577_11_;
   }

   private PathNodeType getPathNodeType(EntityLiving entitylivingIn, BlockPos pos) {
      return this.getPathNodeType(entitylivingIn, pos.getX(), pos.getY(), pos.getZ());
   }

   private PathNodeType getPathNodeType(EntityLiving entitylivingIn, int x, int y, int z) {
      return this.getPathNodeType(this.blockaccess, x, y, z, entitylivingIn, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanBreakDoors(), this.getCanEnterDoors());
   }

   public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
      PathNodeType pathnodetype = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);
      if (pathnodetype == PathNodeType.OPEN && y >= 1) {
         Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
         PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
         pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
         if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA) {
            pathnodetype = PathNodeType.DAMAGE_FIRE;
         }

         if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
            pathnodetype = PathNodeType.DAMAGE_CACTUS;
         }
      }

      pathnodetype = this.func_193578_a(blockaccessIn, x, y, z, pathnodetype);
      return pathnodetype;
   }

   public PathNodeType func_193578_a(IBlockAccess p_193578_1_, int p_193578_2_, int p_193578_3_, int p_193578_4_, PathNodeType p_193578_5_) {
      BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
      if (p_193578_5_ == PathNodeType.WALKABLE) {
         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               if (i != 0 || j != 0) {
                  Block block = p_193578_1_.getBlockState(blockpos$pooledmutableblockpos.setPos(i + p_193578_2_, p_193578_3_, j + p_193578_4_)).getBlock();
                  if (block == Blocks.CACTUS) {
                     p_193578_5_ = PathNodeType.DANGER_CACTUS;
                  } else if (block == Blocks.FIRE) {
                     p_193578_5_ = PathNodeType.DANGER_FIRE;
                  }
               }
            }
         }
      }

      blockpos$pooledmutableblockpos.release();
      return p_193578_5_;
   }

   protected PathNodeType getPathNodeTypeRaw(IBlockAccess p_189553_1_, int p_189553_2_, int p_189553_3_, int p_189553_4_) {
      BlockPos blockpos = new BlockPos(p_189553_2_, p_189553_3_, p_189553_4_);
      IBlockState iblockstate = p_189553_1_.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      Material material = iblockstate.getMaterial();
      if (material == Material.AIR) {
         return PathNodeType.OPEN;
      } else if (block != Blocks.TRAPDOOR && block != Blocks.IRON_TRAPDOOR && block != Blocks.WATERLILY) {
         if (block == Blocks.FIRE) {
            return PathNodeType.DAMAGE_FIRE;
         } else if (block == Blocks.CACTUS) {
            return PathNodeType.DAMAGE_CACTUS;
         } else if (block instanceof BlockDoor && material == Material.WOOD && !(Boolean)iblockstate.getValue(BlockDoor.OPEN)) {
            return PathNodeType.DOOR_WOOD_CLOSED;
         } else if (block instanceof BlockDoor && material == Material.IRON && !(Boolean)iblockstate.getValue(BlockDoor.OPEN)) {
            return PathNodeType.DOOR_IRON_CLOSED;
         } else if (block instanceof BlockDoor && (Boolean)iblockstate.getValue(BlockDoor.OPEN)) {
            return PathNodeType.DOOR_OPEN;
         } else if (block instanceof BlockRailBase) {
            return PathNodeType.RAIL;
         } else if (!(block instanceof BlockFence) && !(block instanceof BlockWall) && (!(block instanceof BlockFenceGate) || (Boolean)iblockstate.getValue(BlockFenceGate.OPEN))) {
            if (material == Material.WATER) {
               return PathNodeType.WATER;
            } else if (material == Material.LAVA) {
               return PathNodeType.LAVA;
            } else {
               return block.isPassable(p_189553_1_, blockpos) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
            }
         } else {
            return PathNodeType.FENCE;
         }
      } else {
         return PathNodeType.TRAPDOOR;
      }
   }
}
