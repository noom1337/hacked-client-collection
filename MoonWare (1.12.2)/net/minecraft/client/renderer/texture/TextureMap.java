package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.MathHelper;
import optifine.BetterGrass;
import optifine.Config;
import optifine.ConnectedTextures;
import optifine.CustomItems;
import optifine.Reflector;
import optifine.ReflectorForge;
import optifine.SpriteDependencies;
import optifine.TextureUtils;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadersmod.client.ShadersTex;

public class TextureMap extends AbstractTexture implements ITickableTextureObject
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Namespaced LOCATION_MISSING_TEXTURE = new Namespaced("missingno");
    public static final Namespaced LOCATION_BLOCKS_TEXTURE = new Namespaced("textures/atlas/blocks.png");
    private final List<TextureAtlasSprite> listAnimatedSprites;
    private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
    private final Map<String, TextureAtlasSprite> mapUploadedSprites;
    private final String basePath;
    private final ITextureMapPopulator iconCreator;
    private int mipmapLevels;
    private final TextureAtlasSprite missingImage;
    private TextureAtlasSprite[] iconGrid;
    private int iconGridSize;
    private int iconGridCountX;
    private int iconGridCountY;
    private double iconGridSizeU;
    private double iconGridSizeV;
    private int counterIndexInMap;
    public int atlasWidth;
    public int atlasHeight;

    public TextureMap(String basePathIn)
    {
        this(basePathIn, null);
    }

    public TextureMap(String p_i3_1_, boolean p_i3_2_)
    {
        this(p_i3_1_, null, p_i3_2_);
    }

    public TextureMap(String basePathIn, @Nullable ITextureMapPopulator iconCreatorIn)
    {
        this(basePathIn, iconCreatorIn, false);
    }

    public TextureMap(String p_i4_1_, ITextureMapPopulator p_i4_2_, boolean p_i4_3_)
    {
        iconGrid = null;
        iconGridSize = -1;
        iconGridCountX = -1;
        iconGridCountY = -1;
        iconGridSizeU = -1.0D;
        iconGridSizeV = -1.0D;
        counterIndexInMap = 0;
        atlasWidth = 0;
        atlasHeight = 0;
        listAnimatedSprites = Lists.newArrayList();
        mapRegisteredSprites = Maps.newHashMap();
        mapUploadedSprites = Maps.newHashMap();
        missingImage = new TextureAtlasSprite("missingno");
        basePath = p_i4_1_;
        iconCreator = p_i4_2_;
    }

    private void initMissingImage()
    {
        int i = getMinSpriteSize();
        int[] aint = getMissingImageData(i);
        missingImage.setIconWidth(i);
        missingImage.setIconHeight(i);
        int[][] aint1 = new int[mipmapLevels + 1][];
        aint1[0] = aint;
        missingImage.setFramesTextureData(Lists.newArrayList(new int[][][] {aint1}));
        missingImage.setIndexInMap(counterIndexInMap++);
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        ShadersTex.resManager = resourceManager;

        if (iconCreator != null)
        {
            loadSprites(resourceManager, iconCreator);
        }
    }

    public void loadSprites(IResourceManager resourceManager, ITextureMapPopulator iconCreatorIn)
    {
        mapRegisteredSprites.clear();
        counterIndexInMap = 0;
        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, this);
        iconCreatorIn.registerSprites(this);

        if (mipmapLevels >= 4)
        {
            mipmapLevels = detectMaxMipmapLevel(mapRegisteredSprites, resourceManager);
            Config.log("Mipmap levels: " + mipmapLevels);
        }

        initMissingImage();
        deleteGlTexture();
        loadTextureAtlas(resourceManager);
    }

    public void loadTextureAtlas(IResourceManager resourceManager)
    {
        ShadersTex.resManager = resourceManager;
        Config.dbg("Multitexture: " + Config.isMultiTexture());

        if (Config.isMultiTexture())
        {
            for (TextureAtlasSprite textureatlassprite : mapUploadedSprites.values())
            {
                textureatlassprite.deleteSpriteTexture();
            }
        }

        ConnectedTextures.updateIcons(this);
        CustomItems.updateIcons(this);
        BetterGrass.updateIcons(this);
        int k1 = TextureUtils.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(k1, k1, 0, mipmapLevels);
        mapUploadedSprites.clear();
        listAnimatedSprites.clear();
        int i = Integer.MAX_VALUE;
        int j = getMinSpriteSize();
        iconGridSize = j;
        int k = 1 << mipmapLevels;
        List<TextureAtlasSprite> list = new ArrayList<TextureAtlasSprite>(mapRegisteredSprites.values());

        for (int l = 0; l < list.size(); ++l)
        {
            TextureAtlasSprite textureatlassprite1 = SpriteDependencies.resolveDependencies(list, l, this);
            Namespaced resourcelocation = getResourceLocation(textureatlassprite1);
            IResource iresource = null;

            if (textureatlassprite1.getIndexInMap() < 0)
            {
                textureatlassprite1.setIndexInMap(counterIndexInMap++);
            }

            if (textureatlassprite1.hasCustomLoader(resourceManager, resourcelocation))
            {
                if (textureatlassprite1.load(resourceManager, resourcelocation, (p_lambda$loadTextureAtlas$0_1_) ->
            {
                return mapRegisteredSprites.get(p_lambda$loadTextureAtlas$0_1_.toString());
                }))
                {
                    Config.dbg("Custom loader (skipped): " + textureatlassprite1);
                    continue;
                }
                Config.dbg("Custom loader: " + textureatlassprite1);
            }
            else
            {
                try
                {
                    PngSizeInfo pngsizeinfo = PngSizeInfo.makeFromResource(resourceManager.getResource(resourcelocation));

                    if (Config.isShaders())
                    {
                        iresource = ShadersTex.loadResource(resourceManager, resourcelocation);
                    }
                    else
                    {
                        iresource = resourceManager.getResource(resourcelocation);
                    }

                    boolean flag = iresource.getMetadata("animation") != null;
                    textureatlassprite1.loadSprite(pngsizeinfo, flag);
                }
                catch (RuntimeException runtimeexception)
                {
                    LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
                    ReflectorForge.FMLClientHandler_trackBrokenTexture(resourcelocation, runtimeexception.getMessage());
                    continue;
                }
                catch (IOException ioexception)
                {
                    LOGGER.error("Using missing texture, unable to load " + resourcelocation + ", " + ioexception.getClass().getName());
                    ReflectorForge.FMLClientHandler_trackMissingTexture(resourcelocation);
                    continue;
                }
                finally
                {
                    IOUtils.closeQuietly(iresource);
                }
            }

            int k2 = textureatlassprite1.getIconWidth();
            int i3 = textureatlassprite1.getIconHeight();

            if (k2 >= 1 && i3 >= 1)
            {
                if (k2 < j || mipmapLevels > 0)
                {
                    int i1 = mipmapLevels > 0 ? TextureUtils.scaleToPowerOfTwo(k2, j) : TextureUtils.scaleMinTo(k2, j);

                    if (i1 != k2)
                    {
                        if (!TextureUtils.isPowerOfTwo(k2))
                        {
                            Config.log("Scaled non power of 2: " + textureatlassprite1.getIconName() + ", " + k2 + " -> " + i1);
                        }
                        else
                        {
                            Config.log("Scaled too small texture: " + textureatlassprite1.getIconName() + ", " + k2 + " -> " + i1);
                        }

                        int j1 = i3 * i1 / k2;
                        textureatlassprite1.setIconWidth(i1);
                        textureatlassprite1.setIconHeight(j1);
                    }
                }

                i = Math.min(i, Math.min(textureatlassprite1.getIconWidth(), textureatlassprite1.getIconHeight()));
                int j3 = Math.min(Integer.lowestOneBit(textureatlassprite1.getIconWidth()), Integer.lowestOneBit(textureatlassprite1.getIconHeight()));

                if (j3 < k)
                {
                    LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", resourcelocation, Integer.valueOf(textureatlassprite1.getIconWidth()), Integer.valueOf(textureatlassprite1.getIconHeight()), Integer.valueOf(MathHelper.log2(k)), Integer.valueOf(MathHelper.log2(j3)));
                    k = j3;
                }

                if (generateMipmaps(resourceManager, textureatlassprite1))
                {
                    stitcher.addSprite(textureatlassprite1);
                }
            }
            else
            {
                Config.warn("Invalid sprite size: " + textureatlassprite1);
            }
        }

        int l1 = Math.min(i, k);
        int i2 = MathHelper.log2(l1);

        if (i2 < 0)
        {
            i2 = 0;
        }

        if (i2 < mipmapLevels)
        {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", basePath, Integer.valueOf(mipmapLevels), Integer.valueOf(i2), Integer.valueOf(l1));
            mipmapLevels = i2;
        }

        missingImage.generateMipmaps(mipmapLevels);
        stitcher.addSprite(missingImage);

        try
        {
            stitcher.doStitch();
        }
        catch (StitcherException stitcherexception)
        {
            throw stitcherexception;
        }

        LOGGER.info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()), Integer.valueOf(stitcher.getCurrentHeight()), basePath);

        if (Config.isShaders())
        {
            ShadersTex.allocateTextureMap(getGlTextureId(), mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), stitcher, this);
        }
        else
        {
            TextureUtil.allocateTextureImpl(getGlTextureId(), mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
        }

        Map<String, TextureAtlasSprite> map = Maps.newHashMap(mapRegisteredSprites);

        for (TextureAtlasSprite textureatlassprite2 : stitcher.getStichSlots())
        {
            if (Config.isShaders())
            {
                ShadersTex.setIconName(ShadersTex.setSprite(textureatlassprite2).getIconName());
            }

            String s = textureatlassprite2.getIconName();
            map.remove(s);
            mapUploadedSprites.put(s, textureatlassprite2);

            try
            {
                if (Config.isShaders())
                {
                    ShadersTex.uploadTexSubForLoadAtlas(textureatlassprite2.getFrameTextureData(0), textureatlassprite2.getIconWidth(), textureatlassprite2.getIconHeight(), textureatlassprite2.getOriginX(), textureatlassprite2.getOriginY(), false, false);
                }
                else
                {
                    TextureUtil.uploadTextureMipmap(textureatlassprite2.getFrameTextureData(0), textureatlassprite2.getIconWidth(), textureatlassprite2.getIconHeight(), textureatlassprite2.getOriginX(), textureatlassprite2.getOriginY(), false, false);
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
                crashreportcategory.addCrashSection("Atlas path", basePath);
                crashreportcategory.addCrashSection("Sprite", textureatlassprite2);
                throw new ReportedException(crashreport);
            }

            if (textureatlassprite2.hasAnimationMetadata())
            {
                listAnimatedSprites.add(textureatlassprite2);
            }
        }

        for (TextureAtlasSprite textureatlassprite3 : map.values())
        {
            textureatlassprite3.copyFrom(missingImage);
        }

        if (Config.isMultiTexture())
        {
            int j2 = stitcher.getCurrentWidth();
            int l2 = stitcher.getCurrentHeight();

            for (TextureAtlasSprite textureatlassprite4 : stitcher.getStichSlots())
            {
                textureatlassprite4.sheetWidth = j2;
                textureatlassprite4.sheetHeight = l2;
                textureatlassprite4.mipmapLevels = mipmapLevels;
                TextureAtlasSprite textureatlassprite5 = textureatlassprite4.spriteSingle;

                if (textureatlassprite5 != null)
                {
                    if (textureatlassprite5.getIconWidth() <= 0)
                    {
                        textureatlassprite5.setIconWidth(textureatlassprite4.getIconWidth());
                        textureatlassprite5.setIconHeight(textureatlassprite4.getIconHeight());
                        textureatlassprite5.initSprite(textureatlassprite4.getIconWidth(), textureatlassprite4.getIconHeight(), 0, 0, false);
                        textureatlassprite5.clearFramesTextureData();
                        List<int[][]> list1 = textureatlassprite4.getFramesTextureData();
                        textureatlassprite5.setFramesTextureData(list1);
                        textureatlassprite5.setAnimationMetadata(textureatlassprite4.getAnimationMetadata());
                    }

                    textureatlassprite5.sheetWidth = j2;
                    textureatlassprite5.sheetHeight = l2;
                    textureatlassprite5.mipmapLevels = mipmapLevels;
                    textureatlassprite4.bindSpriteTexture();
                    boolean flag2 = false;
                    boolean flag1 = true;

                    try
                    {
                        TextureUtil.uploadTextureMipmap(textureatlassprite5.getFrameTextureData(0), textureatlassprite5.getIconWidth(), textureatlassprite5.getIconHeight(), textureatlassprite5.getOriginX(), textureatlassprite5.getOriginY(), flag2, flag1);
                    }
                    catch (Exception exception)
                    {
                        Config.dbg("Error uploading sprite single: " + textureatlassprite5 + ", parent: " + textureatlassprite4);
                        exception.printStackTrace();
                    }
                }
            }

            Minecraft.getTextureManager().bindTexture(LOCATION_BLOCKS_TEXTURE);
        }

        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, this);
        updateIconGrid(stitcher.getCurrentWidth(), stitcher.getCurrentHeight());

        if (Config.equals(System.getProperty("saveTextureMap"), "true"))
        {
            Config.dbg("Exporting texture map: " + basePath);
            TextureUtils.saveGlTexture("debug/" + basePath.replaceAll("/", "_"), getGlTextureId(), mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
        }
    }

    public boolean generateMipmaps(IResourceManager resourceManager, TextureAtlasSprite texture)
    {
        Namespaced resourcelocation1 = getResourceLocation(texture);
        IResource iresource1 = null;

        if (texture.hasCustomLoader(resourceManager, resourcelocation1))
        {
            TextureUtils.generateCustomMipmaps(texture, mipmapLevels);
        }
        else
        {
            label60:
            {
                boolean flag4;

                try
                {
                    iresource1 = resourceManager.getResource(resourcelocation1);
                    texture.loadSpriteFrames(iresource1, mipmapLevels + 1);
                    break label60;
                }
                catch (RuntimeException runtimeexception1)
                {
                    LOGGER.error("Unable to parse metadata from {}", resourcelocation1, runtimeexception1);
                    flag4 = false;
                }
                catch (IOException ioexception1)
                {
                    LOGGER.error("Using missing texture, unable to load {}", resourcelocation1, ioexception1);
                    flag4 = false;
                    boolean crashreportcategory = flag4;
                    return crashreportcategory;
                }
                finally
                {
                    IOUtils.closeQuietly(iresource1);
                }

                return flag4;
            }
        }

        try
        {
            texture.generateMipmaps(mipmapLevels);
            return true;
        }
        catch (Throwable throwable1)
        {
            CrashReport crashreport1 = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
            CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Sprite being mipmapped");
            crashreportcategory1.setDetail("Sprite name", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getIconName();
                }
            });
            crashreportcategory1.setDetail("Sprite size", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getIconWidth() + " x " + texture.getIconHeight();
                }
            });
            crashreportcategory1.setDetail("Sprite frames", new ICrashReportDetail<String>()
            {
                public String call() throws Exception
                {
                    return texture.getFrameCount() + " frames";
                }
            });
            crashreportcategory1.addCrashSection("Mipmap levels", Integer.valueOf(mipmapLevels));
            throw new ReportedException(crashreport1);
        }
    }

    public Namespaced getResourceLocation(TextureAtlasSprite p_184396_1_)
    {
        Namespaced resourcelocation1 = new Namespaced(p_184396_1_.getIconName());
        return completeResourceLocation(resourcelocation1);
    }

    public Namespaced completeResourceLocation(Namespaced p_completeNamespaced_1_)
    {
        return isAbsoluteLocation(p_completeNamespaced_1_) ? new Namespaced(p_completeNamespaced_1_.getNamespace(), p_completeNamespaced_1_.getPath() + ".png") : new Namespaced(p_completeNamespaced_1_.getNamespace(), String.format("%s/%s%s", basePath, p_completeNamespaced_1_.getPath(), ".png"));
    }

    public TextureAtlasSprite getAtlasSprite(String iconName)
    {
        TextureAtlasSprite textureatlassprite6 = mapUploadedSprites.get(iconName);

        if (textureatlassprite6 == null)
        {
            textureatlassprite6 = missingImage;
        }

        return textureatlassprite6;
    }

    public void updateAnimations()
    {
        if (Config.isShaders())
        {
            ShadersTex.updatingTex = getMultiTexID();
        }

        boolean flag3 = false;
        boolean flag4 = false;
        TextureUtil.bindTexture(getGlTextureId());

        for (TextureAtlasSprite textureatlassprite6 : listAnimatedSprites)
        {
            if (isTerrainAnimationActive(textureatlassprite6))
            {
                textureatlassprite6.updateAnimation();

                if (textureatlassprite6.spriteNormal != null)
                {
                    flag3 = true;
                }

                if (textureatlassprite6.spriteSpecular != null)
                {
                    flag4 = true;
                }
            }
        }

        if (Config.isMultiTexture())
        {
            for (TextureAtlasSprite textureatlassprite8 : listAnimatedSprites)
            {
                if (isTerrainAnimationActive(textureatlassprite8))
                {
                    TextureAtlasSprite textureatlassprite7 = textureatlassprite8.spriteSingle;

                    if (textureatlassprite7 != null)
                    {
                        if (textureatlassprite8 == TextureUtils.iconClock || textureatlassprite8 == TextureUtils.iconCompass)
                        {
                            textureatlassprite7.frameCounter = textureatlassprite8.frameCounter;
                        }

                        textureatlassprite8.bindSpriteTexture();
                        textureatlassprite7.updateAnimation();
                    }
                }
            }

            TextureUtil.bindTexture(getGlTextureId());
        }

        if (Config.isShaders())
        {
            if (flag3)
            {
                TextureUtil.bindTexture(getMultiTexID().norm);

                for (TextureAtlasSprite textureatlassprite9 : listAnimatedSprites)
                {
                    if (textureatlassprite9.spriteNormal != null && isTerrainAnimationActive(textureatlassprite9))
                    {
                        if (textureatlassprite9 == TextureUtils.iconClock || textureatlassprite9 == TextureUtils.iconCompass)
                        {
                            textureatlassprite9.spriteNormal.frameCounter = textureatlassprite9.frameCounter;
                        }

                        textureatlassprite9.spriteNormal.updateAnimation();
                    }
                }
            }

            if (flag4)
            {
                TextureUtil.bindTexture(getMultiTexID().spec);

                for (TextureAtlasSprite textureatlassprite10 : listAnimatedSprites)
                {
                    if (textureatlassprite10.spriteSpecular != null && isTerrainAnimationActive(textureatlassprite10))
                    {
                        if (textureatlassprite10 == TextureUtils.iconClock || textureatlassprite10 == TextureUtils.iconCompass)
                        {
                            textureatlassprite10.spriteNormal.frameCounter = textureatlassprite10.frameCounter;
                        }

                        textureatlassprite10.spriteSpecular.updateAnimation();
                    }
                }
            }

            if (flag3 || flag4)
            {
                TextureUtil.bindTexture(getGlTextureId());
            }
        }

        if (Config.isShaders())
        {
            ShadersTex.updatingTex = null;
        }
    }

    public TextureAtlasSprite registerSprite(Namespaced location)
    {
        if (location == null)
        {
            throw new IllegalArgumentException("Location cannot be null!");
        }
        else
        {
            TextureAtlasSprite textureatlassprite6 = mapRegisteredSprites.get(location.toString());

            if (textureatlassprite6 == null)
            {
                textureatlassprite6 = TextureAtlasSprite.makeAtlasSprite(location);
                mapRegisteredSprites.put(location.toString(), textureatlassprite6);

                if (textureatlassprite6.getIndexInMap() < 0)
                {
                    textureatlassprite6.setIndexInMap(counterIndexInMap++);
                }
            }

            return textureatlassprite6;
        }
    }

    public void tick()
    {
        updateAnimations();
    }

    public void setMipmapLevels(int mipmapLevelsIn)
    {
        mipmapLevels = mipmapLevelsIn;
    }

    public TextureAtlasSprite getMissingSprite()
    {
        return missingImage;
    }

    @Nullable
    public TextureAtlasSprite getTextureExtry(String p_getTextureExtry_1_)
    {
        return mapRegisteredSprites.get(p_getTextureExtry_1_);
    }

    public boolean setTextureEntry(TextureAtlasSprite p_setTextureEntry_1_)
    {
        String s1 = p_setTextureEntry_1_.getIconName();

        if (!mapRegisteredSprites.containsKey(s1))
        {
            mapRegisteredSprites.put(s1, p_setTextureEntry_1_);

            if (p_setTextureEntry_1_.getIndexInMap() < 0)
            {
                p_setTextureEntry_1_.setIndexInMap(counterIndexInMap++);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public String getBasePath()
    {
        return basePath;
    }

    public int getMipmapLevels()
    {
        return mipmapLevels;
    }

    private boolean isAbsoluteLocation(Namespaced p_isAbsoluteLocation_1_)
    {
        String s1 = p_isAbsoluteLocation_1_.getPath();
        return isAbsoluteLocationPath(s1);
    }

    private boolean isAbsoluteLocationPath(String p_isAbsoluteLocationPath_1_)
    {
        String s1 = p_isAbsoluteLocationPath_1_.toLowerCase();
        return s1.startsWith("mcpatcher/") || s1.startsWith("optifine/");
    }

    public TextureAtlasSprite getSpriteSafe(String p_getSpriteSafe_1_)
    {
        Namespaced resourcelocation1 = new Namespaced(p_getSpriteSafe_1_);
        return mapRegisteredSprites.get(resourcelocation1.toString());
    }

    public TextureAtlasSprite getRegisteredSprite(Namespaced p_getRegisteredSprite_1_)
    {
        return mapRegisteredSprites.get(p_getRegisteredSprite_1_.toString());
    }

    private boolean isTerrainAnimationActive(TextureAtlasSprite p_isTerrainAnimationActive_1_)
    {
        if (p_isTerrainAnimationActive_1_ != TextureUtils.iconWaterStill && p_isTerrainAnimationActive_1_ != TextureUtils.iconWaterFlow)
        {
            if (p_isTerrainAnimationActive_1_ != TextureUtils.iconLavaStill && p_isTerrainAnimationActive_1_ != TextureUtils.iconLavaFlow)
            {
                if (p_isTerrainAnimationActive_1_ != TextureUtils.iconFireLayer0 && p_isTerrainAnimationActive_1_ != TextureUtils.iconFireLayer1)
                {
                    if (p_isTerrainAnimationActive_1_ == TextureUtils.iconPortal)
                    {
                        return Config.isAnimatedPortal();
                    }
                    else
                    {
                        return p_isTerrainAnimationActive_1_ == TextureUtils.iconClock || p_isTerrainAnimationActive_1_ == TextureUtils.iconCompass || Config.isAnimatedTerrain();
                    }
                }
                else
                {
                    return Config.isAnimatedFire();
                }
            }
            else
            {
                return Config.isAnimatedLava();
            }
        }
        else
        {
            return Config.isAnimatedWater();
        }
    }

    public int getCountRegisteredSprites()
    {
        return counterIndexInMap;
    }

    private int detectMaxMipmapLevel(Map p_detectMaxMipmapLevel_1_, IResourceManager p_detectMaxMipmapLevel_2_)
    {
        int k3 = detectMinimumSpriteSize(p_detectMaxMipmapLevel_1_, p_detectMaxMipmapLevel_2_, 20);

        if (k3 < 16)
        {
            k3 = 16;
        }

        k3 = MathHelper.smallestEncompassingPowerOfTwo(k3);

        if (k3 > 16)
        {
            Config.log("Sprite size: " + k3);
        }

        int l3 = MathHelper.log2(k3);

        if (l3 < 4)
        {
            l3 = 4;
        }

        return l3;
    }

    private int detectMinimumSpriteSize(Map p_detectMinimumSpriteSize_1_, IResourceManager p_detectMinimumSpriteSize_2_, int p_detectMinimumSpriteSize_3_)
    {
        Map map1 = new HashMap();

        for (Object entry : p_detectMinimumSpriteSize_1_.entrySet())
        {
            TextureAtlasSprite textureatlassprite6 = (TextureAtlasSprite)((Map.Entry) entry).getValue();
            Namespaced resourcelocation1 = new Namespaced(textureatlassprite6.getIconName());
            Namespaced resourcelocation2 = completeResourceLocation(resourcelocation1);

            if (!textureatlassprite6.hasCustomLoader(p_detectMinimumSpriteSize_2_, resourcelocation1))
            {
                try
                {
                    IResource iresource1 = p_detectMinimumSpriteSize_2_.getResource(resourcelocation2);

                    if (iresource1 != null)
                    {
                        InputStream inputstream = iresource1.getInputStream();

                        if (inputstream != null)
                        {
                            Dimension dimension = TextureUtils.getImageSize(inputstream, "png");

                            if (dimension != null)
                            {
                                int k3 = dimension.width;
                                int l3 = MathHelper.smallestEncompassingPowerOfTwo(k3);

                                if (!map1.containsKey(Integer.valueOf(l3)))
                                {
                                    map1.put(Integer.valueOf(l3), Integer.valueOf(1));
                                }
                                else
                                {
                                    int i4 = ((Integer)map1.get(Integer.valueOf(l3))).intValue();
                                    map1.put(Integer.valueOf(l3), Integer.valueOf(i4 + 1));
                                }
                            }
                        }
                    }
                }
                catch (Exception var17)
                {
                }
            }
        }

        int j4 = 0;
        Set set = map1.keySet();
        Set set1 = new TreeSet(set);
        int j5;

        for (Iterator iterator = set1.iterator(); iterator.hasNext(); j4 += j5)
        {
            int l4 = ((Integer)iterator.next()).intValue();
            j5 = ((Integer)map1.get(Integer.valueOf(l4))).intValue();
        }

        int k4 = 16;
        int i5 = 0;
        j5 = j4 * p_detectMinimumSpriteSize_3_ / 100;
        Iterator iterator1 = set1.iterator();

        while (iterator1.hasNext())
        {
            int k5 = ((Integer)iterator1.next()).intValue();
            int l5 = ((Integer)map1.get(Integer.valueOf(k5))).intValue();
            i5 += l5;

            if (k5 > k4)
            {
                k4 = k5;
            }

            if (i5 > j5)
            {
                return k4;
            }
        }

        return k4;
    }

    private int getMinSpriteSize()
    {
        int k3 = 1 << mipmapLevels;

        if (k3 < 8)
        {
            k3 = 8;
        }

        return k3;
    }

    private int[] getMissingImageData(int p_getMissingImageData_1_)
    {
        BufferedImage bufferedimage = new BufferedImage(16, 16, 2);
        bufferedimage.setRGB(0, 0, 16, 16, TextureUtil.MISSING_TEXTURE_DATA, 0, 16);
        BufferedImage bufferedimage1 = TextureUtils.scaleToPowerOfTwo(bufferedimage, p_getMissingImageData_1_);
        int[] aint = new int[p_getMissingImageData_1_ * p_getMissingImageData_1_];
        bufferedimage1.getRGB(0, 0, p_getMissingImageData_1_, p_getMissingImageData_1_, aint, 0, p_getMissingImageData_1_);
        return aint;
    }

    public boolean isTextureBound()
    {
        int k3 = GlStateManager.getBoundTexture();
        int l3 = getGlTextureId();
        return k3 == l3;
    }

    private void updateIconGrid(int p_updateIconGrid_1_, int p_updateIconGrid_2_)
    {
        iconGridCountX = -1;
        iconGridCountY = -1;
        iconGrid = null;

        if (iconGridSize > 0)
        {
            iconGridCountX = p_updateIconGrid_1_ / iconGridSize;
            iconGridCountY = p_updateIconGrid_2_ / iconGridSize;
            iconGrid = new TextureAtlasSprite[iconGridCountX * iconGridCountY];
            iconGridSizeU = 1.0D / (double) iconGridCountX;
            iconGridSizeV = 1.0D / (double) iconGridCountY;

            for (TextureAtlasSprite textureatlassprite6 : mapUploadedSprites.values())
            {
                double d0 = 0.5D / (double)p_updateIconGrid_1_;
                double d1 = 0.5D / (double)p_updateIconGrid_2_;
                double d2 = (double)Math.min(textureatlassprite6.getMinU(), textureatlassprite6.getMaxU()) + d0;
                double d3 = (double)Math.min(textureatlassprite6.getMinV(), textureatlassprite6.getMaxV()) + d1;
                double d4 = (double)Math.max(textureatlassprite6.getMinU(), textureatlassprite6.getMaxU()) - d0;
                double d5 = (double)Math.max(textureatlassprite6.getMinV(), textureatlassprite6.getMaxV()) - d1;
                int k3 = (int)(d2 / iconGridSizeU);
                int l3 = (int)(d3 / iconGridSizeV);
                int i4 = (int)(d4 / iconGridSizeU);
                int j4 = (int)(d5 / iconGridSizeV);

                for (int k4 = k3; k4 <= i4; ++k4)
                {
                    if (k4 >= 0 && k4 < iconGridCountX)
                    {
                        for (int l4 = l3; l4 <= j4; ++l4)
                        {
                            if (l4 >= 0 && l4 < iconGridCountX)
                            {
                                int i5 = l4 * iconGridCountX + k4;
                                iconGrid[i5] = textureatlassprite6;
                            }
                            else
                            {
                                Config.warn("Invalid grid V: " + l4 + ", icon: " + textureatlassprite6.getIconName());
                            }
                        }
                    }
                    else
                    {
                        Config.warn("Invalid grid U: " + k4 + ", icon: " + textureatlassprite6.getIconName());
                    }
                }
            }
        }
    }

    public TextureAtlasSprite getIconByUV(double p_getIconByUV_1_, double p_getIconByUV_3_)
    {
        if (iconGrid == null)
        {
            return null;
        }
        else
        {
            int k3 = (int)(p_getIconByUV_1_ / iconGridSizeU);
            int l3 = (int)(p_getIconByUV_3_ / iconGridSizeV);
            int i4 = l3 * iconGridCountX + k3;
            return i4 >= 0 && i4 <= iconGrid.length ? iconGrid[i4] : null;
        }
    }
}
