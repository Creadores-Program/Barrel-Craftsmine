package com.nukkitx.protocol.bedrock.data;

public enum GeneratorType {
    OLD,
    INFINITE,
    FLAT;

    private static final GeneratorType[] VALUES = values();

    public static GeneratorType from(int id) {
        return VALUES[id];
    }
}
