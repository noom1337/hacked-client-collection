package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;

public abstract class Team {
   public boolean isSameTeam(@Nullable Team other) {
      if (other == null) {
         return false;
      } else {
         return this == other;
      }
   }

   public abstract String getRegisteredName();

   public abstract String formatString(String var1);

   public abstract boolean getSeeFriendlyInvisiblesEnabled();

   public abstract boolean getAllowFriendlyFire();

   public abstract Team.EnumVisible getNameTagVisibility();

   public abstract TextFormatting getChatFormat();

   public abstract Collection<String> getMembershipCollection();

   public abstract Team.EnumVisible getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public static enum EnumVisible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Team.EnumVisible> nameMap = Maps.newHashMap();
      public final String internalName;
      public final int id;

      public static String[] getNames() {
         return (String[])((String[])nameMap.keySet().toArray(new String[nameMap.size()]));
      }

      @Nullable
      public static Team.EnumVisible getByName(String nameIn) {
         return (Team.EnumVisible)nameMap.get(nameIn);
      }

      private EnumVisible(String nameIn, int idIn) {
         this.internalName = nameIn;
         this.id = idIn;
      }

      static {
         Team.EnumVisible[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            Team.EnumVisible team$enumvisible = var0[var2];
            nameMap.put(team$enumvisible.internalName, team$enumvisible);
         }

      }
   }

   public static enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("pushOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("pushOwnTeam", 3);

      private static final Map<String, Team.CollisionRule> nameMap = Maps.newHashMap();
      public final String name;
      public final int id;

      public static String[] getNames() {
         return (String[])((String[])nameMap.keySet().toArray(new String[nameMap.size()]));
      }

      @Nullable
      public static Team.CollisionRule getByName(String nameIn) {
         return (Team.CollisionRule)nameMap.get(nameIn);
      }

      private CollisionRule(String nameIn, int idIn) {
         this.name = nameIn;
         this.id = idIn;
      }

      static {
         Team.CollisionRule[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            Team.CollisionRule team$collisionrule = var0[var2];
            nameMap.put(team$collisionrule.name, team$collisionrule);
         }

      }
   }
}