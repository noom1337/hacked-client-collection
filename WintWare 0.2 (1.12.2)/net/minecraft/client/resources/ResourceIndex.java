package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceIndex {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, File> resourceMap = Maps.newHashMap();

   protected ResourceIndex() {
   }

   public ResourceIndex(File assetsFolder, String indexName) {
      File file1 = new File(assetsFolder, "objects");
      File file2 = new File(assetsFolder, "indexes/" + indexName + ".json");
      BufferedReader bufferedreader = null;

      try {
         bufferedreader = Files.newReader(file2, StandardCharsets.UTF_8);
         JsonObject jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
         JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", (JsonObject)null);
         if (jsonobject1 != null) {
            Iterator var8 = jsonobject1.entrySet().iterator();

            while(var8.hasNext()) {
               Entry<String, JsonElement> entry = (Entry)var8.next();
               JsonObject jsonobject2 = (JsonObject)entry.getValue();
               String s = (String)entry.getKey();
               String[] astring = s.split("/", 2);
               String s1 = astring.length == 1 ? astring[0] : astring[0] + ":" + astring[1];
               String s2 = JsonUtils.getString(jsonobject2, "hash");
               File file3 = new File(file1, s2.substring(0, 2) + "/" + s2);
               this.resourceMap.put(s1, file3);
            }
         }
      } catch (JsonParseException var20) {
         LOGGER.error("Unable to parse resource index file: {}", file2);
      } catch (FileNotFoundException var21) {
         LOGGER.error("Can't find the resource index file: {}", file2);
      } finally {
         IOUtils.closeQuietly(bufferedreader);
      }

   }

   @Nullable
   public File getFile(ResourceLocation location) {
      String s = location.toString();
      return (File)this.resourceMap.get(s);
   }

   public boolean isFileExisting(ResourceLocation location) {
      File file1 = this.getFile(location);
      return file1 != null && file1.isFile();
   }

   public File getPackMcmeta() {
      return (File)this.resourceMap.get("pack.mcmeta");
   }
}
