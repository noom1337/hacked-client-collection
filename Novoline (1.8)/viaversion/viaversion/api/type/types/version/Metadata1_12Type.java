package viaversion.viaversion.api.type.types.version;

import viaversion.viaversion.api.minecraft.metadata.MetaType;
import viaversion.viaversion.api.minecraft.metadata.types.MetaType1_12;
import viaversion.viaversion.api.type.types.minecraft.ModernMetaType;

public class Metadata1_12Type extends ModernMetaType {
    @Override
    protected MetaType getType(final int index) {
        return MetaType1_12.byId(index);
    }
}
