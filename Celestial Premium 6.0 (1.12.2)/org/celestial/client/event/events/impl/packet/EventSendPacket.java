/*
 * Decompiled with CFR 0.150.
 */
package org.celestial.client.event.events.impl.packet;

import net.minecraft.network.Packet;
import org.celestial.client.event.events.callables.EventCancellable;

public class EventSendPacket
extends EventCancellable {
    public Packet<?> packet;

    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}

