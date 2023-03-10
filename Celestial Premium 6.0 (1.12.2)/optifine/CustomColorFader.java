/*
 * Decompiled with CFR 0.150.
 */
package optifine;

import net.minecraft.util.math.Vec3d;
import optifine.Config;

public class CustomColorFader {
    private Vec3d color = null;
    private long timeUpdate = System.currentTimeMillis();

    public Vec3d getColor(double p_getColor_1_, double p_getColor_3_, double p_getColor_5_) {
        if (this.color == null) {
            this.color = new Vec3d(p_getColor_1_, p_getColor_3_, p_getColor_5_);
            return this.color;
        }
        long i = System.currentTimeMillis();
        long j = i - this.timeUpdate;
        if (j == 0L) {
            return this.color;
        }
        this.timeUpdate = i;
        if (Math.abs(p_getColor_1_ - this.color.x) < 0.004 && Math.abs(p_getColor_3_ - this.color.y) < 0.004 && Math.abs(p_getColor_5_ - this.color.z) < 0.004) {
            return this.color;
        }
        double d0 = (double)j * 0.001;
        d0 = Config.limit(d0, 0.0, 1.0);
        double d1 = p_getColor_1_ - this.color.x;
        double d2 = p_getColor_3_ - this.color.y;
        double d3 = p_getColor_5_ - this.color.z;
        double d4 = this.color.x + d1 * d0;
        double d5 = this.color.y + d2 * d0;
        double d6 = this.color.z + d3 * d0;
        this.color = new Vec3d(d4, d5, d6);
        return this.color;
    }
}

