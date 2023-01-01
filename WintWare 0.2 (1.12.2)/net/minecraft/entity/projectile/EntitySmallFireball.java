package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySmallFireball extends EntityFireball {
   public EntitySmallFireball(World worldIn) {
      super(worldIn);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3125F, 0.3125F);
   }

   public EntitySmallFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(worldIn, x, y, z, accelX, accelY, accelZ);
      this.setSize(0.3125F, 0.3125F);
   }

   public static void registerFixesSmallFireball(DataFixer fixer) {
      EntityFireball.registerFixesFireball(fixer, "SmallFireball");
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         boolean flag;
         if (result.entityHit != null) {
            if (!result.entityHit.isImmuneToFire()) {
               flag = result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);
               if (flag) {
                  this.applyEnchantments(this.shootingEntity, result.entityHit);
                  result.entityHit.setFire(5);
               }
            }
         } else {
            flag = true;
            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
               flag = this.world.getGameRules().getBoolean("mobGriefing");
            }

            if (flag) {
               BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
               if (this.world.isAirBlock(blockpos)) {
                  this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
               }
            }
         }

         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }
}
