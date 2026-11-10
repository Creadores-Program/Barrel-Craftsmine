package com.nukkitx.protocol.bedrock.data;

public enum DimensionType {
    OVERWORLD,
    NETHER,
    END;

    private static final DimensionType[] VALUES = values();

    public static DimensionType from(int id) {
        return VALUES[id];
    }
}
