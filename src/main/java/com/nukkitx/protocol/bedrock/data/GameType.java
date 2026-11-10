package com.nukkitx.protocol.bedrock.data;

public enum GameType {
    SURVIVAL,
    CREATIVE,
    ADVENTURE;

    private static final GameType[] VALUES = values();

    public static GameType from(int id) {
        return VALUES[id];
    }
}
