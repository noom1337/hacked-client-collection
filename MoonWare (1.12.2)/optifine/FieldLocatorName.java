package optifine;

import java.lang.reflect.Field;

public class FieldLocatorName implements IFieldLocator
{
    private ReflectorClass reflectorClass;
    private String targetFieldName;

    public FieldLocatorName(ReflectorClass p_i38_1_, String p_i38_2_)
    {
        reflectorClass = p_i38_1_;
        targetFieldName = p_i38_2_;
    }

    public Field getField()
    {
        Class oclass = reflectorClass.getTargetClass();

        if (oclass == null)
        {
            return null;
        }
        else
        {
            try
            {
                Field field = getDeclaredField(oclass, targetFieldName);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException var3)
            {
                return null;
            }
            catch (SecurityException securityexception)
            {
                securityexception.printStackTrace();
                return null;
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
                return null;
            }
        }
    }

    private Field getDeclaredField(Class p_getDeclaredField_1_, String p_getDeclaredField_2_) throws NoSuchFieldException
    {
        Field[] afield = p_getDeclaredField_1_.getDeclaredFields();

        for (int i = 0; i < afield.length; ++i)
        {
            Field field = afield[i];

            if (field.getName().equals(p_getDeclaredField_2_))
            {
                return field;
            }
        }

        if (p_getDeclaredField_1_ == Object.class)
        {
            throw new NoSuchFieldException(p_getDeclaredField_2_);
        }
        else
        {
            return getDeclaredField(p_getDeclaredField_1_.getSuperclass(), p_getDeclaredField_2_);
        }
    }
}
