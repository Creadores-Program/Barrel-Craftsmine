package com.nukkitx.protocol.bedrock.data.entity;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class EntityLinkData {
    long vehicleUniqueId;
    long passengerUniqueId;

    Type type;

    byte unknownByte;

    public enum Type {
        REMOVE,
        ADD;

        private static final Type[] VALUES = values();

        public static Type byId(int id) {
            if (id >= 0 && id < VALUES.length) {
                return VALUES[id];
            }
            throw new UnsupportedOperationException("Unknown EntityLinkData.Type ID: " + id);
        }
    }
}
