package org.spray.infinity.via.protocol;

import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;

public class ViaFabricHostnameProtocol extends AbstractSimpleProtocol {
    public static final ViaFabricHostnameProtocol INSTANCE = new ViaFabricHostnameProtocol();

    @Override
    protected void registerPackets() {
        registerServerbound(State.HANDSHAKE, 0, 0, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Protocol version
                map(Type.STRING, new ValueTransformer<String, String>(Type.STRING) {
                    @Override
                    public String transform(PacketWrapper packetWrapper, String s) {
                        return s;
                    }
                });
            }
        });
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }
}
