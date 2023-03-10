package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityGuardian;
import net.optifine.entity.model.anim.RefUtils;
import optifine.Config;
import optifine.ReflectorClass;
import optifine.ReflectorField;

public class ModelAdapterGuardian extends ModelAdapter
{
    public ModelAdapterGuardian()
    {
        super(EntityGuardian.class, "guardian", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelGuardian();
    }

    public static ReflectorClass ModelGuardian = new ReflectorClass(ModelGuardian.class);
    public static ReflectorField ModelGuardian_body = new ReflectorField(ModelGuardian, ModelRenderer.class, 0);
    public static ReflectorField ModelGuardian_eye = new ReflectorField(ModelGuardian, ModelRenderer.class, 1);
    public static ReflectorField ModelGuardian_spines = new ReflectorField(ModelGuardian, ModelRenderer[].class, 0);
    public static ReflectorField ModelGuardian_tail = new ReflectorField(ModelGuardian, ModelRenderer[].class, 1);

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelGuardian))
        {
            return null;
        }
        else
        {
            ModelGuardian modelguardian = (ModelGuardian)model;

            if (modelPart.equals("body"))
            {
                return (ModelRenderer) RefUtils.getFieldValue(modelguardian, ModelGuardian_body);
            }
            else if (modelPart.equals("eye"))
            {
                return (ModelRenderer)RefUtils.getFieldValue(modelguardian, ModelGuardian_eye);
            }
            else
            {
                String s = "spine";

                if (modelPart.startsWith(s))
                {
                    ModelRenderer[] amodelrenderer1 = (ModelRenderer[])RefUtils.getFieldValue(modelguardian, ModelGuardian_spines);

                    if (amodelrenderer1 == null)
                    {
                        return null;
                    }
                    else
                    {
                        String s3 = modelPart.substring(s.length());
                        int j = Config.parseInt(s3, -1);
                        --j;
                        return j >= 0 && j < amodelrenderer1.length ? amodelrenderer1[j] : null;
                    }
                }
                else
                {
                    String s1 = "tail";

                    if (modelPart.startsWith(s1))
                    {
                        ModelRenderer[] amodelrenderer = (ModelRenderer[])RefUtils.getFieldValue(modelguardian, ModelGuardian_tail);

                        if (amodelrenderer == null)
                        {
                            return null;
                        }
                        else
                        {
                            String s2 = modelPart.substring(s1.length());
                            int i = Config.parseInt(s2, -1);
                            --i;
                            return i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
            }
        }
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderGuardian renderguardian = new RenderGuardian(rendermanager);
        renderguardian.mainModel = modelBase;
        renderguardian.shadowSize = shadowSize;
        return renderguardian;
    }
}
