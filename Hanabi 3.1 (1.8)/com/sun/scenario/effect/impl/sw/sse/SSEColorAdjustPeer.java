package com.sun.scenario.effect.impl.sw.sse;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.ColorAdjust;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;

public class SSEColorAdjustPeer extends SSEEffectPeer {
   public SSEColorAdjustPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }

   protected final ColorAdjust getEffect() {
      return (ColorAdjust)super.getEffect();
   }

   private float getHue() {
      return this.getEffect().getHue() / 2.0F;
   }

   private float getSaturation() {
      return this.getEffect().getSaturation() + 1.0F;
   }

   private float getBrightness() {
      return this.getEffect().getBrightness() + 1.0F;
   }

   private float getContrast() {
      float var1 = this.getEffect().getContrast();
      if (var1 > 0.0F) {
         var1 *= 3.0F;
      }

      return var1 + 1.0F;
   }

   public ImageData filter(Effect var1, RenderState var2, BaseTransform var3, Rectangle var4, ImageData... var5) {
      this.setEffect(var1);
      Rectangle var6 = this.getResultBounds(var3, var4, var5);
      this.setDestBounds(var6);
      HeapImage var7 = (HeapImage)var5[0].getTransformedImage(var6);
      byte var8 = 0;
      byte var9 = 0;
      int var10 = var7.getPhysicalWidth();
      int var11 = var7.getPhysicalHeight();
      int var12 = var7.getScanlineStride();
      int[] var13 = var7.getPixelArray();
      Rectangle var14 = new Rectangle(var8, var9, var10, var11);
      Rectangle var15 = var5[0].getTransformedBounds(var6);
      BaseTransform var16 = BaseTransform.IDENTITY_TRANSFORM;
      this.setInputBounds(0, var15);
      this.setInputNativeBounds(0, var14);
      float[] var17 = new float[4];
      this.getTextureCoordinates(0, var17, (float)var15.x, (float)var15.y, (float)var10, (float)var11, var6, var16);
      int var20 = var6.width;
      int var21 = var6.height;
      HeapImage var22 = (HeapImage)this.getRenderer().getCompatibleImage(var20, var21);
      this.setDestNativeBounds(var22.getPhysicalWidth(), var22.getPhysicalHeight());
      int var23 = var22.getScanlineStride();
      int[] var24 = var22.getPixelArray();
      float var25 = this.getBrightness();
      float var26 = this.getContrast();
      float var27 = this.getHue();
      float var28 = this.getSaturation();
      filter(var24, 0, 0, var20, var21, var23, var13, var17[0], var17[1], var17[2], var17[3], var10, var11, var12, var25, var26, var27, var28);
      var5[0].releaseTransformedImage(var7);
      return new ImageData(this.getFilterContext(), var22, var6);
   }

   private static native void filter(int[] var0, int var1, int var2, int var3, int var4, int var5, int[] var6, float var7, float var8, float var9, float var10, int var11, int var12, int var13, float var14, float var15, float var16, float var17);
}
