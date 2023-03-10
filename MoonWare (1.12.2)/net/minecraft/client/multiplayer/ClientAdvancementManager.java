package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.util.Namespaced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientAdvancementManager
{
    private static final Logger field_192800_a = LogManager.getLogger();
    private final Minecraft field_192801_b;
    private final AdvancementList field_192802_c = new AdvancementList();
    private final Map<Advancement, AdvancementProgress> field_192803_d = Maps.newHashMap();
    @Nullable
    private ClientAdvancementManager.IListener field_192804_e;
    @Nullable
    private Advancement field_194231_f;

    public ClientAdvancementManager(Minecraft p_i47380_1_)
    {
        field_192801_b = p_i47380_1_;
    }

    public void func_192799_a(SPacketAdvancementInfo p_192799_1_)
    {
        if (p_192799_1_.func_192602_d())
        {
            field_192802_c.func_192087_a();
            field_192803_d.clear();
        }

        field_192802_c.func_192085_a(p_192799_1_.func_192600_b());
        field_192802_c.func_192083_a(p_192799_1_.func_192603_a());

        for (Map.Entry<Namespaced, AdvancementProgress> entry : p_192799_1_.func_192604_c().entrySet())
        {
            Advancement advancement = field_192802_c.func_192084_a(entry.getKey());

            if (advancement != null)
            {
                AdvancementProgress advancementprogress = entry.getValue();
                advancementprogress.func_192099_a(advancement.getCriteria(), advancement.getRequirements());
                field_192803_d.put(advancement, advancementprogress);

                if (field_192804_e != null)
                {
                    field_192804_e.func_191933_a(advancement, advancementprogress);
                }

                if (!p_192799_1_.func_192602_d() && advancementprogress.func_192105_a() && advancement.getDisplay() != null && advancement.getDisplay().func_193223_h())
                {
                    Minecraft.getToastHud().queue(new AdvancementToast(advancement));
                }
            }
            else
            {
                field_192800_a.warn("Server informed client about progress for unknown advancement " + entry.getKey());
            }
        }
    }

    public AdvancementList func_194229_a()
    {
        return field_192802_c;
    }

    public void func_194230_a(@Nullable Advancement p_194230_1_, boolean p_194230_2_)
    {
        NetHandlerPlayClient nethandlerplayclient = field_192801_b.getConnection();

        if (nethandlerplayclient != null && p_194230_1_ != null && p_194230_2_)
        {
            nethandlerplayclient.sendPacket(CPacketSeenAdvancements.func_194163_a(p_194230_1_));
        }

        if (field_194231_f != p_194230_1_)
        {
            field_194231_f = p_194230_1_;

            if (field_192804_e != null)
            {
                field_192804_e.func_193982_e(p_194230_1_);
            }
        }
    }

    public void func_192798_a(@Nullable ClientAdvancementManager.IListener p_192798_1_)
    {
        field_192804_e = p_192798_1_;
        field_192802_c.func_192086_a(p_192798_1_);

        if (p_192798_1_ != null)
        {
            for (Map.Entry<Advancement, AdvancementProgress> entry : field_192803_d.entrySet())
            {
                p_192798_1_.func_191933_a(entry.getKey(), entry.getValue());
            }

            p_192798_1_.func_193982_e(field_194231_f);
        }
    }

    public interface IListener extends AdvancementList.Listener
    {
        void func_191933_a(Advancement p_191933_1_, AdvancementProgress p_191933_2_);

        void func_193982_e(Advancement p_193982_1_);
    }
}
