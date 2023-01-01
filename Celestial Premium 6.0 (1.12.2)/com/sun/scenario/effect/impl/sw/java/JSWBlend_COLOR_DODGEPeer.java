/*
 * Decompiled with CFR 0.150.
 */
package com.sun.scenario.effect.impl.sw.java;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Blend;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.HeapImage;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;
import com.sun.scenario.effect.impl.sw.java.JSWEffectPeer;

public class JSWBlend_COLOR_DODGEPeer
extends JSWEffectPeer {
    public JSWBlend_COLOR_DODGEPeer(FilterContext filterContext, Renderer renderer, String string) {
        super(filterContext, renderer, string);
    }

    @Override
    protected final Blend getEffect() {
        return (Blend)super.getEffect();
    }

    private float getOpacity() {
        return this.getEffect().getOpacity();
    }

    @Override
    public ImageData filter(Effect effect, RenderState renderState, BaseTransform baseTransform, Rectangle rectangle, ImageData ... arrimageData) {
        this.setEffect(effect);
        Rectangle rectangle2 = this.getResultBounds(baseTransform, rectangle, arrimageData);
        this.setDestBounds(rectangle2);
        HeapImage heapImage = (HeapImage)arrimageData[0].getTransformedImage(rectangle2);
        int n = 0;
        int n2 = 0;
        int n3 = heapImage.getPhysicalWidth();
        int n4 = heapImage.getPhysicalHeight();
        int n5 = heapImage.getScanlineStride();
        int[] arrn = heapImage.getPixelArray();
        Rectangle rectangle3 = new Rectangle(n, n2, n3, n4);
        Rectangle rectangle4 = arrimageData[0].getTransformedBounds(rectangle2);
        BaseTransform baseTransform2 = BaseTransform.IDENTITY_TRANSFORM;
        this.setInputBounds(0, rectangle4);
        this.setInputNativeBounds(0, rectangle3);
        HeapImage heapImage2 = (HeapImage)arrimageData[1].getTransformedImage(rectangle2);
        int n6 = 0;
        int n7 = 0;
        int n8 = heapImage2.getPhysicalWidth();
        int n9 = heapImage2.getPhysicalHeight();
        int n10 = heapImage2.getScanlineStride();
        int[] arrn2 = heapImage2.getPixelArray();
        Rectangle rectangle5 = new Rectangle(n6, n7, n8, n9);
        Rectangle rectangle6 = arrimageData[1].getTransformedBounds(rectangle2);
        BaseTransform baseTransform3 = BaseTransform.IDENTITY_TRANSFORM;
        this.setInputBounds(1, rectangle6);
        this.setInputNativeBounds(1, rectangle5);
        float[] arrf = new float[4];
        this.getTextureCoordinates(0, arrf, rectangle4.x, rectangle4.y, n3, n4, rectangle2, baseTransform2);
        float[] arrf2 = new float[4];
        this.getTextureCoordinates(1, arrf2, rectangle6.x, rectangle6.y, n8, n9, rectangle2, baseTransform3);
        int n11 = rectangle2.width;
        int n12 = rectangle2.height;
        HeapImage heapImage3 = (HeapImage)((Object)this.getRenderer().getCompatibleImage(n11, n12));
        this.setDestNativeBounds(heapImage3.getPhysicalWidth(), heapImage3.getPhysicalHeight());
        int n13 = heapImage3.getScanlineStride();
        int[] arrn3 = heapImage3.getPixelArray();
        float f = this.getOpacity();
        float f2 = (arrf[2] - arrf[0]) / (float)n11;
        float f3 = (arrf[3] - arrf[1]) / (float)n12;
        float f4 = (arrf2[2] - arrf2[0]) / (float)n11;
        float f5 = (arrf2[3] - arrf2[1]) / (float)n12;
        float f6 = arrf[1] + f3 * 0.5f;
        float f7 = arrf2[1] + f5 * 0.5f;
        for (int i = 0; i < 0 + n12; ++i) {
            float f8 = i;
            int n14 = i * n13;
            float f9 = arrf[0] + f2 * 0.5f;
            float f10 = arrf2[0] + f4 * 0.5f;
            for (int j = 0; j < 0 + n11; ++j) {
                float f11;
                float f12;
                float f13;
                int n15;
                int n16;
                float f14 = j;
                float f15 = f9;
                float f16 = f6;
                if (f15 >= 0.0f && f16 >= 0.0f) {
                    int n17 = (int)(f15 * (float)n3);
                    int n18 = (int)(f16 * (float)n4);
                    boolean bl = n17 >= n3 || n18 >= n4;
                    n16 = bl ? 0 : arrn[n18 * n5 + n17];
                } else {
                    n16 = 0;
                }
                float f17 = (float)(n16 >> 16 & 0xFF) / 255.0f;
                float f18 = (float)(n16 >> 8 & 0xFF) / 255.0f;
                float f19 = (float)(n16 & 0xFF) / 255.0f;
                float f20 = (float)(n16 >>> 24) / 255.0f;
                f15 = f17;
                f16 = f18;
                float f21 = f19;
                float f22 = f20;
                float f23 = f10;
                float f24 = f7;
                if (f23 >= 0.0f && f24 >= 0.0f) {
                    int n19 = (int)(f23 * (float)n8);
                    int n20 = (int)(f24 * (float)n9);
                    boolean bl = n19 >= n8 || n20 >= n9;
                    n15 = bl ? 0 : arrn2[n20 * n10 + n19];
                } else {
                    n15 = 0;
                }
                f17 = (float)(n15 >> 16 & 0xFF) / 255.0f;
                f18 = (float)(n15 >> 8 & 0xFF) / 255.0f;
                f19 = (float)(n15 & 0xFF) / 255.0f;
                f20 = (float)(n15 >>> 24) / 255.0f;
                f23 = f17 * f;
                f24 = f18 * f;
                float f25 = f19 * f;
                float f26 = f20 * f;
                float f27 = f15;
                float f28 = f16;
                float f29 = f21;
                float f30 = f22;
                float f31 = f23;
                float f32 = f24;
                float f33 = f25;
                float f34 = f26;
                float f35 = f30 + f34 - f30 * f34;
                float f36 = (1.0f - f34) * f27 + (1.0f - f30) * f31;
                float f37 = (1.0f - f34) * f28 + (1.0f - f30) * f32;
                float f38 = (1.0f - f34) * f29 + (1.0f - f30) * f33;
                float f39 = f30 * f34;
                if (f27 == 0.0f) {
                    f13 = 0.0f;
                } else if (f34 == f31) {
                    f13 = f39;
                } else {
                    f13 = f34 * f34 * f27 / (f34 - f31);
                    if (f13 > f39) {
                        f13 = f39;
                    }
                }
                if (f28 == 0.0f) {
                    f12 = 0.0f;
                } else if (f34 == f32) {
                    f12 = f39;
                } else {
                    f12 = f34 * f34 * f28 / (f34 - f32);
                    if (f12 > f39) {
                        f12 = f39;
                    }
                }
                if (f29 == 0.0f) {
                    f11 = 0.0f;
                } else if (f34 == f33) {
                    f11 = f39;
                } else {
                    f11 = f34 * f34 * f29 / (f34 - f33);
                    if (f11 > f39) {
                        f11 = f39;
                    }
                }
                float f40 = f36 += f13;
                float f41 = f37 += f12;
                float f42 = f38 += f11;
                float f43 = f35;
                float f44 = f40;
                float f45 = f41;
                float f46 = f42;
                float f47 = f43;
                if (f47 < 0.0f) {
                    f47 = 0.0f;
                } else if (f47 > 1.0f) {
                    f47 = 1.0f;
                }
                if (f44 < 0.0f) {
                    f44 = 0.0f;
                } else if (f44 > f47) {
                    f44 = f47;
                }
                if (f45 < 0.0f) {
                    f45 = 0.0f;
                } else if (f45 > f47) {
                    f45 = f47;
                }
                if (f46 < 0.0f) {
                    f46 = 0.0f;
                } else if (f46 > f47) {
                    f46 = f47;
                }
                arrn3[n14 + j] = (int)(f44 * 255.0f) << 16 | (int)(f45 * 255.0f) << 8 | (int)(f46 * 255.0f) << 0 | (int)(f47 * 255.0f) << 24;
                f9 += f2;
                f10 += f4;
            }
            f6 += f3;
            f7 += f5;
        }
        arrimageData[0].releaseTransformedImage(heapImage);
        arrimageData[1].releaseTransformedImage(heapImage2);
        return new ImageData(this.getFilterContext(), heapImage3, rectangle2);
    }
}

