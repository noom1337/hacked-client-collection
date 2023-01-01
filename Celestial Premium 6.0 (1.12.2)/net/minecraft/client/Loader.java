/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  ru.wendoxd.celestial.Celestial
 *  ru.wendoxd.wclassguard.WXFuscator
 */
package net.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.lang.reflect.Type;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Session;
import ru.wendoxd.celestial.Celestial;
import ru.wendoxd.wclassguard.WXFuscator;

public class Loader {
    public static void init() {
    }

    @WXFuscator
    public static void postMain(String[] p_main_0_) {
        if (Celestial.initialized == null) {
            return;
        }
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        ArgumentAcceptingOptionSpec optionspec = optionparser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)25565, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo((Object)new File("."), (Object[])new File[0]);
        ArgumentAcceptingOptionSpec optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo((Object)"8080", (Object[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec optionspec9 = optionparser.accepts("username").withRequiredArg().defaultsTo((Object)("Celestial" + Minecraft.getSystemTime() % 1000L), (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionspec10 = optionparser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec optionspec12 = optionparser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo((Object)854, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo((Object)480, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionspec15 = optionparser.accepts("userProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionspec17 = optionparser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo((Object)"legacy", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionspec19 = optionparser.accepts("versionType").withRequiredArg().defaultsTo((Object)"release", (Object[])new String[0]);
        NonOptionArgumentSpec optionspec20 = optionparser.nonOptions();
        OptionSet optionset = optionparser.parse(p_main_0_);
        List list = optionset.valuesOf((OptionSpec)optionspec20);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }
        String s = (String)optionset.valueOf((OptionSpec)optionspec5);
        Proxy proxy = Proxy.NO_PROXY;
        if (s != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(s, (int)((Integer)optionset.valueOf((OptionSpec)optionspec6))));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        final String s1 = (String)optionset.valueOf((OptionSpec)optionspec7);
        final String s2 = (String)optionset.valueOf((OptionSpec)optionspec8);
        if (!proxy.equals(Proxy.NO_PROXY) && Minecraft.isNullOrEmpty(s1) && Minecraft.isNullOrEmpty(s2)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(s1, s2.toCharArray());
                }
            });
        }
        int i = (Integer)optionset.valueOf((OptionSpec)optionspec13);
        int j = (Integer)optionset.valueOf((OptionSpec)optionspec14);
        boolean flag = optionset.has("fullscreen");
        boolean flag1 = optionset.has("checkGlErrors");
        boolean flag2 = optionset.has("demo");
        String s3 = (String)optionset.valueOf((OptionSpec)optionspec12);
        Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)PropertyMap.class), new PropertyMap.Serializer()).create();
        PropertyMap propertymap = JsonUtils.gsonDeserialize(gson, (String)optionset.valueOf((OptionSpec)optionspec15), PropertyMap.class);
        PropertyMap propertymap1 = JsonUtils.gsonDeserialize(gson, (String)optionset.valueOf((OptionSpec)optionspec16), PropertyMap.class);
        String s4 = (String)optionset.valueOf((OptionSpec)optionspec19);
        File file1 = (File)optionset.valueOf((OptionSpec)optionspec2);
        File file2 = optionset.has((OptionSpec)optionspec3) ? (File)optionset.valueOf((OptionSpec)optionspec3) : new File(file1, "assets/");
        File file3 = optionset.has((OptionSpec)optionspec4) ? (File)optionset.valueOf((OptionSpec)optionspec4) : new File(file1, "resourcepacks/");
        String s5 = optionset.has((OptionSpec)optionspec10) ? (String)optionspec10.value(optionset) : (String)optionspec9.value(optionset);
        String s6 = optionset.has((OptionSpec)optionspec17) ? (String)optionspec17.value(optionset) : null;
        String s7 = (String)optionset.valueOf((OptionSpec)optionspec);
        Integer integer = (Integer)optionset.valueOf((OptionSpec)optionspec1);
        Session session = new Session((String)optionspec9.value(optionset), s5, (String)optionspec11.value(optionset), (String)optionspec18.value(optionset));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, propertymap, propertymap1, proxy), new GameConfiguration.DisplayInformation(i, j, flag, flag1), new GameConfiguration.FolderInformation(file1, file3, file2, s6), new GameConfiguration.GameInformation(flag2, s3, s4), new GameConfiguration.ServerInformation(s7, integer));
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });
        Thread.currentThread().setName("Client thread");
        new Minecraft(gameconfiguration).run();
    }
}

