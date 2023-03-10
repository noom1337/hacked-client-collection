package net.minecraft.entity.ai;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAITempt extends EntityAIBase
{
    /** The entity using this AI that is tempted by the player. */
    private final EntityCreature temptedEntity;
    private final double speed;

    /** X position of player tempting this mob */
    private double targetX;

    /** Y position of player tempting this mob */
    private double targetY;

    /** Z position of player tempting this mob */
    private double targetZ;

    /** Tempting player's pitch */
    private double pitch;

    /** Tempting player's yaw */
    private double yaw;

    /** The player that is tempting the entity that is using this AI. */
    private EntityPlayer temptingPlayer;

    /**
     * A counter that is decremented each time the shouldExecute method is called. The shouldExecute method will always
     * return false if delayTemptCounter is greater than 0.
     */
    private int delayTemptCounter;

    /** True if this EntityAITempt task is running */
    private boolean isRunning;
    private final Set<Item> temptItem;

    /**
     * Whether the entity using this AI will be scared by the tempter's sudden movement.
     */
    private final boolean scaredByPlayerMovement;

    public EntityAITempt(EntityCreature temptedEntityIn, double speedIn, Item temptItemIn, boolean scaredByPlayerMovementIn)
    {
        this(temptedEntityIn, speedIn, scaredByPlayerMovementIn, Sets.newHashSet(temptItemIn));
    }

    public EntityAITempt(EntityCreature temptedEntityIn, double speedIn, boolean scaredByPlayerMovementIn, Set<Item> temptItemIn)
    {
        temptedEntity = temptedEntityIn;
        speed = speedIn;
        temptItem = temptItemIn;
        scaredByPlayerMovement = scaredByPlayerMovementIn;
        setMutexBits(3);

        if (!(temptedEntityIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (delayTemptCounter > 0)
        {
            --delayTemptCounter;
            return false;
        }
        else
        {
            temptingPlayer = temptedEntity.world.getClosestPlayerToEntity(temptedEntity, 10.0D);

            if (temptingPlayer == null)
            {
                return false;
            }
            else
            {
                return isTempting(temptingPlayer.getHeldItemMainhand()) || isTempting(temptingPlayer.getHeldItemOffhand());
            }
        }
    }

    protected boolean isTempting(ItemStack stack)
    {
        return temptItem.contains(stack.getItem());
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (scaredByPlayerMovement)
        {
            if (temptedEntity.getDistanceSqToEntity(temptingPlayer) < 36.0D)
            {
                if (temptingPlayer.getDistanceSq(targetX, targetY, targetZ) > 0.010000000000000002D)
                {
                    return false;
                }

                if (Math.abs((double) temptingPlayer.rotationPitch - pitch) > 5.0D || Math.abs((double) temptingPlayer.rotationYaw - yaw) > 5.0D)
                {
                    return false;
                }
            }
            else
            {
                targetX = temptingPlayer.posX;
                targetY = temptingPlayer.posY;
                targetZ = temptingPlayer.posZ;
            }

            pitch = temptingPlayer.rotationPitch;
            yaw = temptingPlayer.rotationYaw;
        }

        return shouldExecute();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        targetX = temptingPlayer.posX;
        targetY = temptingPlayer.posY;
        targetZ = temptingPlayer.posZ;
        isRunning = true;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        temptingPlayer = null;
        temptedEntity.getNavigator().clearPathEntity();
        delayTemptCounter = 100;
        isRunning = false;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        temptedEntity.getLookHelper().setLookPositionWithEntity(temptingPlayer, (float)(temptedEntity.getHorizontalFaceSpeed() + 20), (float) temptedEntity.getVerticalFaceSpeed());

        if (temptedEntity.getDistanceSqToEntity(temptingPlayer) < 6.25D)
        {
            temptedEntity.getNavigator().clearPathEntity();
        }
        else
        {
            temptedEntity.getNavigator().tryMoveToEntityLiving(temptingPlayer, speed);
        }
    }

    /**
     * @see #isRunning
     */
    public boolean isRunning()
    {
        return isRunning;
    }
}
