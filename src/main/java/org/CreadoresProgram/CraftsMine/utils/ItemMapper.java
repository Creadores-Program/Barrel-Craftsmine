package org.CreadoresProgram.CraftsMine.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps modern Bedrock item identifiers (1.7+ numeric IDs or string identifiers)
 * to Minecraft Pocket Edition 0.15.x item IDs (numeric 256..466).
 *
 * <p>Strategy follows the user spec:</p>
 * <ul>
 *   <li>Items that exist with the same ID in both versions are passed through.</li>
 *   <li>Items that were renamed but still exist (e.g. {@code clownfish} ->
 *       {@code tropical_fish}) are remapped to their 0.15.x ID.</li>
 *   <li>Items that do NOT exist in 0.15.x (Elytra, Shulker Shell, Trident,
 *       Heart of the Sea, Nautilus Shell, Turtle Shell, Phantom Membrane,
 *       Totem of Undying, Dragon's Breath, Lingering Potion, End Crystal,
 *       Shield, Debug Stick, Bee related items, Netherite gear, etc.) are
 *       mapped to {@link #AIR} so they are silently dropped from inventory
 *       transactions, creative menu and slot updates - the player simply
 *       won't see them rather than crashing the 0.15.x client.</li>
 *   <li>For items with a visually similar 0.15.x counterpart (e.g. modern
 *       {@code turtle_helmet} -> {@code iron_helmet}, {@code phantom_membrane}
 *       -> {@code leather}) a substitution is made so the slot is not empty.</li>
 * </ul>
 *
 * <p>The mapping table is intentionally static and readable so it can be
 * iteratively tuned against real 0.15.x client tests. Callers should always
 * go through {@link #mapToLegacy(int)} and never read {@link #ITEM_MAP}
 * directly.</p>
 */
public final class ItemMapper {

    /** Sentinel returned when the source item does not exist in 0.15.x. */
    public static final int AIR = 0;
    /** Sentinel returned when a mapping is ambiguous or the input is invalid. */
    public static final int UNKNOWN = -1;

    /**
     * Modern Bedrock numeric item id -> 0.15.x numeric item id.
     * Bedrock 1.7-1.10 era uses largely the same numeric space as 0.15.x for
     * vanilla items; the entries below cover the divergent and removed items.
     */
    private static final Map<Integer, Integer> ITEM_MAP = new HashMap<>();
    /**
     * Modern Bedrock string identifier -> 0.15.x numeric item id. Used when
     * the translator has a string identifier (creative content, item NBT)
     * instead of a numeric runtime id.
     */
    private static final Map<String, Integer> STRING_MAP = new HashMap<>();

    static {
        // ---- Items removed in modern versions: drop them entirely ----
        // CAMERA (456) only exists in old MCPE; modern Bedrock dropped it.
        ITEM_MAP.put(456, AIR); // camera
        STRING_MAP.put("minecraft:camera", AIR);

        // ---- Items added in modern versions: drop or substitute ----
        // Elytra -> air (no flight slot in 0.15.x inventory UI)
        STRING_MAP.put("minecraft:elytra", AIR);
        // Shulker shells / shulker boxes -> air
        STRING_MAP.put("minecraft:shulker_shell", AIR);
        STRING_MAP.put("minecraft:shulker_box", AIR);
        for (int c = 0; c < 16; c++) {
            STRING_MAP.put("minecraft:shulker_box;" + c, AIR);
        }
        // Trident -> air (no trident item type in 0.15.x)
        STRING_MAP.put("minecraft:trident", AIR);
        // Totem of Undying -> air
        STRING_MAP.put("minecraft:totem_of_undying", AIR);
        // Dragon's Breath -> air
        STRING_MAP.put("minecraft:dragon_breath", AIR);
        // Lingering Potion -> regular splash potion (373) so the slot is not empty
        STRING_MAP.put("minecraft:lingering_potion", 373);
        // End Crystal -> air (no end crystal item in 0.15.x)
        STRING_MAP.put("minecraft:end_crystal", AIR);
        // Shield -> air (no off-hand slot in 0.15.x)
        STRING_MAP.put("minecraft:shield", AIR);
        // Heart of the Sea -> air
        STRING_MAP.put("minecraft:heart_of_the_sea", AIR);
        // Nautilus Shell -> air
        STRING_MAP.put("minecraft:nautilus_shell", AIR);
        // Phantom Membrane -> leather (334) visually similar material
        STRING_MAP.put("minecraft:phantom_membrane", 334);
        // Turtle Shell (helmet) -> iron_helmet (306) - closest helmet
        STRING_MAP.put("minecraft:turtle_helmet", 306);
        // Turtle Egg -> air
        STRING_MAP.put("minecraft:turtle_egg", AIR);
        // Scute -> leather (334)
        STRING_MAP.put("minecraft:scute", 334);
        // Beehive / Bee_nest / Honeycomb / Honey_bottle -> air (added in 1.14)
        STRING_MAP.put("minecraft:beehive", AIR);
        STRING_MAP.put("minecraft:bee_nest", AIR);
        STRING_MAP.put("minecraft:honeycomb", AIR);
        STRING_MAP.put("minecraft:honey_bottle", AIR);
        // Conduit -> air
        STRING_MAP.put("minecraft:conduit", AIR);
        // Kelp -> air
        STRING_MAP.put("minecraft:kelp", AIR);
        // Seagrass -> air
        STRING_MAP.put("minecraft:seagrass", AIR);
        // Dried Kelp Block -> air
        STRING_MAP.put("minecraft:dried_kelp_block", AIR);
        // Scaffolding -> air
        STRING_MAP.put("minecraft:scaffolding", AIR);
        // Bamboo -> stick (280) visually closest
        STRING_MAP.put("minecraft: bamboo", 280);
        STRING_MAP.put("minecraft:bamboo", 280);
        // Crossbow -> bow (261)
        STRING_MAP.put("minecraft:crossbow", 261);
        // Berries (sweet_berries) -> apple (260)
        STRING_MAP.put("minecraft:sweet_berries", 260);
        // Cornflower, Lily of the Valley, Wither Rose, all new 1.14+ flowers -> dandelion (37)
        STRING_MAP.put("minecraft:cornflower", 37);
        STRING_MAP.put("minecraft:lily_of_the_valley", 37);
        STRING_MAP.put("minecraft:wither_rose", 37);
        STRING_MAP.put("minecraft:sunflower", 37);
        STRING_MAP.put("minecraft:peony", 37);
        STRING_MAP.put("minecraft:rose_bush", 37);
        STRING_MAP.put("minecraft:lilac", 37);
        // Netherite gear (1.16+) -> diamond equivalent
        STRING_MAP.put("minecraft:netherite_ingot", 264); // diamond
        STRING_MAP.put("minecraft:netherite_scrap", 264);
        STRING_MAP.put("minecraft:netherite_sword", 276); // diamond_sword
        STRING_MAP.put("minecraft:netherite_pickaxe", 278);
        STRING_MAP.put("minecraft:netherite_axe", 279);
        STRING_MAP.put("minecraft:netherite_shovel", 277);
        STRING_MAP.put("minecraft:netherite_hoe", 293);
        STRING_MAP.put("minecraft:netherite_helmet", 310);
        STRING_MAP.put("minecraft:netherite_chestplate", 311);
        STRING_MAP.put("minecraft:netherite_leggings", 312);
        STRING_MAP.put("minecraft:netherite_boots", 313);
        STRING_MAP.put("minecraft:ancient_debris", 49); // obsidian closest
        STRING_MAP.put("minecraft:netherite_block", 49);
        STRING_MAP.put("minecraft:basalt", 1); // stone
        STRING_MAP.put("minecraft:blackstone", 49); // obsidian
        STRING_MAP.put("minecraft:gilded_blackstone", 14); // gold_ore
        STRING_MAP.put("minecraft:warped_nylium", 88); // mycelium
        STRING_MAP.put("minecraft:crimson_nylium", 88);
        STRING_MAP.put("minecraft:warped_wart_block", 110); // mycelium
        STRING_MAP.put("minecraft:shroomlight", 89); // glowstone
        STRING_MAP.put("minecraft:warped_stem", 17); // log
        STRING_MAP.put("minecraft:crimson_stem", 17);
        STRING_MAP.put("minecraft:target", 89); // glowstone-like
        STRING_MAP.put("minecraft:soul_soil", 88); // soul_sand closest
        STRING_MAP.put("minecraft:soul_campfire", 61); // furnace
        STRING_MAP.put("minecraft:soul_torch", 50);
        STRING_MAP.put("minecraft:soul_lantern", 91); // glowstone alt
        STRING_MAP.put("minecraft:chain", 41); // iron_block
        STRING_MAP.put("minecraft:polished_blackstone", 1);
        STRING_MAP.put("minecraft:crying_obsidian", 49);
        STRING_MAP.put("minecraft:respawn_anchor", 49); // obsidian
        STRING_MAP.put("minecraft:nether_gold_ore", 14);
        STRING_MAP.put("minecraft:nether_sprouts", 31); // tallgrass
        STRING_MAP.put("minecraft:twisting_vines", 31);
        STRING_MAP.put("minecraft:weeping_vines", 31);
        STRING_MAP.put("minecraft:lodestone", 41); // iron_block
        STRING_MAP.put("minecraft:honey_block", 41); // iron_block-ish
        STRING_MAP.put("minecraft:honeycomb_block", 41);

        // ---- Renamed items: remap to 0.15.x id ----
        // Clownfish -> Tropical fish -> raw_fish (349)
        STRING_MAP.put("minecraft:tropical_fish", 349);
        STRING_MAP.put("minecraft:clownfish", 349);
        // Pufferfish -> raw_fish (349) with different damage value
        STRING_MAP.put("minecraft:pufferfish", 349);
        // Raw/Cooked salmon already exist in 0.15.x at id 460/463 but modern
        // Bedrock moved them into the generic fish stack. Map to 0.15.x raw_fish.
        STRING_MAP.put("minecraft:salmon", 460);
        STRING_MAP.put("minecraft:cooked_salmon", 463);

        // ---- Spawn eggs: maps via metadata in 0.15.x; modern string ids are dropped to air if entity unmapped ----
        // (Spawn eggs all use id 383 in 0.15.x with metadata = entity network id.)
        // The EntityMapper handles which entity network id to use.
    }

    private ItemMapper() {}

    /**
     * Map a modern Bedrock numeric item id to its 0.15.x numeric equivalent.
     * If the id is unchanged between versions, the same value is returned.
     * If the item does not exist in 0.15.x, {@link #AIR} (0) is returned so
     * the caller can drop the item from the inventory transaction.
     *
     * @param modernId modern Bedrock item id
     * @return 0.15.x item id, or 0 if the item should be dropped
     */
    public static int mapToLegacy(int modernId) {
        Integer mapped = ITEM_MAP.get(modernId);
        if (mapped != null) return mapped;
        // Pass-through for items whose numeric id is shared between versions.
        // 0.15.x item ids live in [256..466] plus block-ish items in [0..255]
        // that double as block items. Anything outside that range is almost
        // certainly a modern id and should be dropped.
        if (modernId >= 0 && modernId <= 466) return modernId;
        return AIR;
    }

    /**
     * Map a modern Bedrock string identifier (e.g. {@code minecraft:trident})
     * to its 0.15.x numeric item id. Unknown identifiers fall back to AIR
     * so the slot is silently dropped instead of crashing the 0.15.x client.
     *
     * @param modernIdentifier modern Bedrock string identifier, with or without
     *                         the {@code minecraft:} prefix
     * @return 0.15.x item id, or 0 if the item should be dropped
     */
    public static int mapToLegacy(String modernIdentifier) {
        if (modernIdentifier == null || modernIdentifier.isEmpty()) return AIR;
        String key = modernIdentifier.startsWith("minecraft:")
            ? modernIdentifier
            : "minecraft:" + modernIdentifier;
        // Strip damage/data suffix like "minecraft:wool;6"
        int semi = key.indexOf(';');
        if (semi > 0) key = key.substring(0, semi);
        Integer mapped = STRING_MAP.get(key);
        if (mapped != null) return mapped;
        // Unknown identifier -> drop
        return AIR;
    }

    /**
     * Returns true when the modern Bedrock numeric item id has no 0.15.x
     * equivalent and should be silently dropped from inventory operations.
     * Equivalent to {@code mapToLegacy(modernId) == AIR}.
     */
    public static boolean isDropped(int modernId) {
        return mapToLegacy(modernId) == AIR;
    }

    /**
     * Returns true when the modern Bedrock string identifier has no 0.15.x
     * equivalent and should be silently dropped from inventory operations.
     */
    public static boolean isDropped(String modernIdentifier) {
        return mapToLegacy(modernIdentifier) == AIR;
    }
}
