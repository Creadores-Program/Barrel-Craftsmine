package org.CreadoresProgram.CraftsMine.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps modern Bedrock effect ids to Minecraft Pocket Edition 0.15.x effect ids.
 *
 * <p>0.15.x supports exactly 23 effects (ids 1..23: SPEED..SATURATION). Modern
 * Bedrock adds LEVITATION (24), FATAL_POISON (25), SLOW_FALLING (27),
 * CONDUIT_POWER (29), DOLPHINS_GRACE (30), BAD_OMEN (28), VILLAGE_HERO (31),
 * and several others from 1.16+ (e.g. DARKNESS, BIGGER_GROWTH, etc.).</p>
 *
 * <p>Strategy per the user spec: <b>ignore effects that do not exist in 0.15.x</b>.
 * The MobEffectPacket translator should call {@link #mapToLegacy(int)} and if
 * the result is {@link #DROP} it skips sending the packet to the 0.15.x client
 * rather than sending an unknown effect id (which the client would either
 * silently ignore or, in pathological cases, render as a wrong effect).</p>
 */
public final class EffectMapper {

    /**
     * Returned when the modern effect does NOT exist in 0.15.x. The translator
     * should drop the MobEffectPacket entirely.
     */
    public static final int DROP = -1;

    /** 0.15.x effect ids. */
    public static final int SPEED = 1;
    public static final int SLOWNESS = 2;
    public static final int HASTE = 3;
    public static final int MINING_FATIGUE = 4;
    public static final int STRENGTH = 5;
    public static final int INSTANT_HEALTH = 6;
    public static final int INSTANT_DAMAGE = 7;
    public static final int JUMP_BOOST = 8;
    public static final int NAUSEA = 9;
    public static final int REGENERATION = 10;
    public static final int RESISTANCE = 11;
    public static final int FIRE_RESISTANCE = 12;
    public static final int WATER_BREATHING = 13;
    public static final int INVISIBILITY = 14;
    public static final int BLINDNESS = 15;
    public static final int NIGHT_VISION = 16;
    public static final int HUNGER = 17;
    public static final int WEAKNESS = 18;
    public static final int POISON = 19;
    public static final int WITHER = 20;
    public static final int HEALTH_BOOST = 21;
    public static final int ABSORPTION = 22;
    public static final int SATURATION = 23;

    /**
     * Modern Bedrock effect id (>=24 means post-0.15.x) -> 0.15.x effect id,
     * or {@link #DROP} if the effect does not exist.
     */
    private static final Map<Integer, Integer> EFFECT_MAP = new HashMap<>();

    static {
        // 1..23 are the same in both versions. Pass-through entries are
        // implicit; mapToLegacy returns the input when the table has no
        // explicit override.
        // LEVITATION (24) -> drop
        EFFECT_MAP.put(24, DROP);
        // FATAL_POISON (25) -> poison (19) - it is the same debuff visually
        EFFECT_MAP.put(25, POISON);
        // VILLAGE_HERO (26) -> drop (no village-hero effect in 0.15.x)
        EFFECT_MAP.put(26, DROP);
        // BAD_OMEN (28) -> drop
        EFFECT_MAP.put(28, DROP);
        // SLOW_FALLING (27) -> drop
        EFFECT_MAP.put(27, DROP);
        // CONDUIT_POWER (29) -> drop
        EFFECT_MAP.put(29, DROP);
        // DOLPHINS_GRACE (30) -> drop
        EFFECT_MAP.put(30, DROP);
        // Anything >= 31 is post-1.13 Bedrock and absent from 0.15.x.
        // Bigger_growth, darkness, etc. all drop.
    }

    private EffectMapper() {}

    /**
     * Map a modern Bedrock effect id to its 0.15.x equivalent.
     *
     * @param modernEffectId modern Bedrock effect id (typically from a
     *                       MobEffectPacket sent by the destination server)
     * @return 0.15.x effect id, or {@link #DROP} (-1) if the effect does not
     *         exist in 0.15.x and the packet should be silently ignored
     */
    public static int mapToLegacy(int modernEffectId) {
        Integer mapped = EFFECT_MAP.get(modernEffectId);
        if (mapped != null) return mapped;
        // ids 1..23 are unchanged between versions; anything else drops.
        if (modernEffectId >= 1 && modernEffectId <= 23) return modernEffectId;
        return DROP;
    }

    /** Returns true when the modern effect does NOT exist in 0.15.x. */
    public static boolean isDropped(int modernEffectId) {
        return mapToLegacy(modernEffectId) == DROP;
    }
}
