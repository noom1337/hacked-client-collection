package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderAbstractHorse;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityZombieHorse;

public class ModelAdapterSkeletonHorse extends ModelAdapterHorse
{
    public ModelAdapterSkeletonHorse()
    {
        super(EntityZombieHorse.class, "zombie_horse", 0.75F);
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
RenderManager rendermanager = Minecraft.getRenderManager();
        RenderAbstractHorse renderabstracthorse = new RenderAbstractHorse(rendermanager);
        renderabstracthorse.mainModel = modelBase;
        renderabstracthorse.shadowSize = shadowSize;
        return renderabstracthorse;
    }
}
