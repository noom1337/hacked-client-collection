package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ItemNameTag extends Item
{
    public ItemNameTag()
    {
        setCreativeTab(CreativeTabs.TOOLS);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        if (stack.hasDisplayName() && !(target instanceof EntityPlayer))
        {
            target.setCustomNameTag(stack.getDisplayName());

            if (target instanceof EntityLiving)
            {
                ((EntityLiving)target).enablePersistence();
            }

            stack.func_190918_g(1);
            return true;
        }
        else
        {
            return false;
        }
    }
}
