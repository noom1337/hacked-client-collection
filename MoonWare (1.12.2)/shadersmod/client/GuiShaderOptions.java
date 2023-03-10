package shadersmod.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import optifine.Config;
import optifine.GuiScreenOF;
import optifine.Lang;
import optifine.StrUtils;

public class GuiShaderOptions extends GuiScreenOF
{
    private Screen prevScreen;
    protected String title;
    private GameSettings settings;
    private int lastMouseX;
    private int lastMouseY;
    private long mouseStillTime;
    private String screenName;
    private String screenText;
    private boolean changed;
    public static final String OPTION_PROFILE = "<profile>";
    public static final String OPTION_EMPTY = "<empty>";
    public static final String OPTION_REST = "*";

    public GuiShaderOptions(Screen guiscreen, GameSettings gamesettings)
    {
        lastMouseX = 0;
        lastMouseY = 0;
        mouseStillTime = 0L;
        screenName = null;
        screenText = null;
        changed = false;
        title = "Shader Options";
        prevScreen = guiscreen;
        settings = gamesettings;
    }

    public GuiShaderOptions(Screen guiscreen, GameSettings gamesettings, String screenName)
    {
        this(guiscreen, gamesettings);
        this.screenName = screenName;

        if (screenName != null)
        {
            screenText = Shaders.translate("screen." + screenName, screenName);
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void init()
    {
        title = I18n.format("of.options.shaderOptionsTitle");
        int i = 100;
        int j = 0;
        int k = 30;
        int l = 20;
        int i1 = width - 130;
        int j1 = 120;
        int k1 = 20;
        int l1 = Shaders.getShaderPackColumns(screenName, 2);
        ShaderOption[] ashaderoption = Shaders.getShaderPackOptions(screenName);

        if (ashaderoption != null)
        {
            int i2 = MathHelper.ceil((double)ashaderoption.length / 9.0D);

            if (l1 < i2)
            {
                l1 = i2;
            }

            for (int j2 = 0; j2 < ashaderoption.length; ++j2)
            {
                ShaderOption shaderoption = ashaderoption[j2];

                if (shaderoption != null && shaderoption.isVisible())
                {
                    int k2 = j2 % l1;
                    int l2 = j2 / l1;
                    int i3 = Math.min(width / l1, 200);
                    j = (width - i3 * l1) / 2;
                    int j3 = k2 * i3 + 5 + j;
                    int k3 = k + l2 * l;
                    int l3 = i3 - 10;
                    String s = getButtonText(shaderoption, l3);
                    GuiButtonShaderOption guibuttonshaderoption;

                    if (Shaders.isShaderPackOptionSlider(shaderoption.getName()))
                    {
                        guibuttonshaderoption = new GuiSliderShaderOption(i + j2, j3, k3, l3, k1, shaderoption, s);
                    }
                    else
                    {
                        guibuttonshaderoption = new GuiButtonShaderOption(i + j2, j3, k3, l3, k1, shaderoption, s);
                    }

                    guibuttonshaderoption.enabled = shaderoption.isEnabled();
                    widgets.add(guibuttonshaderoption);
                }
            }
        }

        widgets.add(new ButtonWidget(201, width / 2 - j1 - 20, height / 6 + 168 + 11, j1, k1, I18n.format("controls.reset")));
        widgets.add(new ButtonWidget(200, width / 2 + 20, height / 6 + 168 + 11, j1, k1, I18n.format("gui.done")));
    }

    public static String getButtonText(ShaderOption so, int btnWidth)
    {
        String s = so.getNameText();

        if (so instanceof ShaderOptionScreen)
        {
            ShaderOptionScreen shaderoptionscreen = (ShaderOptionScreen)so;
            return s + "...";
        }
        else
        {
            Font fontrenderer = Minecraft.font;

            for (int i = fontrenderer.getStringWidth(": " + Lang.getOff()) + 5; fontrenderer.getStringWidth(s) + i >= btnWidth && s.length() > 0; s = s.substring(0, s.length() - 1))
            {
            }

            String s1 = so.isChanged() ? so.getValueColor(so.getValue()) : "";
            String s2 = so.getValueText(so.getValue());
            return s + ": " + s1 + s2;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    public void actionPerformed(ButtonWidget button)
    {
        if (button.enabled)
        {
            if (button.id < 200 && button instanceof GuiButtonShaderOption)
            {
                GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption) button;
                ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();

                if (shaderoption instanceof ShaderOptionScreen)
                {
                    String s = shaderoption.getName();
                    GuiShaderOptions guishaderoptions = new GuiShaderOptions(this, settings, s);
                    Minecraft.openScreen(guishaderoptions);
                    return;
                }

                if (Screen.hasShiftDown())
                {
                    shaderoption.resetValue();
                }
                else
                {
                    shaderoption.nextValue();
                }

                updateAllButtons();
                changed = true;
            }

            if (button.id == 201)
            {
                ShaderOption[] ashaderoption = Shaders.getChangedOptions(Shaders.getShaderPackOptions());

                for (int i = 0; i < ashaderoption.length; ++i)
                {
                    ShaderOption shaderoption1 = ashaderoption[i];
                    shaderoption1.resetValue();
                    changed = true;
                }

                updateAllButtons();
            }

            if (button.id == 200)
            {
                if (changed)
                {
                    Shaders.saveShaderPackOptions();
                    changed = false;
                    Shaders.uninit();
                }

                Minecraft.openScreen(prevScreen);
            }
        }
    }

    protected void actionPerformedRightClick(ButtonWidget btn)
    {
        if (btn instanceof GuiButtonShaderOption)
        {
            GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)btn;
            ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();

            if (Screen.hasShiftDown())
            {
                shaderoption.resetValue();
            }
            else
            {
                shaderoption.prevValue();
            }

            updateAllButtons();
            changed = true;
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onClosed()
    {
        super.onClosed();

        if (changed)
        {
            Shaders.saveShaderPackOptions();
            changed = false;
            Shaders.uninit();
        }
    }

    private void updateAllButtons()
    {
        for (ButtonWidget guibutton : widgets)
        {
            if (guibutton instanceof GuiButtonShaderOption)
            {
                GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)guibutton;
                ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();

                if (shaderoption instanceof ShaderOptionProfile)
                {
                    ShaderOptionProfile shaderoptionprofile = (ShaderOptionProfile)shaderoption;
                    shaderoptionprofile.updateProfile();
                }

                guibuttonshaderoption.text = getButtonText(shaderoption, guibuttonshaderoption.width);
                guibuttonshaderoption.valueChanged();
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void draw(int mouseX, int mouseY, float partialTick)
    {
        drawDefaultBackground();

        if (screenText != null)
        {
            drawCenteredString(font, screenText, width / 2, 15, 16777215);
        }
        else
        {
            drawCenteredString(font, title, width / 2, 15, 16777215);
        }

        super.draw(mouseX, mouseY, partialTick);

        if (Math.abs(mouseX - lastMouseX) <= 5 && Math.abs(mouseY - lastMouseY) <= 5)
        {
            drawTooltips(mouseX, mouseY, widgets);
        }
        else
        {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            mouseStillTime = System.currentTimeMillis();
        }
    }

    private void drawTooltips(int x, int y, List buttons)
    {
        int i = 700;

        if (System.currentTimeMillis() >= mouseStillTime + (long)i)
        {
            int j = width / 2 - 150;
            int k = height / 6 - 7;

            if (y <= k + 98)
            {
                k += 105;
            }

            int l = j + 150 + 150;
            int i1 = k + 84 + 10;
            ButtonWidget guibutton = GuiScreenOF.getSelectedButton(buttons, x, y);

            if (guibutton instanceof GuiButtonShaderOption)
            {
                GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption)guibutton;
                ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();
                String[] astring = makeTooltipLines(shaderoption, l - j);

                if (astring == null)
                {
                    return;
                }

                drawGradientRect(j, k, l, i1, -536870912, -536870912);

                for (int j1 = 0; j1 < astring.length; ++j1)
                {
                    String s = astring[j1];
                    int k1 = 14540253;

                    if (s.endsWith("!"))
                    {
                        k1 = 16719904;
                    }

                    font.drawStringWithShadow(s, (float)(j + 5), (float)(k + 5 + j1 * 11), k1);
                }
            }
        }
    }

    private String[] makeTooltipLines(ShaderOption so, int width)
    {
        if (so instanceof ShaderOptionProfile)
        {
            return null;
        }
        else
        {
            String s = so.getNameText();
            String s1 = Config.normalize(so.getDescriptionText()).trim();
            String[] astring = splitDescription(s1);
            String s2 = null;

            if (!s.equals(so.getName()) && settings.advancedItemTooltips)
            {
                s2 = "\u00a78" + Lang.get("of.general.id") + ": " + so.getName();
            }

            String s3 = null;

            if (so.getPaths() != null && settings.advancedItemTooltips)
            {
                s3 = "\u00a78" + Lang.get("of.general.from") + ": " + Config.arrayToString(so.getPaths());
            }

            String s4 = null;

            if (so.getValueDefault() != null && settings.advancedItemTooltips)
            {
                String s5 = so.isEnabled() ? so.getValueText(so.getValueDefault()) : Lang.get("of.general.ambiguous");
                s4 = "\u00a78" + Lang.getDefault() + ": " + s5;
            }

            List<String> list = new ArrayList<String>();
            list.add(s);
            list.addAll(Arrays.asList(astring));

            if (s2 != null)
            {
                list.add(s2);
            }

            if (s3 != null)
            {
                list.add(s3);
            }

            if (s4 != null)
            {
                list.add(s4);
            }

            String[] astring1 = makeTooltipLines(width, list);
            return astring1;
        }
    }

    private String[] splitDescription(String desc)
    {
        if (desc.length() <= 0)
        {
            return new String[0];
        }
        else
        {
            desc = StrUtils.removePrefix(desc, "//");
            String[] astring = desc.split("\\. ");

            for (int i = 0; i < astring.length; ++i)
            {
                astring[i] = "- " + astring[i].trim();
                astring[i] = StrUtils.removeSuffix(astring[i], ".");
            }

            return astring;
        }
    }

    private String[] makeTooltipLines(int width, List<String> args)
    {
        Font fontrenderer = Minecraft.font;
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < args.size(); ++i)
        {
            String s = args.get(i);

            if (s != null && s.length() > 0)
            {
                for (String s1 : fontrenderer.split(s, width))
                {
                    list.add(s1);
                }
            }
        }

        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}
