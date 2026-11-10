package com.nukkitx.protocol.bedrock.data.inventory;

public enum CraftingType {
    INVENTORY,
    CRAFTING,
    WORKBENCH;

    private static final CraftingType[] VALUES = values();

    public static CraftingType from(int id) {
        return VALUES[id];
    }
}
