package org.CreadoresProgram.CraftsMine.utils;

import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtType;
import org.CreadoresProgram.CraftsMine.server.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps modern Bedrock runtime block IDs to legacy MCPE 0.15.10 (id, metadata) pairs.
 * Built from the block palette received in StartGamePacket.
 */
public class BlockMapper {

    private final int[] legacyIds;
    private final byte[] legacyMeta;

    public BlockMapper(List<NbtMap> blockPalette) {
        int size = blockPalette != null ? blockPalette.size() : 0;
        this.legacyIds = new int[size];
        this.legacyMeta = new byte[size];
        for (int i = 0; i < size; i++) {
            NbtMap entry = blockPalette.get(i);
            String name = getNbtString(entry, "name");
            NbtMap states = getNbtCompound(entry, "states");
            int[] lm = mapBlock(name, states);
            this.legacyIds[i] = lm[0];
            this.legacyMeta[i] = (byte) lm[1];
        }
        // Log first few entries for diagnostics
        if (Server.getInstance() != null && Server.getInstance().getConfig() != null
                && Server.getInstance().getConfig().isDebug() && size > 0) {
            StringBuilder sb = new StringBuilder();
            int showCnt = Math.min(size, 10);
            for (int i = 0; i < showCnt; i++) {
                NbtMap entry = blockPalette.get(i);
                String name = getNbtString(entry, "name");
                sb.append("[").append(i).append("]=").append(name)
                  .append("->id=").append(legacyIds[i])
                  .append(":").append(legacyMeta[i]).append(" ");
            }
            Server.getInstance().getLogger().debug(
                "[BlockMapper] Created size=" + size + " first entries: " + sb);
        }
    }

    public int getLegacyId(int runtimeId) {
        if (runtimeId < 0 || runtimeId >= legacyIds.length) return 0;
        return legacyIds[runtimeId];
    }

    public byte getLegacyMeta(int runtimeId) {
        if (runtimeId < 0 || runtimeId >= legacyMeta.length) return 0;
        return legacyMeta[runtimeId];
    }

    public int size() {
        return legacyIds.length;
    }

    private static final Map<String, int[]> NAME_MAP = new HashMap<>();

    static {
        // [legacyId, defaultMetadata]
        NAME_MAP.put("minecraft:air", new int[]{0, 0});
        NAME_MAP.put("minecraft:stone", new int[]{1, 0});
        NAME_MAP.put("minecraft:grass", new int[]{2, 0});
        NAME_MAP.put("minecraft:dirt", new int[]{3, 0});
        NAME_MAP.put("minecraft:cobblestone", new int[]{4, 0});
        NAME_MAP.put("minecraft:planks", new int[]{5, 0});
        NAME_MAP.put("minecraft:sapling", new int[]{6, 0});
        NAME_MAP.put("minecraft:bedrock", new int[]{7, 0});
        NAME_MAP.put("minecraft:flowing_water", new int[]{8, 0});
        NAME_MAP.put("minecraft:water", new int[]{9, 0});
        NAME_MAP.put("minecraft:flowing_lava", new int[]{10, 0});
        NAME_MAP.put("minecraft:lava", new int[]{11, 0});
        NAME_MAP.put("minecraft:sand", new int[]{12, 0});
        NAME_MAP.put("minecraft:gravel", new int[]{13, 0});
        NAME_MAP.put("minecraft:gold_ore", new int[]{14, 0});
        NAME_MAP.put("minecraft:iron_ore", new int[]{15, 0});
        NAME_MAP.put("minecraft:coal_ore", new int[]{16, 0});
        NAME_MAP.put("minecraft:log", new int[]{17, 0});
        NAME_MAP.put("minecraft:leaves", new int[]{18, 0});
        NAME_MAP.put("minecraft:sponge", new int[]{19, 0});
        NAME_MAP.put("minecraft:glass", new int[]{20, 0});
        NAME_MAP.put("minecraft:lapis_ore", new int[]{21, 0});
        NAME_MAP.put("minecraft:lapis_block", new int[]{22, 0});
        NAME_MAP.put("minecraft:dispenser", new int[]{23, 0});
        NAME_MAP.put("minecraft:sandstone", new int[]{24, 0});
        NAME_MAP.put("minecraft:noteblock", new int[]{25, 0});
        NAME_MAP.put("minecraft:bed", new int[]{26, 0});
        NAME_MAP.put("minecraft:golden_rail", new int[]{27, 0});
        NAME_MAP.put("minecraft:detector_rail", new int[]{28, 0});
        NAME_MAP.put("minecraft:sticky_piston", new int[]{29, 0});
        NAME_MAP.put("minecraft:web", new int[]{30, 0});
        NAME_MAP.put("minecraft:tallgrass", new int[]{31, 1});
        NAME_MAP.put("minecraft:deadbush", new int[]{32, 0});
        NAME_MAP.put("minecraft:piston", new int[]{33, 0});
        NAME_MAP.put("minecraft:piston_arm_collision", new int[]{34, 0});
        NAME_MAP.put("minecraft:wool", new int[]{35, 0});
        NAME_MAP.put("minecraft:element_0", new int[]{36, 0});
        NAME_MAP.put("minecraft:yellow_flower", new int[]{37, 0});
        NAME_MAP.put("minecraft:red_flower", new int[]{38, 0});
        NAME_MAP.put("minecraft:brown_mushroom", new int[]{39, 0});
        NAME_MAP.put("minecraft:red_mushroom", new int[]{40, 0});
        NAME_MAP.put("minecraft:gold_block", new int[]{41, 0});
        NAME_MAP.put("minecraft:iron_block", new int[]{42, 0});
        NAME_MAP.put("minecraft:double_stone_slab", new int[]{43, 0});
        NAME_MAP.put("minecraft:stone_slab", new int[]{44, 0});
        NAME_MAP.put("minecraft:brick_block", new int[]{45, 0});
        NAME_MAP.put("minecraft:tnt", new int[]{46, 0});
        NAME_MAP.put("minecraft:bookshelf", new int[]{47, 0});
        NAME_MAP.put("minecraft:mossy_cobblestone", new int[]{48, 0});
        NAME_MAP.put("minecraft:obsidian", new int[]{49, 0});
        NAME_MAP.put("minecraft:torch", new int[]{50, 0});
        NAME_MAP.put("minecraft:fire", new int[]{51, 0});
        NAME_MAP.put("minecraft:mob_spawner", new int[]{52, 0});
        NAME_MAP.put("minecraft:oak_stairs", new int[]{53, 0});
        NAME_MAP.put("minecraft:chest", new int[]{54, 0});
        NAME_MAP.put("minecraft:redstone_wire", new int[]{55, 0});
        NAME_MAP.put("minecraft:diamond_ore", new int[]{56, 0});
        NAME_MAP.put("minecraft:diamond_block", new int[]{57, 0});
        NAME_MAP.put("minecraft:crafting_table", new int[]{58, 0});
        NAME_MAP.put("minecraft:wheat", new int[]{59, 0});
        NAME_MAP.put("minecraft:farmland", new int[]{60, 0});
        NAME_MAP.put("minecraft:furnace", new int[]{61, 0});
        NAME_MAP.put("minecraft:lit_furnace", new int[]{62, 0});
        NAME_MAP.put("minecraft:standing_sign", new int[]{63, 0});
        NAME_MAP.put("minecraft:wooden_door", new int[]{64, 0});
        NAME_MAP.put("minecraft:ladder", new int[]{65, 0});
        NAME_MAP.put("minecraft:rail", new int[]{66, 0});
        NAME_MAP.put("minecraft:stone_stairs", new int[]{67, 0});
        NAME_MAP.put("minecraft:wall_sign", new int[]{68, 0});
        NAME_MAP.put("minecraft:lever", new int[]{69, 0});
        NAME_MAP.put("minecraft:stone_pressure_plate", new int[]{70, 0});
        NAME_MAP.put("minecraft:iron_door", new int[]{71, 0});
        NAME_MAP.put("minecraft:wooden_pressure_plate", new int[]{72, 0});
        NAME_MAP.put("minecraft:redstone_ore", new int[]{73, 0});
        NAME_MAP.put("minecraft:lit_redstone_ore", new int[]{74, 0});
        NAME_MAP.put("minecraft:unlit_redstone_torch", new int[]{75, 0});
        NAME_MAP.put("minecraft:redstone_torch", new int[]{76, 0});
        NAME_MAP.put("minecraft:stone_button", new int[]{77, 0});
        NAME_MAP.put("minecraft:snow_layer", new int[]{78, 0});
        NAME_MAP.put("minecraft:ice", new int[]{79, 0});
        NAME_MAP.put("minecraft:snow", new int[]{80, 0});
        NAME_MAP.put("minecraft:cactus", new int[]{81, 0});
        NAME_MAP.put("minecraft:clay", new int[]{82, 0});
        NAME_MAP.put("minecraft:reeds", new int[]{83, 0});
        NAME_MAP.put("minecraft:jukebox", new int[]{84, 0});
        NAME_MAP.put("minecraft:fence", new int[]{85, 0});
        NAME_MAP.put("minecraft:pumpkin", new int[]{86, 0});
        NAME_MAP.put("minecraft:netherrack", new int[]{87, 0});
        NAME_MAP.put("minecraft:soul_sand", new int[]{88, 0});
        NAME_MAP.put("minecraft:glowstone", new int[]{89, 0});
        NAME_MAP.put("minecraft:portal", new int[]{90, 0});
        NAME_MAP.put("minecraft:lit_pumpkin", new int[]{91, 0});
        NAME_MAP.put("minecraft:cake", new int[]{92, 0});
        NAME_MAP.put("minecraft:unpowered_repeater", new int[]{93, 0});
        NAME_MAP.put("minecraft:powered_repeater", new int[]{94, 0});
        NAME_MAP.put("minecraft:invisiblebedrock", new int[]{95, 0});
        NAME_MAP.put("minecraft:trapdoor", new int[]{96, 0});
        NAME_MAP.put("minecraft:monster_egg", new int[]{97, 0});
        NAME_MAP.put("minecraft:stonebrick", new int[]{98, 0});
        NAME_MAP.put("minecraft:brown_mushroom_block", new int[]{99, 0});
        NAME_MAP.put("minecraft:red_mushroom_block", new int[]{100, 0});
        NAME_MAP.put("minecraft:iron_bars", new int[]{101, 0});
        NAME_MAP.put("minecraft:glass_pane", new int[]{102, 0});
        NAME_MAP.put("minecraft:melon_block", new int[]{103, 0});
        NAME_MAP.put("minecraft:pumpkin_stem", new int[]{104, 0});
        NAME_MAP.put("minecraft:melon_stem", new int[]{105, 0});
        NAME_MAP.put("minecraft:vine", new int[]{106, 0});
        NAME_MAP.put("minecraft:fence_gate", new int[]{107, 0});
        NAME_MAP.put("minecraft:brick_stairs", new int[]{108, 0});
        NAME_MAP.put("minecraft:stone_brick_stairs", new int[]{109, 0});
        NAME_MAP.put("minecraft:mycelium", new int[]{110, 0});
        NAME_MAP.put("minecraft:waterlily", new int[]{111, 0});
        NAME_MAP.put("minecraft:nether_brick", new int[]{112, 0});
        NAME_MAP.put("minecraft:nether_brick_fence", new int[]{113, 0});
        NAME_MAP.put("minecraft:nether_brick_stairs", new int[]{114, 0});
        NAME_MAP.put("minecraft:nether_wart", new int[]{115, 0});
        NAME_MAP.put("minecraft:enchanting_table", new int[]{116, 0});
        NAME_MAP.put("minecraft:brewing_stand", new int[]{117, 0});
        NAME_MAP.put("minecraft:cauldron", new int[]{118, 0});
        NAME_MAP.put("minecraft:end_portal", new int[]{119, 0});
        NAME_MAP.put("minecraft:end_portal_frame", new int[]{120, 0});
        NAME_MAP.put("minecraft:end_stone", new int[]{121, 0});
        NAME_MAP.put("minecraft:dragon_egg", new int[]{122, 0});
        NAME_MAP.put("minecraft:redstone_lamp", new int[]{123, 0});
        NAME_MAP.put("minecraft:lit_redstone_lamp", new int[]{124, 0});
        NAME_MAP.put("minecraft:dropper", new int[]{125, 0});
        NAME_MAP.put("minecraft:activator_rail", new int[]{126, 0});
        NAME_MAP.put("minecraft:cocoa", new int[]{127, 0});
        NAME_MAP.put("minecraft:sandstone_stairs", new int[]{128, 0});
        NAME_MAP.put("minecraft:emerald_ore", new int[]{129, 0});
        NAME_MAP.put("minecraft:ender_chest", new int[]{130, 0});
        NAME_MAP.put("minecraft:tripwire_hook", new int[]{131, 0});
        NAME_MAP.put("minecraft:tripwire", new int[]{132, 0});
        NAME_MAP.put("minecraft:emerald_block", new int[]{133, 0});
        NAME_MAP.put("minecraft:spruce_stairs", new int[]{134, 0});
        NAME_MAP.put("minecraft:birch_stairs", new int[]{135, 0});
        NAME_MAP.put("minecraft:jungle_stairs", new int[]{136, 0});
        NAME_MAP.put("minecraft:command_block", new int[]{137, 0});
        NAME_MAP.put("minecraft:beacon", new int[]{138, 0});
        NAME_MAP.put("minecraft:cobblestone_wall", new int[]{139, 0});
        NAME_MAP.put("minecraft:flower_pot", new int[]{140, 0});
        NAME_MAP.put("minecraft:carrots", new int[]{141, 0});
        NAME_MAP.put("minecraft:potatoes", new int[]{142, 0});
        NAME_MAP.put("minecraft:wooden_button", new int[]{143, 0});
        NAME_MAP.put("minecraft:skull", new int[]{144, 0});
        NAME_MAP.put("minecraft:anvil", new int[]{145, 0});
        NAME_MAP.put("minecraft:trapped_chest", new int[]{146, 0});
        NAME_MAP.put("minecraft:light_weighted_pressure_plate", new int[]{147, 0});
        NAME_MAP.put("minecraft:heavy_weighted_pressure_plate", new int[]{148, 0});
        NAME_MAP.put("minecraft:unpowered_comparator", new int[]{149, 0});
        NAME_MAP.put("minecraft:powered_comparator", new int[]{150, 0});
        NAME_MAP.put("minecraft:daylight_detector", new int[]{151, 0});
        NAME_MAP.put("minecraft:redstone_block", new int[]{152, 0});
        NAME_MAP.put("minecraft:quartz_ore", new int[]{153, 0});
        NAME_MAP.put("minecraft:hopper", new int[]{154, 0});
        NAME_MAP.put("minecraft:quartz_block", new int[]{155, 0});
        NAME_MAP.put("minecraft:quartz_stairs", new int[]{156, 0});
        NAME_MAP.put("minecraft:activator_rail", new int[]{126, 0});
        NAME_MAP.put("minecraft:double_wooden_slab", new int[]{157, 0});
        NAME_MAP.put("minecraft:wooden_slab", new int[]{158, 0});
        NAME_MAP.put("minecraft:terracotta", new int[]{159, 0});
        NAME_MAP.put("minecraft:stained_hardened_clay", new int[]{159, 0});
        NAME_MAP.put("minecraft:stained_glass_pane", new int[]{160, 0});
        NAME_MAP.put("minecraft:leaves2", new int[]{161, 0});
        NAME_MAP.put("minecraft:log2", new int[]{162, 0});
        NAME_MAP.put("minecraft:acacia_stairs", new int[]{163, 0});
        NAME_MAP.put("minecraft:dark_oak_stairs", new int[]{164, 0});
        NAME_MAP.put("minecraft:slime", new int[]{165, 0});
        NAME_MAP.put("minecraft:glow_stick", new int[]{166, 0});
        NAME_MAP.put("minecraft:iron_trapdoor", new int[]{167, 0});
        NAME_MAP.put("minecraft:prismarine", new int[]{168, 0});
        NAME_MAP.put("minecraft:seagrass", new int[]{169, 0}); // approximate
        NAME_MAP.put("minecraft:coral", new int[]{170, 0});
        NAME_MAP.put("minecraft:coral_block", new int[]{171, 0});
        NAME_MAP.put("minecraft:coral_fan", new int[]{172, 0});
        NAME_MAP.put("minecraft:coral_fan_dead", new int[]{173, 0});
        NAME_MAP.put("minecraft:coral_fan_hang", new int[]{174, 0});
        NAME_MAP.put("minecraft:coral_fan_hang2", new int[]{175, 0});
        NAME_MAP.put("minecraft:coral_fan_hang3", new int[]{176, 0});
        NAME_MAP.put("minecraft:kelp", new int[]{177, 0});
        NAME_MAP.put("minecraft:dried_kelp_block", new int[]{178, 0});
        NAME_MAP.put("minecraft:carved_pumpkin", new int[]{179, 0});
        NAME_MAP.put("minecraft:sea_pickle", new int[]{180, 0});
        NAME_MAP.put("minecraft:concrete", new int[]{181, 0});
        NAME_MAP.put("minecraft:concrete_powder", new int[]{182, 0});
        NAME_MAP.put("minecraft:chemistry_table", new int[]{238, 0});
        NAME_MAP.put("minecraft:underwater_torch", new int[]{239, 0});
        NAME_MAP.put("minecraft:chorus_plant", new int[]{240, 0});
        NAME_MAP.put("minecraft:chorus_flower", new int[]{241, 0});
        NAME_MAP.put("minecraft:stained_glass", new int[]{241, 0}); // fallback overlap
        NAME_MAP.put("minecraft:beetroot", new int[]{244, 0});
        NAME_MAP.put("minecraft:stonecutter", new int[]{245, 0});
        NAME_MAP.put("minecraft:glowing_obsidian", new int[]{246, 0});
        NAME_MAP.put("minecraft:netherreactor", new int[]{247, 0});
        NAME_MAP.put("minecraft:update_block", new int[]{248, 0});
        NAME_MAP.put("minecraft:info_update", new int[]{248, 0});
        NAME_MAP.put("minecraft:info_update2", new int[]{249, 0});
        NAME_MAP.put("minecraft:moving_block", new int[]{250, 0});
        NAME_MAP.put("minecraft:observer", new int[]{251, 0});
        NAME_MAP.put("minecraft:structure_block", new int[]{252, 0});
        // Additional common blocks
        NAME_MAP.put("minecraft:granite", new int[]{1, 1});
        NAME_MAP.put("minecraft:polished_granite", new int[]{1, 2});
        NAME_MAP.put("minecraft:diorite", new int[]{1, 3});
        NAME_MAP.put("minecraft:polished_diorite", new int[]{1, 4});
        NAME_MAP.put("minecraft:andesite", new int[]{1, 5});
        NAME_MAP.put("minecraft:polished_andesite", new int[]{1, 6});
        NAME_MAP.put("minecraft:coarse_dirt", new int[]{3, 1});
        NAME_MAP.put("minecraft:podzol", new int[]{3, 2});
        NAME_MAP.put("minecraft:smooth_sandstone", new int[]{24, 2});
        NAME_MAP.put("minecraft:chiseled_sandstone", new int[]{24, 1});
        NAME_MAP.put("minecraft:red_sandstone", new int[]{179, 0});
        NAME_MAP.put("minecraft:smooth_red_sandstone", new int[]{179, 2});
        NAME_MAP.put("minecraft:chiseled_red_sandstone", new int[]{179, 1});
        NAME_MAP.put("minecraft:red_sandstone_stairs", new int[]{180, 0});
        NAME_MAP.put("minecraft:mossy_stone_brick", new int[]{98, 1});
        NAME_MAP.put("minecraft:cracked_stone_brick", new int[]{98, 2});
        NAME_MAP.put("minecraft:chiseled_stone_brick", new int[]{98, 3});
        NAME_MAP.put("minecraft:smooth_stone", new int[]{43, 6}); // double slab smooth stone
        NAME_MAP.put("minecraft:nether_gold_ore", new int[]{14, 0}); // approximate
        NAME_MAP.put("minecraft:ancient_debris", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:basalt", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:polished_basalt", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:blackstone", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:obsidian", new int[]{49, 0});
        NAME_MAP.put("minecraft:crying_obsidian", new int[]{49, 0}); // approximate
        NAME_MAP.put("minecraft:glowstone", new int[]{89, 0});
        NAME_MAP.put("minecraft:shroomlight", new int[]{89, 0}); // approximate
        NAME_MAP.put("minecraft:snow", new int[]{80, 0});
        NAME_MAP.put("minecraft:snow_layer", new int[]{78, 0});
        NAME_MAP.put("minecraft:packed_ice", new int[]{174, 0});
        NAME_MAP.put("minecraft:blue_ice", new int[]{174, 0}); // approximate
        NAME_MAP.put("minecraft:frosted_ice", new int[]{79, 0}); // approximate
        NAME_MAP.put("minecraft:scaffolding", new int[]{165, 0}); // approximate
        NAME_MAP.put("minecraft:grindstone", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:blast_furnace", new int[]{61, 0}); // approximate
        NAME_MAP.put("minecraft:smithing_table", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:fletching_table", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:cartography_table", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:loom", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:barrel", new int[]{54, 0}); // approximate
        NAME_MAP.put("minecraft:smoker", new int[]{61, 0}); // approximate
        NAME_MAP.put("minecraft:composter", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:lectern", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:stonecutter_block", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:bell", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:lantern", new int[]{50, 0}); // approximate
        NAME_MAP.put("minecraft:campfire", new int[]{62, 0}); // approximate
        NAME_MAP.put("minecraft:soul_campfire", new int[]{62, 0}); // approximate
        NAME_MAP.put("minecraft:bee_nest", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:beehive", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:honeycomb_block", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:honey_block", new int[]{165, 0}); // approximate
        NAME_MAP.put("minecraft:target", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:lodestone", new int[]{245, 0}); // approximate
        NAME_MAP.put("minecraft:netherite_block", new int[]{49, 0}); // approximate
        NAME_MAP.put("minecraft:chain", new int[]{85, 0}); // approximate
        NAME_MAP.put("minecraft:warped_nylium", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:crimson_nylium", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:warped_wart_block", new int[]{214, 0}); // approximate
        NAME_MAP.put("minecraft:nether_wart_block", new int[]{214, 0}); // approximate
        NAME_MAP.put("minecraft:warped_stem", new int[]{17, 0}); // approximate
        NAME_MAP.put("minecraft:crimson_stem", new int[]{17, 0}); // approximate
        NAME_MAP.put("minecraft:warped_hyphae", new int[]{17, 0}); // approximate
        NAME_MAP.put("minecraft:crimson_hyphae", new int[]{17, 0}); // approximate
        NAME_MAP.put("minecraft:warped_planks", new int[]{5, 0}); // approximate
        NAME_MAP.put("minecraft:crimson_planks", new int[]{5, 0}); // approximate
        NAME_MAP.put("minecraft:warped_roots", new int[]{31, 0}); // approximate
        NAME_MAP.put("minecraft:crimson_roots", new int[]{31, 0}); // approximate
        NAME_MAP.put("minecraft:weeping_vines", new int[]{106, 0}); // approximate
        NAME_MAP.put("minecraft:twisting_vines", new int[]{106, 0}); // approximate
        NAME_MAP.put("minecraft:soul_soil", new int[]{88, 0}); // approximate
        NAME_MAP.put("minecraft:soul_fire", new int[]{51, 0}); // approximate
        NAME_MAP.put("minecraft:polished_blackstone", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:polished_blackstone_bricks", new int[]{112, 0}); // approximate
        NAME_MAP.put("minecraft:cracked_polished_blackstone_bricks", new int[]{112, 0}); // approximate
        NAME_MAP.put("minecraft:chiseled_polished_blackstone", new int[]{112, 0}); // approximate
        NAME_MAP.put("minecraft:gilded_blackstone", new int[]{14, 0}); // approximate
        NAME_MAP.put("minecraft:blackstone_slab", new int[]{44, 0}); // approximate
        NAME_MAP.put("minecraft:blackstone_stairs", new int[]{67, 0}); // approximate
        NAME_MAP.put("minecraft:blackstone_wall", new int[]{139, 0}); // approximate
        NAME_MAP.put("minecraft:polished_blackstone_slab", new int[]{44, 0}); // approximate
        NAME_MAP.put("minecraft:polished_blackstone_stairs", new int[]{67, 0}); // approximate
        NAME_MAP.put("minecraft:polished_blackstone_brick_slab", new int[]{44, 0}); // approximate
        NAME_MAP.put("minecraft:polished_blackstone_brick_stairs", new int[]{67, 0}); // approximate
        NAME_MAP.put("minecraft:quartz_bricks", new int[]{155, 0}); // approximate
        NAME_MAP.put("minecraft:amethyst_block", new int[]{155, 0}); // approximate
        NAME_MAP.put("minecraft:budding_amethyst", new int[]{155, 0}); // approximate
        NAME_MAP.put("minecraft:amethyst_cluster", new int[]{50, 0}); // approximate
        NAME_MAP.put("minecraft:large_amethyst_bud", new int[]{50, 0}); // approximate
        NAME_MAP.put("minecraft:medium_amethyst_bud", new int[]{50, 0}); // approximate
        NAME_MAP.put("minecraft:small_amethyst_bud", new int[]{50, 0}); // approximate
        NAME_MAP.put("minecraft:tuff", new int[]{1, 0}); // approximate
        NAME_MAP.put("minecraft:calcite", new int[]{1, 0}); // approximate
        NAME_MAP.put("minecraft:smooth_basalt", new int[]{87, 0}); // approximate
        NAME_MAP.put("minecraft:dripstone_block", new int[]{82, 0}); // approximate
        NAME_MAP.put("minecraft:pointed_dripstone", new int[]{82, 0}); // approximate
        NAME_MAP.put("minecraft:moss_block", new int[]{3, 0}); // approximate
        NAME_MAP.put("minecraft:spore_blossom", new int[]{31, 0}); // approximate
        NAME_MAP.put("minecraft:hanging_roots", new int[]{31, 0}); // approximate
        NAME_MAP.put("minecraft:rooted_dirt", new int[]{3, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate", new int[]{1, 0}); // approximate
        NAME_MAP.put("minecraft:cobbled_deepslate", new int[]{4, 0}); // approximate
        NAME_MAP.put("minecraft:polished_deepslate", new int[]{1, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate_coal_ore", new int[]{16, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate_iron_ore", new int[]{15, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate_gold_ore", new int[]{14, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate_diamond_ore", new int[]{56, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate_lapis_ore", new int[]{21, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate_redstone_ore", new int[]{73, 0}); // approximate
        NAME_MAP.put("minecraft:deepslate_emerald_ore", new int[]{129, 0}); // approximate
        NAME_MAP.put("minecraft:lightning_rod", new int[]{50, 0}); // approximate
        NAME_MAP.put("minecraft:glow_lichen", new int[]{106, 0}); // approximate
        NAME_MAP.put("minecraft:cave_vines", new int[]{106, 0}); // approximate
        NAME_MAP.put("minecraft:azalea", new int[]{31, 0}); // approximate
        NAME_MAP.put("minecraft:azalea_leaves", new int[]{18, 0}); // approximate
        NAME_MAP.put("minecraft:flowering_azalea_leaves", new int[]{18, 0}); // approximate
        NAME_MAP.put("minecraft:big_dripleaf", new int[]{31, 0}); // approximate
        NAME_MAP.put("minecraft:small_dripleaf", new int[]{31, 0}); // approximate
        NAME_MAP.put("minecraft:powder_snow", new int[]{80, 0}); // approximate
        NAME_MAP.put("minecraft:copper_ore", new int[]{15, 0}); // approximate
        NAME_MAP.put("minecraft:copper_block", new int[]{41, 0}); // approximate
        NAME_MAP.put("minecraft:exposed_copper", new int[]{41, 0}); // approximate
        NAME_MAP.put("minecraft:weathered_copper", new int[]{41, 0}); // approximate
        NAME_MAP.put("minecraft:oxidized_copper", new int[]{41, 0}); // approximate
        NAME_MAP.put("minecraft:waxed_copper", new int[]{41, 0}); // approximate
        NAME_MAP.put("minecraft:cut_copper", new int[]{41, 0}); // approximate
        NAME_MAP.put("minecraft:raw_copper_block", new int[]{41, 0}); // approximate
        NAME_MAP.put("minecraft:raw_iron_block", new int[]{42, 0}); // approximate
        NAME_MAP.put("minecraft:raw_gold_block", new int[]{41, 0}); // approximate
    }

    /**
     * Maps a (name, states) pair to legacy [id, metadata].
     * Returns [1, 0] (stone) as a safe fallback for unknown solid blocks.
     */
    private static int[] mapBlock(String name, NbtMap states) {
        if (name == null || name.isEmpty()) return new int[]{0, 0};

        // Wool colors
        if (name.equals("minecraft:wool")) {
            String color = getStateString(states, "color");
            return new int[]{35, woolMeta(color)};
        }
        // Stained glass / stained clay / carpet / concrete / concrete_powder
        if (name.equals("minecraft:stained_glass") || name.equals("minecraft:stained_hardened_clay")
                || name.equals("minecraft:carpet") || name.equals("minecraft:concrete")
                || name.equals("minecraft:concrete_powder") || name.equals("minecraft:stained_glass_pane")) {
            String color = getStateString(states, "color");
            int base = name.contains("glass_pane") ? 160 : name.contains("glass") ? 241 :
                    name.contains("concrete_powder") ? 182 : name.contains("concrete") ? 181 :
                    name.contains("carpet") ? 171 : 159;
            return new int[]{base, woolMeta(color)};
        }

        // Wood / logs
        if (name.equals("minecraft:log") || name.equals("minecraft:log2")) {
            String species = getStateString(states, "old_log_type");
            if (species == null) species = getStateString(states, "new_log_type");
            if (species == null) species = getStateString(states, "wood_type");
            int meta = 0;
            if ("oak".equals(species) || "oak".equals(getStateString(states, "pillar_axis"))) meta = 0;
            else if ("spruce".equals(species)) meta = 1;
            else if ("birch".equals(species)) meta = 2;
            else if ("jungle".equals(species)) meta = 3;
            else if ("acacia".equals(species)) meta = 4;
            else if ("dark_oak".equals(species)) meta = 5;
            // Check pillar_axis for bark
            String axis = getStateString(states, "pillar_axis");
            if ("x".equals(axis)) meta |= 4;
            else if ("z".equals(axis)) meta |= 8;
            return new int[]{name.equals("minecraft:log2") ? 162 : 17, meta};
        }

        // Planks
        if (name.equals("minecraft:planks")) {
            String type = getStateString(states, "wood_type");
            int meta = 0;
            if ("oak".equals(type)) meta = 0;
            else if ("spruce".equals(type)) meta = 1;
            else if ("birch".equals(type)) meta = 2;
            else if ("jungle".equals(type)) meta = 3;
            else if ("acacia".equals(type)) meta = 4;
            else if ("dark_oak".equals(type)) meta = 5;
            return new int[]{5, meta};
        }

        // Leaves
        if (name.equals("minecraft:leaves") || name.equals("minecraft:leaves2")) {
            String type = getStateString(states, "old_leaf_type");
            if (type == null) type = getStateString(states, "new_leaf_type");
            int meta = 0;
            if ("oak".equals(type)) meta = 0;
            else if ("spruce".equals(type)) meta = 1;
            else if ("birch".equals(type)) meta = 2;
            else if ("jungle".equals(type)) meta = 3;
            else if ("acacia".equals(type)) meta = 4;
            else if ("dark_oak".equals(type)) meta = 5;
            return new int[]{name.equals("minecraft:leaves2") ? 161 : 18, meta};
        }

        // Sapling
        if (name.equals("minecraft:sapling")) {
            String type = getStateString(states, "sapling_type");
            int meta = 0;
            if ("oak".equals(type)) meta = 0;
            else if ("spruce".equals(type)) meta = 1;
            else if ("birch".equals(type)) meta = 2;
            else if ("jungle".equals(type)) meta = 3;
            else if ("acacia".equals(type)) meta = 4;
            else if ("dark_oak".equals(type)) meta = 5;
            return new int[]{6, meta};
        }

        // Tallgrass
        if (name.equals("minecraft:tallgrass")) {
            String type = getStateString(states, "tall_grass_type");
            int meta = 0;
            if ("dead_bush".equals(type)) meta = 0;
            else if ("tall_grass".equals(type)) meta = 1;
            else if ("fern".equals(type)) meta = 2;
            else if ("snow".equals(type)) meta = 3;
            return new int[]{31, meta};
        }

        // Red flower
        if (name.equals("minecraft:red_flower")) {
            String type = getStateString(states, "flower_type");
            int meta = 0;
            if ("poppy".equals(type)) meta = 0;
            else if ("orchid".equals(type)) meta = 1;
            else if ("allium".equals(type)) meta = 2;
            else if ("houstonia".equals(type)) meta = 3;
            else if ("tulip_red".equals(type)) meta = 4;
            else if ("tulip_orange".equals(type)) meta = 5;
            else if ("tulip_white".equals(type)) meta = 6;
            else if ("tulip_pink".equals(type)) meta = 7;
            else if ("oxeye".equals(type)) meta = 8;
            else if ("cornflower".equals(type)) meta = 9;
            return new int[]{38, meta};
        }

        // Double plant
        if (name.equals("minecraft:double_plant")) {
            String type = getStateString(states, "double_plant_type");
            int meta = 0;
            if ("sunflower".equals(type)) meta = 0;
            else if ("syringa".equals(type)) meta = 1;
            else if ("grass".equals(type)) meta = 2;
            else if ("fern".equals(type)) meta = 3;
            else if ("rose".equals(type)) meta = 4;
            else if ("paeonia".equals(type)) meta = 5;
            return new int[]{175, meta};
        }

        // Stone slab variants
        if (name.equals("minecraft:stone_slab") || name.equals("minecraft:double_stone_slab")) {
            String type = getStateString(states, "stone_slab_type");
            int meta = slabMeta(type);
            return new int[]{name.equals("minecraft:stone_slab") ? 44 : 43, meta};
        }

        // Wooden slab
        if (name.equals("minecraft:wooden_slab") || name.equals("minecraft:double_wooden_slab")) {
            String type = getStateString(states, "wood_type");
            int meta = 0;
            if ("oak".equals(type)) meta = 0;
            else if ("spruce".equals(type)) meta = 1;
            else if ("birch".equals(type)) meta = 2;
            else if ("jungle".equals(type)) meta = 3;
            else if ("acacia".equals(type)) meta = 4;
            else if ("dark_oak".equals(type)) meta = 5;
            return new int[]{name.equals("minecraft:wooden_slab") ? 158 : 157, meta};
        }

        // Sandstone variants
        if (name.equals("minecraft:sandstone")) {
            String type = getStateString(states, "sand_stone_type");
            int meta = 0;
            if ("default".equals(type)) meta = 0;
            else if ("heiroglyphic".equals(type)) meta = 1;
            else if ("cut".equals(type)) meta = 2;
            else if ("smooth".equals(type)) meta = 3;
            return new int[]{24, meta};
        }

        // Stone brick variants
        if (name.equals("minecraft:stonebrick")) {
            String type = getStateString(states, "stone_brick_type");
            int meta = 0;
            if ("default".equals(type)) meta = 0;
            else if ("mossy".equals(type)) meta = 1;
            else if ("cracked".equals(type)) meta = 2;
            else if ("chiseled".equals(type)) meta = 3;
            else if ("smooth".equals(type)) meta = 4;
            return new int[]{98, meta};
        }

        // Prismarine variants
        if (name.equals("minecraft:prismarine")) {
            String type = getStateString(states, "prismarine_block_type");
            int meta = 0;
            if ("default".equals(type)) meta = 0;
            else if ("dark".equals(type)) meta = 1;
            else if ("bricks".equals(type)) meta = 2;
            return new int[]{168, meta};
        }

        // Quartz block variants
        if (name.equals("minecraft:quartz_block")) {
            String type = getStateString(states, "chisel_type");
            int meta = 0;
            if ("default".equals(type)) meta = 0;
            else if ("chiseled".equals(type)) meta = 1;
            else if ("lines".equals(type)) meta = 2;
            else if ("smooth".equals(type)) meta = 3;
            return new int[]{155, meta};
        }

        // Monster egg variants
        if (name.equals("minecraft:monster_egg")) {
            String type = getStateString(states, "monster_egg_stone_type");
            int meta = 0;
            if ("stone".equals(type)) meta = 0;
            else if ("cobblestone".equals(type)) meta = 1;
            else if ("stone_brick".equals(type)) meta = 2;
            else if ("mossy_stone_brick".equals(type)) meta = 3;
            else if ("cracked_stone_brick".equals(type)) meta = 4;
            else if ("chiseled_stone_brick".equals(type)) meta = 5;
            return new int[]{97, meta};
        }

        // Cobblestone wall variants
        if (name.equals("minecraft:cobblestone_wall")) {
            String type = getStateString(states, "wall_block_type");
            int meta = 0;
            if ("cobblestone".equals(type)) meta = 0;
            else if ("mossy_cobblestone".equals(type)) meta = 1;
            else if ("granite".equals(type)) meta = 2;
            else if ("diorite".equals(type)) meta = 3;
            else if ("andesite".equals(type)) meta = 4;
            else if ("sandstone".equals(type)) meta = 5;
            else if ("brick".equals(type)) meta = 6;
            else if ("stone_brick".equals(type)) meta = 7;
            else if ("mossy_stone_brick".equals(type)) meta = 8;
            else if ("nether_brick".equals(type)) meta = 9;
            else if ("end_brick".equals(type)) meta = 10;
            else if ("prismarine".equals(type)) meta = 11;
            else if ("red_sandstone".equals(type)) meta = 12;
            else if ("red_nether_brick".equals(type)) meta = 13;
            return new int[]{139, meta};
        }

        // Dirt variants
        if (name.equals("minecraft:dirt")) {
            String type = getStateString(states, "dirt_type");
            int meta = 0;
            if ("normal".equals(type)) meta = 0;
            else if ("coarse".equals(type)) meta = 1;
            else if ("podzol".equals(type)) meta = 2;
            return new int[]{3, meta};
        }

        // Sponge
        if (name.equals("minecraft:sponge")) {
            String type = getStateString(states, "sponge_type");
            int meta = 0;
            if ("dry".equals(type)) meta = 0;
            else if ("wet".equals(type)) meta = 1;
            return new int[]{19, meta};
        }

        // Anvil
        if (name.equals("minecraft:anvil")) {
            String type = getStateString(states, "damage");
            int meta = 0;
            if ("undamaged".equals(type)) meta = 0;
            else if ("slightly_damaged".equals(type)) meta = 1;
            else if ("very_damaged".equals(type)) meta = 2;
            else if ("broken".equals(type)) meta = 3;
            return new int[]{145, meta};
        }

        // Skull
        if (name.equals("minecraft:skull")) {
            String type = getStateString(states, "skull_type");
            int meta = 0;
            if ("skeleton".equals(type)) meta = 0;
            else if ("wither_skeleton".equals(type)) meta = 1;
            else if ("zombie".equals(type)) meta = 2;
            else if ("player".equals(type)) meta = 3;
            else if ("creeper".equals(type)) meta = 4;
            else if ("dragon".equals(type)) meta = 5;
            return new int[]{144, meta};
        }

        // Prismarine stairs
        if (name.equals("minecraft:prismarine_stairs")) return new int[]{2, 0}; // no legacy equivalent

        // Check static name map
        int[] mapped = NAME_MAP.get(name);
        if (mapped != null) return mapped;

        // Fallback: air for air-like blocks, stone for unknown solid blocks
        if (name.contains("air")) return new int[]{0, 0};
        if (name.contains("torch") || name.contains("flower") || name.contains("mushroom")
                || name.contains("sapling") || name.contains("grass") || name.contains("fern")
                || name.contains("vine") || name.contains("roots") || name.contains("bush")) {
            return new int[]{31, 1}; // tallgrass fallback
        }
        if (name.contains("button") || name.contains("pressure_plate") || name.contains("lever")
                || name.contains("rail")) {
            return new int[]{0, 0}; // small mechanical blocks -> air for now
        }
        return new int[]{1, 0}; // stone fallback
    }

    private static String getStateString(NbtMap states, String key) {
        if (states == null) return null;
        return getNbtString(states, key);
    }

    private static String getNbtString(NbtMap map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object val = map.get(key);
        if (val instanceof String) return (String) val;
        if (val instanceof byte[]) {
            // Some NBT implementations store strings as byte arrays
            return new String((byte[]) val);
        }
        return val != null ? val.toString() : null;
    }

    private static NbtMap getNbtCompound(NbtMap map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object val = map.get(key);
        if (val instanceof NbtMap) return (NbtMap) val;
        return null;
    }

    private static int woolMeta(String color) {
        if (color == null) return 0;
        switch (color) {
            case "white": return 0;
            case "orange": return 1;
            case "magenta": return 2;
            case "light_blue": return 3;
            case "yellow": return 4;
            case "lime": return 5;
            case "pink": return 6;
            case "gray": return 7;
            case "silver":
            case "light_gray": return 8;
            case "cyan": return 9;
            case "purple": return 10;
            case "blue": return 11;
            case "brown": return 12;
            case "green": return 13;
            case "red": return 14;
            case "black": return 15;
            default: return 0;
        }
    }

    private static int slabMeta(String type) {
        if (type == null) return 0;
        switch (type) {
            case "stone": return 0;
            case "sandstone": return 1;
            case "wood": return 2;
            case "cobblestone": return 3;
            case "brick": return 4;
            case "stone_brick": return 5;
            case "quartz": return 6;
            case "nether_brick": return 7;
            default: return 0;
        }
    }
}
