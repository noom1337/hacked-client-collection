package net.optifine.entity.model.anim;

public class Constant implements IExpression
{
    private float value;

    public Constant(float value)
    {
        this.value = value;
    }

    public float eval()
    {
        return value;
    }

    public String toString()
    {
        return "" + value;
    }
}
