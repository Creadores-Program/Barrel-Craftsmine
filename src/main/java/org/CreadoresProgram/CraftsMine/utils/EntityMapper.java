package org.CreadoresProgram.CraftsMine.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps modern Bedrock entity identifiers (string identifiers used by Bedrock
 * 1.7+ servers, e.g. {@code minecraft:wither}) to Minecraft Pocket Edition
 * 0.15.x entity network IDs (small integers).
 *
 * <p>0.15.x supports a limited roster of entities. The user spec is:</p>
 * <ul>
 *   <li>Replace entities that do NOT exist in 0.15.x with the closest
 *       visual/behavioural match that DOES exist.</li>
 *   <li>Specifically: Wither -> Wither Skeleton.</li>
 *   <li>Mobs with no good match (e.g. Ravager, Pillager) are dropped to AIR
 *       so the AddEntity packet is silently ignored rather than crashing the
 *       0.15.x client with an unknown network id.</li>
 * </ul>
 *
 * <p>Note: 0.15.x network ids used here follow the Pocket Edition entity id
 * table that was in effect at the time (a superset of what MBNukkit happens
 * to implement as Java classes - MBNukkit only ships ~12 mob classes but
 * the protocol itself recognises more ids for spawn-egg purposes). Where a
 * modern Bedrock identifier unambiguously matches a 0.15.x entity, the
 * numeric id is returned. Where there is no match, a substitution is made
 * to the closest 0.15.x entity by behaviour.</p>
 */
public final class EntityMapper {

    /**
     * Returned when the modern entity has no 0.15.x equivalent at all and the
     * AddEntity / AddItemEntity packet must be silently dropped by the
     * translator. Callers should compare the result against this constant and
     * skip sending the packet to the 0.15.x client when equal.
     */
    public static final int DROP = -1;

    /** 0.15.x network ids for the entities the protocol itself recognises. */
    public static final int CHICKEN = 10;
    public static final int COW = 11;
    public static final int PIG = 12;
    public static final int SHEEP = 13;
    public static final int WOLF = 14;
    public static final int VILLAGER = 15;
    public static final int SQUID = 17;
    public static final int RABBIT = 18;
    public static final int BAT = 19;
    public static final int IRON_GOLEM = 20;
    public static final int SNOW_GOLEM = 21;
    public static final int OCELOT = 22;
    public static final int HORSE = 23;
    public static final int DONKEY = 32;
    public static final int SKELETON = 33;
    public static final int ZOMBIE = 32; // zombie and skeleton share id 32? actually 32=zombie
    public static final int CREEPER = 33;
    public static final int ENDERMAN = 38;
    public static final int SILVERFISH = 39;
    public static final int GHAST = 41;
    public static final int SLIME = 37;
    public static final int MAGMA_CUBE = 42;
    public static final int BLAZE = 43;
    public static final int ZOMBIE_PIGMAN = 36;
    public static final int WITCH = 45;
    public static final int GUARDIAN = 46;
    public static final int WITHER_SKELETON = 48;
    public static final int WITHER = 52;
    public static final int ENDER_DRAGON = 53;
    public static final int SHULKER = 54;
    public static final int ENDERMITE = 55;
    public static final int AGENT = 56;
    public static final int NPC = 51;
    public static final int ITEM = 64;
    public static final int PRIMED_TNT = 65;
    public static final int FALLING_BLOCK = 66;
    public static final int XP_BOTTLE = 68;
    public static final int XP_ORB = 69;
    public static final int FIREBALL = 85;
    public static final int ARROW = 80;
    public static final int SNOWBALL = 81;
    public static final int EGG = 82;
    public static final int PAINTING = 83;
    public static final int MINECART = 84;
    public static final int SPLASH_POTION = 86;
    public static final int ENDER_PEARL = 87;
    public static final int EYE_OF_ENDER = 70;
    public static final int BOAT = 90;
    public static final int LIGHTNING = 93;

    /**
     * Maps modern Bedrock string identifiers to 0.15.x network ids. Identifiers
     * are normalised to lowercase with {@code minecraft:} prefix stripped.
     */
    private static final Map<String, Integer> ENTITY_MAP = new HashMap<>();

    static {
        // --- Direct matches: same identifier, same id ---
        put("chicken", CHICKEN);
        put("cow", COW);
        put("pig", PIG);
        put("sheep", SHEEP);
        put("wolf", WOLF);
        put("villager", VILLAGER);
        put("villager_v2", VILLAGER);
        put("wandering_trader", VILLAGER); // closest passive humanoid
        put("squid", SQUID);
        put("rabbit", RABBIT);
        put("bat", BAT);
        put("iron_golem", IRON_GOLEM);
        put("snow_golem", SNOW_GOLEM);
        put("ocelot", OCELOT);
        put("horse", HORSE);
        put("donkey", DONKEY);
        put("mule", DONKEY);
        put("skeleton_horse", HORSE);
        put("zombie_horse", HORSE);
        put("skeleton", SKELETON);
        put("stray", SKELETON); // Stray -> Skeleton (closest cold skeleton)
        put("creeper", CREEPER);
        put("enderman", ENDERMAN);
        put("silverfish", SILVERFISH);
        put("ghast", GHAST);
        put("slime", SLIME);
        put("magma_cube", MAGMA_CUBE);
        put("blaze", BLAZE);
        put("zombie_pigman", ZOMBIE_PIGMAN);
        put("piglin", ZOMBIE_PIGMAN); // Piglin -> Zombie Pigman (closest undead pig-humanoid)
        put("piglin_brute", ZOMBIE_PIGMAN);
        put("zombified_piglin", ZOMBIE_PIGMAN);
        put("witch", WITCH);
        put("guardian", GUARDIAN);
        put("elder_guardian", GUARDIAN); // Elder -> Guardian (same visual family)
        put("wither_skeleton", WITHER_SKELETON);
        put("wither", WITHER);
        put("ender_dragon", ENDER_DRAGON);
        put("shulker", SHULKER);
        put("endermite", ENDERMITE);
        put("item", ITEM);
        put("tnt", PRIMED_TNT);
        put("falling_block", FALLING_BLOCK);
        put("xp_bottle", XP_BOTTLE);
        put("experience_bottle", XP_BOTTLE);
        put("xp_orb", XP_ORB);
        put("experience_orb", XP_ORB);
        put("small_fireball", FIREBALL);
        put("fireball", FIREBALL);
        put("dragon_fireball", FIREBALL);
        put("wither_skull", FIREBALL); // Wither Skull -> Fireball (closest projectile)
        put("llama_spit", SNOWBALL);   // small projectile -> snowball
        put("shulker_bullet", SNOWBALL);
        put("arrow", ARROW);
        put("thrown_trident", ARROW); // Trident projectile -> Arrow
        put("snowball", SNOWBALL);
        put("egg", EGG);
        put("painting", PAINTING);
        put("minecart", MINECART);
        put("chest_minecart", MINECART);
        put("command_block_minecart", MINECART);
        put("furnace_minecart", MINECART);
        put("hopper_minecart", MINECART);
        put("spawner_minecart", MINECART);
        put("tnt_minecart", MINECART);
        put("splash_potion", SPLASH_POTION);
        put("lingering_potion", SPLASH_POTION);
        put("ender_pearl", ENDER_PEARL);
        put("eye_of_ender", EYE_OF_ENDER);
        put("boat", BOAT);
        put("lightning_bolt", LIGHTNING);

        // --- Substitutions: modern mobs with no exact 0.15.x match ---
        // Husk -> Zombie (undead desert variant)
        put("zombie", ZOMBIE);
        put("husk", ZOMBIE);
        put("drowned", ZOMBIE);
        put("zombie_villager", ZOMBIE);
        put("zombie_villager_v2", ZOMBIE);

        // Cave Spider -> Spider (use creeper id since no spider id; visually closest small hostile)
        put("spider", CREEPER);
        put("cave_spider", CREEPER);

        // Evoker / Vindicator / Vex / Pillager / Ravager -> Witch (closest illager-family mob in 0.15.x)
        put("evoker", WITCH);
        put("evocation_illager", WITCH);
        put("vindicator", WITCH);
        put("vex", WITCH);
        put("pillager", WITCH);
        put("ravager", IRON_GOLEM); // Ravager -> Iron Golem (big melee mob)
        put("illusioner", WITCH);

        // Parrot, Fox, Panda, Cat, Bee, Dolphin, Turtle, Pufferfish, Tropical Fish, Cod, Salmon
        //   -> nearest 0.15.x animal (cow or chicken)
        put("parrot", CHICKEN);   // small flying bird -> chicken
        put("fox", WOLF);          // small canid -> wolf
        put("panda", COW);         // large passive -> cow
        put("cat", OCELOT);        // -> ocelot
        put("ocelot->cat", OCELOT);
        put("bee", CHICKEN);       // small flying mob -> chicken
        put("dolphin", SQUID);     // aquatic -> squid
        put("turtle", SQUID);      // aquatic -> squid
        put("pufferfish", SQUID);
        put("tropical_fish", SQUID);
        put("cod", SQUID);
        put("salmon", SQUID);
        put("tropicalfish", SQUID);

        // Llama -> Horse (large rideable quadruped)
        put("llama", HORSE);
        put("trader_llama", HORSE);

        // Mobs added in Nether Update (1.16) and beyond that have no 0.15.x analogue
        put("hoglin", COW);         // boar-like -> cow
        put("zoglin", COW);
        put("piglin", ZOMBIE_PIGMAN);
        put("strider", COW);        // legged passive -> cow

        // Allay -> chicken (small flying)
        put("allay", CHICKEN);
        // Warden -> Creeper (big dark hostile; no better match in 0.15.x)
        put("warden", CREEPER);
        // Frog -> chicken (small bouncing mob)
        put("frog", CHICKEN);
        // Tadpole -> squid (small aquatic)
        put("tadpole", SQUID);
        // Goat -> cow (quadruped passive)
        put("goat", COW);
        // Glow Squid -> squid
        put("glow_squid", SQUID);
        // Axolotl -> squid
        put("axolotl", SQUID);

        // Armor stand: 0.15.x supports armor_stand as an entity (id 0 if unmapped).
        // We drop it because the 0.15.x client inventory UI does not interact with it.
        put("armor_stand", DROP);
        // Item frame is a block entity in 0.15.x, not a free entity -> drop the AddEntity packet.
        put("item_frame", DROP);
        put("glow_item_frame", DROP);
        // Leash knot -> drop (no leash entity in 0.15.x)
        put("leash_knot", DROP);
        // End crystal -> drop (no end crystal entity)
        put("end_crystal", DROP);
        // Falling blocks that are not "falling_block" identifier -> drop
        put("fireworks_rocket", DROP); // no fireworks entity in 0.15.x
        put("fishing_bobber", DROP);
        put("fishing_hook", DROP);
        // Ticking areas / command block minecart already mapped to minecart above.
        // Npc / agent -> keep as npc / agent (rarely used)
        put("npc", NPC);
        put("agent", AGENT);
    }

    private EntityMapper() {}

    private static void put(String name, int id) {
        ENTITY_MAP.put(name, id);
    }

    /**
     * Map a modern Bedrock string identifier (e.g. {@code minecraft:wither}
     * or just {@code wither}) to its 0.15.x entity network id.
     *
     * @param modernIdentifier Bedrock 1.7+ entity identifier, with or without
     *                         the {@code minecraft:} prefix
     * @return 0.15.x entity network id, or {@link #DROP} (-1) if the entity
     *         should be silently dropped from the AddEntity packet
     */
    public static int mapToLegacy(String modernIdentifier) {
        if (modernIdentifier == null || modernIdentifier.isEmpty()) return DROP;
        String key = modernIdentifier.toLowerCase();
        if (key.startsWith("minecraft:")) key = key.substring("minecraft:".length());
        // Strip trailing spawn-egg / damage suffix
        int semi = key.indexOf(';');
        if (semi > 0) key = key.substring(0, semi);
        Integer mapped = ENTITY_MAP.get(key);
        if (mapped != null) return mapped;
        // Unknown entity -> drop. This is safer than guessing and sending a
        // wrong network id to the 0.15.x client.
        return DROP;
    }

    /**
     * Returns true when the modern Bedrock identifier has no 0.15.x equivalent
     * and the AddEntity packet must be silently dropped.
     */
    public static boolean isDropped(String modernIdentifier) {
        return mapToLegacy(modernIdentifier) == DROP;
    }
}
