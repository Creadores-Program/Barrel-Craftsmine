package com.nukkitx.protocol.bedrock.data;

public enum DifficultyType {
    PEACEFUL,
    EASY,
    NORMAL,
    HARD;

    private static final DifficultyType[] VALUES = values();

    public static DifficultyType from(int id) {
        return VALUES[id];
    }
}
