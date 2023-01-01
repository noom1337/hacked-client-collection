package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySquid extends EntityWaterMob {
   public float squidPitch;
   public float prevSquidPitch;
   public float squidYaw;
   public float prevSquidYaw;
   public float squidRotation;
   public float prevSquidRotation;
   public float tentacleAngle;
   public float lastTentacleAngle;
   private float randomMotionSpeed;
   private float rotationVelocity;
   private float rotateSpeed;
   private float randomMotionVecX;
   private float randomMotionVecY;
   private float randomMotionVecZ;

   public EntitySquid(World worldIn) {
      super(worldIn);
      this.setSize(0.8F, 0.8F);
      this.rand.setSeed((long)(1 + this.getEntityId()));
      this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
   }

   public static void registerFixesSquid(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntitySquid.class);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntitySquid.AIMoveRandom(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
   }

   public float getEyeHeight() {
      return this.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SQUID_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SQUID;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.prevSquidPitch = this.squidPitch;
      this.prevSquidYaw = this.squidYaw;
      this.prevSquidRotation = this.squidRotation;
      this.lastTentacleAngle = this.tentacleAngle;
      this.squidRotation += this.rotationVelocity;
      if ((double)this.squidRotation > 6.283185307179586D) {
         if (this.world.isRemote) {
            this.squidRotation = 6.2831855F;
         } else {
            this.squidRotation = (float)((double)this.squidRotation - 6.283185307179586D);
            if (this.rand.nextInt(10) == 0) {
               this.rotationVelocity = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
            }

            this.world.setEntityState(this, (byte)19);
         }
      }

      if (this.inWater) {
         float f1;
         if (this.squidRotation < 3.1415927F) {
            f1 = this.squidRotation / 3.1415927F;
            this.tentacleAngle = MathHelper.sin(f1 * f1 * 3.1415927F) * 3.1415927F * 0.25F;
            if ((double)f1 > 0.75D) {
               this.randomMotionSpeed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.randomMotionSpeed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.world.isRemote) {
            this.motionX = (double)(this.randomMotionVecX * this.randomMotionSpeed);
            this.motionY = (double)(this.randomMotionVecY * this.randomMotionSpeed);
            this.motionZ = (double)(this.randomMotionVecZ * this.randomMotionSpeed);
         }

         f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.renderYawOffset += (-((float)MathHelper.atan2(this.motionX, this.motionZ)) * 57.295776F - this.renderYawOffset) * 0.1F;
         this.rotationYaw = this.renderYawOffset;
         this.squidYaw = (float)((double)this.squidYaw + 3.141592653589793D * (double)this.rotateSpeed * 1.5D);
         this.squidPitch += (-((float)MathHelper.atan2((double)f1, this.motionY)) * 57.295776F - this.squidPitch) * 0.1F;
      } else {
         this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * 3.1415927F * 0.25F;
         if (!this.world.isRemote) {
            this.motionX = 0.0D;
            this.motionZ = 0.0D;
            if (this.isPotionActive(MobEffects.LEVITATION)) {
               this.motionY += 0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY;
            } else if (!this.hasNoGravity()) {
               this.motionY -= 0.08D;
            }

            this.motionY *= 0.9800000190734863D;
         }

         this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0F - this.squidPitch) * 0.02D);
      }

   }

   public void func_191986_a(float p_191986_1_, float p_191986_2_, float p_191986_3_) {
      this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
   }

   public boolean getCanSpawnHere() {
      return this.posY > 45.0D && this.posY < (double)this.world.getSeaLevel() && super.getCanSpawnHere();
   }

   public void handleStatusUpdate(byte id) {
      if (id == 19) {
         this.squidRotation = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void setMovementVector(float randomMotionVecXIn, float randomMotionVecYIn, float randomMotionVecZIn) {
      this.randomMotionVecX = randomMotionVecXIn;
      this.randomMotionVecY = randomMotionVecYIn;
      this.randomMotionVecZ = randomMotionVecZIn;
   }

   public boolean hasMovementVector() {
      return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
   }

   static class AIMoveRandom extends EntityAIBase {
      private final EntitySquid squid;

      public AIMoveRandom(EntitySquid p_i45859_1_) {
         this.squid = p_i45859_1_;
      }

      public boolean shouldExecute() {
         return true;
      }

      public void updateTask() {
         int i = this.squid.getAge();
         if (i > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.hasMovementVector()) {
            float f = this.squid.getRNG().nextFloat() * 6.2831855F;
            float f1 = MathHelper.cos(f) * 0.2F;
            float f2 = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
            float f3 = MathHelper.sin(f) * 0.2F;
            this.squid.setMovementVector(f1, f2, f3);
         }

      }
   }
}
