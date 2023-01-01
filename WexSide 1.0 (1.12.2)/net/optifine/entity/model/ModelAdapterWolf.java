package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.optifine.entity.model.anim.RefUtils;
import optifine.ReflectorClass;
import optifine.ReflectorField;

public class ModelAdapterWolf extends ModelAdapter
{
    public ModelAdapterWolf()
    {
        super(EntityWolf.class, "wolf", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelWolf();
    }

    public static ReflectorClass ModelWolf = new ReflectorClass(ModelWolf.class);
    public static ReflectorField ModelWolf_tail = new ReflectorField(ModelWolf, ModelRenderer.class, 6);
    public static ReflectorField ModelWolf_mane = new ReflectorField(ModelWolf, ModelRenderer.class, 7);

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelWolf))
        {
            return null;
        }
        else
        {
            ModelWolf modelwolf = (ModelWolf)model;

            if (modelPart.equals("head"))
            {
                return modelwolf.wolfHeadMain;
            }
            else if (modelPart.equals("body"))
            {
                return modelwolf.wolfBody;
            }
            else if (modelPart.equals("leg1"))
            {
                return modelwolf.wolfLeg1;
            }
            else if (modelPart.equals("leg2"))
            {
                return modelwolf.wolfLeg2;
            }
            else if (modelPart.equals("leg3"))
            {
                return modelwolf.wolfLeg3;
            }
            else if (modelPart.equals("leg4"))
            {
                return modelwolf.wolfLeg4;
            }
            else if (modelPart.equals("tail"))
            {
                return (ModelRenderer) RefUtils.getFieldValue(modelwolf, ModelWolf_tail);
            }
            else
            {
                return modelPart.equals("mane") ? (ModelRenderer)RefUtils.getFieldValue(modelwolf, ModelWolf_mane) : null;
            }
        }
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderWolf renderwolf = new RenderWolf(rendermanager);
        renderwolf.mainModel = modelBase;
        renderwolf.shadowSize = shadowSize;
        return renderwolf;
    }
}
