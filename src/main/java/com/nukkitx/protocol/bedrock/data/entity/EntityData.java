package com.nukkitx.protocol.bedrock.data.entity;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;

@Getter
@RequiredArgsConstructor
public enum EntityData {
    FLAGS(Type.FLAGS, true),
    HEALTH(Type.INT),
    VARIANT(Type.INT),
    COLOR(Type.BYTE),
    NAMETAG(Type.STRING),
    OWNER_EID(Type.LONG),
    TARGET_EID(Type.LONG),
    AIR_SUPPLY(Type.SHORT),
    EFFECT_COLOR(Type.INT),
    EFFECT_AMBIENT(Type.BYTE),
    JUMP_DURATION(Type.BYTE),
    HURT_TIME(Type.INT),
    HURT_DIRECTION(Type.INT),
    ROW_TIME_LEFT(Type.FLOAT),
    ROW_TIME_RIGHT(Type.FLOAT),
    EXPERIENCE_VALUE(Type.INT),
    DISPLAY_BLOCK_STATE_OR_HORSE_FLAGS_OR_WITHER_SKULL_DANGEROUS(Type.INT),
    DISPLAY_OFFSET(Type.INT),
    CUSTOM_DISPLAY(Type.BYTE),
    SWELL(Type.INT),
    OLD_SWELL(Type.INT),
    SWELL_DIRECTION(Type.INT),
    CHARGE_AMOUNT(Type.BYTE),
    ENDERMAN_ITEM_ID(Type.SHORT),
    ENDERMAN_ITEM_DAMAGE(Type.SHORT),
    AGE(Type.SHORT),
    PLAYER_FLAGS(Type.BYTE),
    PLAYER_INDEX(Type.INT),
    BED_POSITION(Type.VECTOR3I),
    FIREBALL_X_POWER(Type.FLOAT),
    FIREBALL_Y_POWER(Type.FLOAT),
    FIREBALL_Z_POWER(Type.FLOAT),
    AUX_POWER(Type.FLOAT),
    FISH_X(Type.FLOAT),
    FISH_Z(Type.FLOAT),
    FISH_ANGLE(Type.FLOAT),
    POTION_AUX_VALUE(Type.SHORT),
    LEASH_HOLDER_EID(Type.LONG),
    SCALE(Type.FLOAT),
    INTERACTIVE_TAG(Type.STRING),
    NPC_SKIN_ID(Type.STRING),
    URL_TAG(Type.STRING),
    MAX_AIR_SUPPLY(Type.SHORT),
    MARK_VARIANT(Type.INT),
    CONTAINER_TYPE(Type.BYTE),
    CONTAINER_BASE_SIZE(Type.INT),
    CONTAINER_STRENGTH_MODIFIER(Type.INT),
    BLOCK_TARGET(Type.VECTOR3I),
    WITHER_INVULNERABLE_TICKS(Type.INT),
    WITHER_CENTER_TARGET(Type.LONG),
    WITHER_LEFT_TARGET(Type.LONG),
    WITHER_RIGHT_TARGET(Type.LONG),
    WITHER_AERIAL_ATTACK(Type.SHORT),
    BOUNDING_BOX_WIDTH(Type.FLOAT),
    BOUNDING_BOX_HEIGHT(Type.FLOAT),
    FUSE_LENGTH(Type.INT),
    RIDER_SEAT_POSITION(Type.VECTOR3F),
    RIDER_ROTATION_LOCKED(Type.BYTE),
    RIDER_MAX_ROTATION(Type.FLOAT),
    RIDER_MIN_ROTATION(Type.FLOAT),
    AREA_EFFECT_CLOUD_RADIUS(Type.FLOAT),
    AREA_EFFECT_CLOUD_WAITING(Type.INT),
    AREA_EFFECT_CLOUD_PARTICLE_ID(Type.INT),
    SHULKER_PEAK_HEIGHT(Type.INT),
    SHULKER_ATTACH_FACE(Type.BYTE),
    SHULKER_UNKNOWN(Type.SHORT),
    SHULKER_ATTACH_POS(Type.VECTOR3I),
    TRADE_PLAYER_EID(Type.LONG),
    COMMAND_BLOCK_ENABLED(Type.BYTE),
    COMMAND_BLOCK_COMMAND(Type.STRING),
    COMMAND_BLOCK_LAST_OUTPUT(Type.STRING),
    COMMAND_BLOCK_TRACK_OUTPUT(Type.STRING),
    CONTROLLING_RIDER_SEAT_INDEX(Type.BYTE),
    STRENGTH(Type.INT),
    MAX_STRENGTH(Type.INT),
    EVOKER_SPELL_COLOR(Type.INT),
    ARMOR_STAND_POSE_INDEX(Type.INT),
    ENDER_CRYSTAL_TIME_OFFSET(Type.INT);

    private final Type type;
    private final boolean flags;

    EntityData(Type type) {
        this.type = type;
        this.flags = false;
    }

    @RequiredArgsConstructor
    public enum Type {
        FLAGS,
        BYTE,
        SHORT,
        INT,
        FLOAT,
        STRING,
        NBT,
        VECTOR3I,
        LONG,
        VECTOR3F;

        @Nonnull
        public static Type from(Object o) {
            if (o instanceof EntityFlags) {
                return EntityData.Type.FLAGS;
            } else if (o instanceof Byte) {
                return EntityData.Type.BYTE;
            } else if (o instanceof Short) {
                return EntityData.Type.SHORT;
            } else if (o instanceof Integer) {
                return EntityData.Type.INT;
            } else if (o instanceof Float) {
                return EntityData.Type.FLOAT;
            } else if (o instanceof String) {
                return EntityData.Type.STRING;
            } else if (o instanceof ItemData || o instanceof NbtMap) {
                return EntityData.Type.NBT;
            } else if (o instanceof Vector3i) {
                return EntityData.Type.VECTOR3I;
            } else if (o instanceof Long) {
                return EntityData.Type.LONG;
            } else if (o instanceof Vector3f) {
                return EntityData.Type.VECTOR3F;
            }
            throw new IllegalArgumentException("Invalid type");
        }
    }
}
