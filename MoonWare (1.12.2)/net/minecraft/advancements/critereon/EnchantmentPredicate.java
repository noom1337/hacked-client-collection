package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Namespaced;

public class EnchantmentPredicate
{
    public static final EnchantmentPredicate field_192466_a = new EnchantmentPredicate();
    private final Enchantment field_192467_b;
    private final MinMaxBounds field_192468_c;

    public EnchantmentPredicate()
    {
        field_192467_b = null;
        field_192468_c = MinMaxBounds.field_192516_a;
    }

    public EnchantmentPredicate(@Nullable Enchantment p_i47436_1_, MinMaxBounds p_i47436_2_)
    {
        field_192467_b = p_i47436_1_;
        field_192468_c = p_i47436_2_;
    }

    public boolean func_192463_a(Map<Enchantment, Integer> p_192463_1_)
    {
        if (field_192467_b != null)
        {
            if (!p_192463_1_.containsKey(field_192467_b))
            {
                return false;
            }

            int i = p_192463_1_.get(field_192467_b).intValue();

            return field_192468_c == null || field_192468_c.func_192514_a((float) i);
        }
        else if (field_192468_c != null)
        {
            for (Integer integer : p_192463_1_.values())
            {
                if (field_192468_c.func_192514_a((float)integer.intValue()))
                {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public static EnchantmentPredicate func_192464_a(@Nullable JsonElement p_192464_0_)
    {
        if (p_192464_0_ != null && !p_192464_0_.isJsonNull())
        {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_192464_0_, "enchantment");
            Enchantment enchantment = null;

            if (jsonobject.has("enchantment"))
            {
                Namespaced resourcelocation = new Namespaced(JsonUtils.getString(jsonobject, "enchantment"));
                enchantment = Enchantment.REGISTRY.getObject(resourcelocation);

                if (enchantment == null)
                {
                    throw new JsonSyntaxException("Unknown enchantment '" + resourcelocation + "'");
                }
            }

            MinMaxBounds minmaxbounds = MinMaxBounds.func_192515_a(jsonobject.get("levels"));
            return new EnchantmentPredicate(enchantment, minmaxbounds);
        }
        else
        {
            return field_192466_a;
        }
    }

    public static EnchantmentPredicate[] func_192465_b(@Nullable JsonElement p_192465_0_)
    {
        if (p_192465_0_ != null && !p_192465_0_.isJsonNull())
        {
            JsonArray jsonarray = JsonUtils.getJsonArray(p_192465_0_, "enchantments");
            EnchantmentPredicate[] aenchantmentpredicate = new EnchantmentPredicate[jsonarray.size()];

            for (int i = 0; i < aenchantmentpredicate.length; ++i)
            {
                aenchantmentpredicate[i] = func_192464_a(jsonarray.get(i));
            }

            return aenchantmentpredicate;
        }
        else
        {
            return new EnchantmentPredicate[0];
        }
    }
}
