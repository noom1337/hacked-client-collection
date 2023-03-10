/*
 * Decompiled with CFR 0.152.
 */
package com.github.steveice10.mc.protocol.data.game.world.effect;

import com.github.steveice10.mc.protocol.data.game.world.effect.WorldEffectData;
import com.github.steveice10.mc.protocol.util.ObjectUtil;

public class BonemealGrowEffectData
implements WorldEffectData {
    private int particleCount;

    public BonemealGrowEffectData(int particleCount) {
        this.particleCount = particleCount;
    }

    public int getParticleCount() {
        return this.particleCount;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BonemealGrowEffectData)) {
            return false;
        }
        BonemealGrowEffectData that = (BonemealGrowEffectData)o;
        return this.particleCount == that.particleCount;
    }

    public int hashCode() {
        return ObjectUtil.hashCode(this.particleCount);
    }

    public String toString() {
        return ObjectUtil.toString(this);
    }
}

