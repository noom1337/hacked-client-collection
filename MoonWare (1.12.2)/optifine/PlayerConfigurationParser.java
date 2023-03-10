package optifine;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

import net.minecraft.util.Namespaced;

public class PlayerConfigurationParser
{
    private String player;
    public static final String CONFIG_ITEMS = "items";
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_ACTIVE = "active";

    public PlayerConfigurationParser(String p_i70_1_)
    {
        player = p_i70_1_;
    }

    public PlayerConfiguration parsePlayerConfiguration(JsonElement p_parsePlayerConfiguration_1_)
    {
        if (p_parsePlayerConfiguration_1_ == null)
        {
            throw new JsonParseException("JSON object is null, player: " + player);
        }
        else
        {
            JsonObject jsonobject = (JsonObject)p_parsePlayerConfiguration_1_;
            PlayerConfiguration playerconfiguration = new PlayerConfiguration();
            JsonArray jsonarray = (JsonArray)jsonobject.get("items");

            if (jsonarray != null)
            {
                for (int i = 0; i < jsonarray.size(); ++i)
                {
                    JsonObject jsonobject1 = (JsonObject)jsonarray.get(i);
                    boolean flag = Json.getBoolean(jsonobject1, "active", true);

                    if (flag)
                    {
                        String s = Json.getString(jsonobject1, "type");

                        if (s == null)
                        {
                            Config.warn("Item type is null, player: " + player);
                        }
                        else
                        {
                            String s1 = Json.getString(jsonobject1, "model");

                            if (s1 == null)
                            {
                                s1 = "items/" + s + "/model.cfg";
                            }

                            PlayerItemModel playeritemmodel = downloadModel(s1);

                            if (playeritemmodel != null)
                            {
                                if (!playeritemmodel.isUsePlayerTexture())
                                {
                                    String s2 = Json.getString(jsonobject1, "texture");

                                    if (s2 == null)
                                    {
                                        s2 = "items/" + s + "/users/" + player + ".png";
                                    }

                                    BufferedImage bufferedimage = downloadTextureImage(s2);

                                    if (bufferedimage == null)
                                    {
                                        continue;
                                    }

                                    playeritemmodel.setTextureImage(bufferedimage);
                                    Namespaced resourcelocation = new Namespaced("optifine.net", s2);
                                    playeritemmodel.setTextureLocation(resourcelocation);
                                }

                                playerconfiguration.addPlayerItemModel(playeritemmodel);
                            }
                        }
                    }
                }
            }

            return playerconfiguration;
        }
    }

    private BufferedImage downloadTextureImage(String p_downloadTextureImage_1_)
    {
        String s = HttpUtils.getPlayerItemsUrl() + "/" + p_downloadTextureImage_1_;

        try
        {
            byte[] abyte = HttpPipeline.get(s, Proxy.NO_PROXY);
            BufferedImage bufferedimage = ImageIO.read(new ByteArrayInputStream(abyte));
            return bufferedimage;
        }
        catch (IOException ioexception)
        {
            Config.warn("Error loading item texture " + p_downloadTextureImage_1_ + ": " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private PlayerItemModel downloadModel(String p_downloadModel_1_)
    {
        String s = HttpUtils.getPlayerItemsUrl() + "/" + p_downloadModel_1_;

        try
        {
            byte[] abyte = HttpPipeline.get(s, Proxy.NO_PROXY);
            String s1 = new String(abyte, StandardCharsets.US_ASCII);
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = (JsonObject)jsonparser.parse(s1);
            PlayerItemModel playeritemmodel = PlayerItemParser.parseItemModel(jsonobject);
            return playeritemmodel;
        }
        catch (Exception exception)
        {
            Config.warn("Error loading item model " + p_downloadModel_1_ + ": " + exception.getClass().getName() + ": " + exception.getMessage());
            return null;
        }
    }
}
