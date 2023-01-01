package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> items = Lists.newArrayList();

   public TeleportToTeam() {
      Minecraft minecraft = Minecraft.getMinecraft();
      Iterator var2 = minecraft.world.getScoreboard().getTeams().iterator();

      while(var2.hasNext()) {
         ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)var2.next();
         this.items.add(new TeleportToTeam.TeamSelectionObject(scoreplayerteam));
      }

   }

   public List<ISpectatorMenuObject> getItems() {
      return this.items;
   }

   public ITextComponent getPrompt() {
      return new TextComponentTranslation("spectatorMenu.team_teleport.prompt", new Object[0]);
   }

   public void selectItem(SpectatorMenu menu) {
      menu.selectCategory(this);
   }

   public ITextComponent getSpectatorName() {
      return new TextComponentTranslation("spectatorMenu.team_teleport", new Object[0]);
   }

   public void renderIcon(float p_178663_1_, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   public boolean isEnabled() {
      Iterator var1 = this.items.iterator();

      ISpectatorMenuObject ispectatormenuobject;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         ispectatormenuobject = (ISpectatorMenuObject)var1.next();
      } while(!ispectatormenuobject.isEnabled());

      return true;
   }

   class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam team;
      private final ResourceLocation location;
      private final List<NetworkPlayerInfo> players;

      public TeamSelectionObject(ScorePlayerTeam p_i45492_2_) {
         this.team = p_i45492_2_;
         this.players = Lists.newArrayList();
         Iterator var3 = p_i45492_2_.getMembershipCollection().iterator();

         while(var3.hasNext()) {
            String s = (String)var3.next();
            NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(s);
            if (networkplayerinfo != null) {
               this.players.add(networkplayerinfo);
            }
         }

         if (this.players.isEmpty()) {
            this.location = DefaultPlayerSkin.getDefaultSkinLegacy();
         } else {
            String s1 = ((NetworkPlayerInfo)this.players.get((new Random()).nextInt(this.players.size()))).getGameProfile().getName();
            this.location = AbstractClientPlayer.getLocationSkin(s1);
            AbstractClientPlayer.getDownloadImageSkin(this.location, s1);
         }

      }

      public void selectItem(SpectatorMenu menu) {
         menu.selectCategory(new TeleportToPlayer(this.players));
      }

      public ITextComponent getSpectatorName() {
         return new TextComponentString(this.team.getTeamName());
      }

      public void renderIcon(float p_178663_1_, int alpha) {
         int i = -1;
         String s = FontRenderer.getFormatFromString(this.team.getColorPrefix());
         if (s.length() >= 2) {
            i = Minecraft.getMinecraft().fontRendererObj.getColorCode(s.charAt(1));
         }

         if (i >= 0) {
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            Gui.drawRect(1.0D, 1.0D, 15.0D, 15.0D, MathHelper.rgb(f * p_178663_1_, f1 * p_178663_1_, f2 * p_178663_1_) | alpha << 24);
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(this.location);
         GlStateManager.color(p_178663_1_, p_178663_1_, p_178663_1_, (float)alpha / 255.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      }

      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
