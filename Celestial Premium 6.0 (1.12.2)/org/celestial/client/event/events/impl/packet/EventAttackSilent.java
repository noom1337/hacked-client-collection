/*
 * Decompiled with CFR 0.150.
 */
package org.celestial.client.event.events.impl.packet;

import net.minecraft.entity.Entity;
import org.celestial.client.event.events.callables.EventCancellable;

public class EventAttackSilent
extends EventCancellable {
    private final Entity targetEntity;

    public EventAttackSilent(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Entity getTargetEntity() {
        return this.targetEntity;
    }
}

