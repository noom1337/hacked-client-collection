package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Namespaced;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityThrowable extends Entity implements IProjectile
{
    private int xTile;
    private int yTile;
    private int zTile;
    private Block inTile;
    protected boolean inGround;
    public int throwableShake;

    /** The entity that threw this throwable item. */
    protected EntityLivingBase thrower;
    private String throwerName;
    private int ticksInGround;
    private int ticksInAir;
    public Entity ignoreEntity;
    private int ignoreTime;

    public EntityThrowable(World worldIn)
    {
        super(worldIn);
        xTile = -1;
        yTile = -1;
        zTile = -1;
        setSize(0.25F, 0.25F);
    }

    public EntityThrowable(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        setPosition(x, y, z);
    }

    public EntityThrowable(World worldIn, EntityLivingBase throwerIn)
    {
        this(worldIn, throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - 0.10000000149011612D, throwerIn.posZ);
        thrower = throwerIn;
    }

    protected void entityInit()
    {
    }

    /**
     * Checks if the entity is in range to render.
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(d0))
        {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    /**
     * Sets throwable heading based on an entity that's throwing it
     */
    public void setHeadingFromThrower(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        setThrowableHeading(f, f1, f2, velocity, inaccuracy);
        motionX += entityThrower.motionX;
        motionZ += entityThrower.motionZ;

        if (!entityThrower.onGround)
        {
            motionY += entityThrower.motionY;
        }
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        motionX = x;
        motionY = y;
        motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        rotationPitch = (float)(MathHelper.atan2(y, f1) * (180D / Math.PI));
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        ticksInGround = 0;
    }

    /**
     * Updates the velocity of the entity to a new value.
     */
    public void setVelocity(double x, double y, double z)
    {
        motionX = x;
        motionY = y;
        motionZ = z;

        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            rotationPitch = (float)(MathHelper.atan2(y, f) * (180D / Math.PI));
            prevRotationYaw = rotationYaw;
            prevRotationPitch = rotationPitch;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        lastTickPosX = posX;
        lastTickPosY = posY;
        lastTickPosZ = posZ;
        super.onUpdate();

        if (throwableShake > 0)
        {
            --throwableShake;
        }

        if (inGround)
        {
            if (world.getBlockState(new BlockPos(xTile, yTile, zTile)).getBlock() == inTile)
            {
                ++ticksInGround;

                if (ticksInGround == 1200)
                {
                    setDead();
                }

                return;
            }

            inGround = false;
            motionX *= rand.nextFloat() * 0.2F;
            motionY *= rand.nextFloat() * 0.2F;
            motionZ *= rand.nextFloat() * 0.2F;
            ticksInGround = 0;
            ticksInAir = 0;
        }
        else
        {
            ++ticksInAir;
        }

        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        Vec3d vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1);
        vec3d = new Vec3d(posX, posY, posZ);
        vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

        if (raytraceresult != null)
        {
            vec3d1 = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
        }

        Entity entity = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().addCoord(motionX, motionY, motionZ).expandXyz(1.0D));
        double d0 = 0.0D;
        boolean flag = false;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity1 = list.get(i);

            if (entity1.canBeCollidedWith())
            {
                if (entity1 == ignoreEntity)
                {
                    flag = true;
                }
                else if (thrower != null && ticksExisted < 2 && ignoreEntity == null)
                {
                    ignoreEntity = entity1;
                    flag = true;
                }
                else
                {
                    flag = false;
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
                    RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                    if (raytraceresult1 != null)
                    {
                        double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }
        }

        if (ignoreEntity != null)
        {
            if (flag)
            {
                ignoreTime = 2;
            }
            else if (ignoreTime-- <= 0)
            {
                ignoreEntity = null;
            }
        }

        if (entity != null)
        {
            raytraceresult = new RayTraceResult(entity);
        }

        if (raytraceresult != null)
        {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL)
            {
                setPortal(raytraceresult.getBlockPos());
            }
            else
            {
                onImpact(raytraceresult);
            }
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)(MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));

        for (rotationPitch = (float)(MathHelper.atan2(motionY, f) * (180D / Math.PI)); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
        {
        }

        while (rotationPitch - prevRotationPitch >= 180.0F)
        {
            prevRotationPitch += 360.0F;
        }

        while (rotationYaw - prevRotationYaw < -180.0F)
        {
            prevRotationYaw -= 360.0F;
        }

        while (rotationYaw - prevRotationYaw >= 180.0F)
        {
            prevRotationYaw += 360.0F;
        }

        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = getGravityVelocity();

        if (isInWater())
        {
            for (int j = 0; j < 4; ++j)
            {
                float f3 = 0.25F;
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D, posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX, motionY, motionZ);
            }

            f1 = 0.8F;
        }

        motionX *= f1;
        motionY *= f1;
        motionZ *= f1;

        if (!hasNoGravity())
        {
            motionY -= f2;
        }

        setPosition(posX, posY, posZ);
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.03F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected abstract void onImpact(RayTraceResult result);

    public static void registerFixesThrowable(DataFixer fixer, String name)
    {
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("xTile", xTile);
        compound.setInteger("yTile", yTile);
        compound.setInteger("zTile", zTile);
        Namespaced resourcelocation = Block.REGISTRY.getNameForObject(inTile);
        compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("shake", (byte) throwableShake);
        compound.setByte("inGround", (byte)(inGround ? 1 : 0));

        if ((throwerName == null || throwerName.isEmpty()) && thrower instanceof EntityPlayer)
        {
            throwerName = thrower.getName();
        }

        compound.setString("ownerName", throwerName == null ? "" : throwerName);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        xTile = compound.getInteger("xTile");
        yTile = compound.getInteger("yTile");
        zTile = compound.getInteger("zTile");

        if (compound.hasKey("inTile", 8))
        {
            inTile = Block.getBlockFromName(compound.getString("inTile"));
        }
        else
        {
            inTile = Block.getBlockById(compound.getByte("inTile") & 255);
        }

        throwableShake = compound.getByte("shake") & 255;
        inGround = compound.getByte("inGround") == 1;
        thrower = null;
        throwerName = compound.getString("ownerName");

        if (throwerName != null && throwerName.isEmpty())
        {
            throwerName = null;
        }

        thrower = getThrower();
    }

    @Nullable
    public EntityLivingBase getThrower()
    {
        if (thrower == null && throwerName != null && !throwerName.isEmpty())
        {
            thrower = world.getPlayerEntityByName(throwerName);

            if (thrower == null && world instanceof WorldServer)
            {
                try
                {
                    Entity entity = ((WorldServer) world).getEntityFromUuid(UUID.fromString(throwerName));

                    if (entity instanceof EntityLivingBase)
                    {
                        thrower = (EntityLivingBase)entity;
                    }
                }
                catch (Throwable var2)
                {
                    thrower = null;
                }
            }
        }

        return thrower;
    }
}
