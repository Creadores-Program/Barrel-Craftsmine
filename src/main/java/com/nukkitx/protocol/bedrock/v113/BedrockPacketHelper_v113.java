package com.nukkitx.protocol.bedrock.v113;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NBTOutputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtUtils;
import com.nukkitx.network.VarInts;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.data.GameRuleData;
import com.nukkitx.protocol.bedrock.data.LevelEventType;
import com.nukkitx.protocol.bedrock.data.SoundEvent;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.data.entity.*;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.skin.ImageData;
import com.nukkitx.protocol.bedrock.data.skin.SerializedSkin;
import com.nukkitx.protocol.bedrock.util.LittleEndianByteBufOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;

import static com.nukkitx.protocol.bedrock.data.entity.EntityData.Type;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BedrockPacketHelper_v113 extends BedrockPacketHelper {

    public static final BedrockPacketHelper INSTANCE = new BedrockPacketHelper_v113();

    @Override
    protected void registerEntityData() {
        this.addEntityData(0, EntityData.FLAGS);
        this.addEntityData(1, EntityData.HEALTH);
        this.addEntityData(2, EntityData.VARIANT);
        this.addEntityData(3, EntityData.COLOR);
        this.addEntityData(4, EntityData.NAMETAG);
        this.addEntityData(5, EntityData.OWNER_EID);
        this.addEntityData(6, EntityData.TARGET_EID);
        this.addEntityData(7, EntityData.AIR_SUPPLY);
        this.addEntityData(8, EntityData.EFFECT_COLOR);
        this.addEntityData(9, EntityData.EFFECT_AMBIENT);
        this.addEntityData(10, EntityData.JUMP_DURATION);
        this.addEntityData(11, EntityData.HURT_TIME);
        this.addEntityData(12, EntityData.HURT_DIRECTION);
        this.addEntityData(13, EntityData.ROW_TIME_LEFT);
        this.addEntityData(14, EntityData.ROW_TIME_RIGHT);
        this.addEntityData(15, EntityData.EXPERIENCE_VALUE);
        this.addEntityData(16, EntityData.DISPLAY_BLOCK_STATE_OR_HORSE_FLAGS_OR_WITHER_SKULL_DANGEROUS);
        this.addEntityData(17, EntityData.DISPLAY_OFFSET);
        this.addEntityData(18, EntityData.CUSTOM_DISPLAY);
        this.addEntityData(19, EntityData.SWELL);
        this.addEntityData(20, EntityData.OLD_SWELL);
        this.addEntityData(21, EntityData.SWELL_DIRECTION);
        this.addEntityData(22, EntityData.CHARGE_AMOUNT);
        this.addEntityData(23, EntityData.ENDERMAN_ITEM_ID);
        this.addEntityData(24, EntityData.ENDERMAN_ITEM_DAMAGE);
        this.addEntityData(25, EntityData.AGE);
        this.addEntityData(27, EntityData.PLAYER_FLAGS);
        this.addEntityData(28, EntityData.PLAYER_INDEX);
        this.addEntityData(29, EntityData.BED_POSITION);
        this.addEntityData(30, EntityData.FIREBALL_X_POWER);
        this.addEntityData(31, EntityData.FIREBALL_Y_POWER);
        this.addEntityData(32, EntityData.FIREBALL_Z_POWER);
        this.addEntityData(33, EntityData.AUX_POWER);
        this.addEntityData(34, EntityData.FISH_X);
        this.addEntityData(35, EntityData.FISH_Z);
        this.addEntityData(36, EntityData.FISH_ANGLE);
        this.addEntityData(37, EntityData.POTION_AUX_VALUE);
        this.addEntityData(38, EntityData.LEASH_HOLDER_EID);
        this.addEntityData(39, EntityData.SCALE);
        this.addEntityData(40, EntityData.INTERACTIVE_TAG);
        this.addEntityData(41, EntityData.NPC_SKIN_ID);
        this.addEntityData(42, EntityData.URL_TAG);
        this.addEntityData(43, EntityData.MAX_AIR_SUPPLY);
        this.addEntityData(44, EntityData.MARK_VARIANT);
        this.addEntityData(45, EntityData.CONTAINER_TYPE);
        this.addEntityData(46, EntityData.CONTAINER_BASE_SIZE);
        this.addEntityData(47, EntityData.CONTAINER_STRENGTH_MODIFIER);
        this.addEntityData(48, EntityData.BLOCK_TARGET);
        this.addEntityData(49, EntityData.WITHER_INVULNERABLE_TICKS);
        this.addEntityData(50, EntityData.WITHER_CENTER_TARGET);
        this.addEntityData(51, EntityData.WITHER_LEFT_TARGET);
        this.addEntityData(52, EntityData.WITHER_RIGHT_TARGET);
        this.addEntityData(53, EntityData.WITHER_AERIAL_ATTACK);
        this.addEntityData(54, EntityData.BOUNDING_BOX_WIDTH);
        this.addEntityData(55, EntityData.BOUNDING_BOX_HEIGHT);
        this.addEntityData(56, EntityData.FUSE_LENGTH);
        this.addEntityData(57, EntityData.RIDER_SEAT_POSITION);
        this.addEntityData(58, EntityData.RIDER_ROTATION_LOCKED);
        this.addEntityData(59, EntityData.RIDER_MAX_ROTATION);
        this.addEntityData(60, EntityData.RIDER_MIN_ROTATION);
        this.addEntityData(61, EntityData.AREA_EFFECT_CLOUD_RADIUS);
        this.addEntityData(62, EntityData.AREA_EFFECT_CLOUD_WAITING);
        this.addEntityData(63, EntityData.AREA_EFFECT_CLOUD_PARTICLE_ID);
        this.addEntityData(64, EntityData.SHULKER_PEAK_HEIGHT);
        this.addEntityData(65, EntityData.SHULKER_ATTACH_FACE);
        this.addEntityData(66, EntityData.SHULKER_UNKNOWN);
        this.addEntityData(67, EntityData.SHULKER_ATTACH_POS);
        this.addEntityData(68, EntityData.TRADE_PLAYER_EID);
        this.addEntityData(70, EntityData.COMMAND_BLOCK_ENABLED); // Unsure
        this.addEntityData(71, EntityData.COMMAND_BLOCK_COMMAND);
        this.addEntityData(72, EntityData.COMMAND_BLOCK_LAST_OUTPUT);
        this.addEntityData(73, EntityData.COMMAND_BLOCK_TRACK_OUTPUT);
        this.addEntityData(74, EntityData.CONTROLLING_RIDER_SEAT_INDEX);
        this.addEntityData(75, EntityData.STRENGTH);
        this.addEntityData(76, EntityData.MAX_STRENGTH);
        this.addEntityData(77, EntityData.EVOKER_SPELL_COLOR);
        this.addEntityData(78, EntityData.ARMOR_STAND_POSE_INDEX);
        this.addEntityData(79, EntityData.ENDER_CRYSTAL_TIME_OFFSET);
    }

    @Override
    protected void registerEntityFlags() {
        this.addEntityFlag(0, EntityFlag.ON_FIRE);
        this.addEntityFlag(1, EntityFlag.SNEAKING);
        this.addEntityFlag(2, EntityFlag.RIDING);
        this.addEntityFlag(3, EntityFlag.SPRINTING);
        this.addEntityFlag(4, EntityFlag.USING_ITEM);
        this.addEntityFlag(5, EntityFlag.INVISIBLE);
        this.addEntityFlag(6, EntityFlag.TEMPTED);
        this.addEntityFlag(7, EntityFlag.IN_LOVE);
        this.addEntityFlag(8, EntityFlag.SADDLED);
        this.addEntityFlag(9, EntityFlag.POWERED);
        this.addEntityFlag(10, EntityFlag.IGNITED);
        this.addEntityFlag(11, EntityFlag.BABY);
        this.addEntityFlag(12, EntityFlag.CONVERTING);
        this.addEntityFlag(13, EntityFlag.CRITICAL);
        this.addEntityFlag(14, EntityFlag.CAN_SHOW_NAME);
        this.addEntityFlag(15, EntityFlag.ALWAYS_SHOW_NAME);
        this.addEntityFlag(16, EntityFlag.NO_AI);
        this.addEntityFlag(17, EntityFlag.SILENT);
        this.addEntityFlag(18, EntityFlag.WALL_CLIMBING);
        this.addEntityFlag(19, EntityFlag.CAN_CLIMB);
        this.addEntityFlag(20, EntityFlag.CAN_SWIM);
        this.addEntityFlag(21, EntityFlag.CAN_FLY);
        this.addEntityFlag(22, EntityFlag.RESTING);
        this.addEntityFlag(23, EntityFlag.SITTING);
        this.addEntityFlag(24, EntityFlag.ANGRY);
        this.addEntityFlag(25, EntityFlag.INTERESTED);
        this.addEntityFlag(26, EntityFlag.CHARGED);
        this.addEntityFlag(27, EntityFlag.TAMED);
        this.addEntityFlag(28, EntityFlag.LEASHED);
        this.addEntityFlag(29, EntityFlag.SHEARED);
        this.addEntityFlag(30, EntityFlag.GLIDING);
        this.addEntityFlag(31, EntityFlag.ELDER);
        this.addEntityFlag(32, EntityFlag.MOVING);
        this.addEntityFlag(33, EntityFlag.BREATHING);
        this.addEntityFlag(34, EntityFlag.CHESTED);
        this.addEntityFlag(35, EntityFlag.STACKABLE);
        this.addEntityFlag(36, EntityFlag.SHOW_BOTTOM);
        this.addEntityFlag(37, EntityFlag.STANDING);
        this.addEntityFlag(38, EntityFlag.SHAKING);
        this.addEntityFlag(39, EntityFlag.IDLING);
        this.addEntityFlag(40, EntityFlag.CASTING);
        this.addEntityFlag(41, EntityFlag.CHARGING);
        this.addEntityFlag(45, EntityFlag.LINGERING);
    }

    @Override
    protected void registerEntityDataTypes() {
        this.addEntityDataType(7, Type.FLAGS);
        this.addEntityDataType(0, Type.BYTE);
        this.addEntityDataType(1, Type.SHORT);
        this.addEntityDataType(2, Type.INT);
        this.addEntityDataType(3, Type.FLOAT);
        this.addEntityDataType(4, Type.STRING);
        this.addEntityDataType(5, Type.NBT);
        this.addEntityDataType(6, Type.VECTOR3I);
        this.addEntityDataType(7, Type.LONG);
        this.addEntityDataType(8, Type.VECTOR3F);
    }

    @Override
    protected void registerEntityEvents() {
        this.addEntityEvent(0, EntityEventType.NONE);
        this.addEntityEvent(1, EntityEventType.JUMP);
        this.addEntityEvent(2, EntityEventType.HURT);
        this.addEntityEvent(3, EntityEventType.DEATH);
        this.addEntityEvent(4, EntityEventType.ATTACK_START);
        this.addEntityEvent(5, EntityEventType.ATTACK_STOP);
        this.addEntityEvent(6, EntityEventType.TAME_FAILED);
        this.addEntityEvent(7, EntityEventType.TAME_SUCCEEDED);
        this.addEntityEvent(8, EntityEventType.SHAKE_WETNESS);
        this.addEntityEvent(9, EntityEventType.USE_ITEM);
        this.addEntityEvent(10, EntityEventType.EAT_GRASS);
        this.addEntityEvent(11, EntityEventType.FISH_HOOK_BUBBLE);
        this.addEntityEvent(12, EntityEventType.FISH_HOOK_POSITION);
        this.addEntityEvent(13, EntityEventType.FISH_HOOK_TIME);
        this.addEntityEvent(14, EntityEventType.FISH_HOOK_TEASE);
        this.addEntityEvent(15, EntityEventType.SQUID_FLEEING);
        this.addEntityEvent(16, EntityEventType.ZOMBIE_VILLAGER_CURE);
        this.addEntityEvent(17, EntityEventType.PLAY_AMBIENT);
        this.addEntityEvent(18, EntityEventType.RESPAWN);
        this.addEntityEvent(19, EntityEventType.GOLEM_FLOWER_OFFER);
        this.addEntityEvent(20, EntityEventType.GOLEM_FLOWER_WITHDRAW);
        this.addEntityEvent(21, EntityEventType.LOVE_PARTICLES);
        this.addEntityEvent(22, EntityEventType.VILLAGER_ANGRY);
        this.addEntityEvent(23, EntityEventType.VILLAGER_HAPPY);
        this.addEntityEvent(24, EntityEventType.WITCH_HAT_MAGIC);
        this.addEntityEvent(25, EntityEventType.FIREWORK_EXPLODE);
        this.addEntityEvent(26, EntityEventType.IN_LOVE_HEARTS);
        this.addEntityEvent(27, EntityEventType.SILVERFISH_MERGE_WITH_STONE);
        this.addEntityEvent(28, EntityEventType.GUARDIAN_ATTACK_ANIMATION);
        this.addEntityEvent(29, EntityEventType.WITCH_DRINK_POTION);
        this.addEntityEvent(30, EntityEventType.WITCH_THROW_POTION);
        this.addEntityEvent(31, EntityEventType.PRIME_TNT_MINECART);
        this.addEntityEvent(32, EntityEventType.PRIME_CREEPER);
        this.addEntityEvent(33, EntityEventType.AIR_SUPPLY);
        this.addEntityEvent(34, EntityEventType.PLAYER_ADD_XP_LEVELS);
        this.addEntityEvent(35, EntityEventType.ELDER_GUARDIAN_CURSE);
        this.addEntityEvent(36, EntityEventType.AGENT_ARM_SWING);
        this.addEntityEvent(37, EntityEventType.ENDER_DRAGON_DEATH);
        this.addEntityEvent(38, EntityEventType.DUST_PARTICLES);
        this.addEntityEvent(39, EntityEventType.ARROW_SHAKE);

        this.addEntityEvent(57, EntityEventType.EATING_ITEM);
        this.addEntityEvent(60, EntityEventType.BABY_ANIMAL_FEED);
        this.addEntityEvent(61, EntityEventType.DEATH_SMOKE_CLOUD);
        this.addEntityEvent(62, EntityEventType.COMPLETE_TRADE);
        this.addEntityEvent(63, EntityEventType.REMOVE_LEASH);
        this.addEntityEvent(64, EntityEventType.CARAVAN);
        this.addEntityEvent(65, EntityEventType.CONSUME_TOTEM);
        this.addEntityEvent(66, EntityEventType.CHECK_TREASURE_HUNTER_ACHIEVEMENT);
        this.addEntityEvent(67, EntityEventType.ENTITY_SPAWN);
        this.addEntityEvent(68, EntityEventType.DRAGON_FLAMING);
        this.addEntityEvent(69, EntityEventType.UPDATE_ITEM_STACK_SIZE);
    }

    @Override
    protected void registerGameRuleTypes() {
        this.addGameRuleType(1, Boolean.class);
        this.addGameRuleType(2, Integer.class);
        this.addGameRuleType(3, Float.class);
    }

    @Override
    protected void registerSoundEvents() {
        this.addSoundEvent(0, SoundEvent.ITEM_USE_ON);
        this.addSoundEvent(1, SoundEvent.HIT);
        this.addSoundEvent(2, SoundEvent.STEP);
        this.addSoundEvent(3, SoundEvent.JUMP);
        this.addSoundEvent(4, SoundEvent.BREAK);
        this.addSoundEvent(5, SoundEvent.PLACE);
        this.addSoundEvent(6, SoundEvent.HEAVY_STEP);
        this.addSoundEvent(7, SoundEvent.GALLOP);
        this.addSoundEvent(8, SoundEvent.FALL);
        this.addSoundEvent(9, SoundEvent.AMBIENT);
        this.addSoundEvent(10, SoundEvent.AMBIENT_BABY);
        this.addSoundEvent(11, SoundEvent.AMBIENT_IN_WATER);
        this.addSoundEvent(12, SoundEvent.BREATHE);
        this.addSoundEvent(13, SoundEvent.DEATH);
        this.addSoundEvent(14, SoundEvent.DEATH_IN_WATER);
        this.addSoundEvent(15, SoundEvent.DEATH_TO_ZOMBIE);
        this.addSoundEvent(16, SoundEvent.HURT);
        this.addSoundEvent(17, SoundEvent.HURT_IN_WATER);
        this.addSoundEvent(18, SoundEvent.MAD);
        this.addSoundEvent(19, SoundEvent.BOOST);
        this.addSoundEvent(20, SoundEvent.BOW);
        this.addSoundEvent(21, SoundEvent.SQUISH_BIG);
        this.addSoundEvent(22, SoundEvent.SQUISH_SMALL);
        this.addSoundEvent(23, SoundEvent.FALL_BIG);
        this.addSoundEvent(24, SoundEvent.FALL_SMALL);
        this.addSoundEvent(25, SoundEvent.SPLASH);
        this.addSoundEvent(26, SoundEvent.FIZZ);
        this.addSoundEvent(27, SoundEvent.FLAP);
        this.addSoundEvent(28, SoundEvent.SWIM);
        this.addSoundEvent(29, SoundEvent.DRINK);
        this.addSoundEvent(30, SoundEvent.EAT);
        this.addSoundEvent(31, SoundEvent.TAKEOFF);
        this.addSoundEvent(32, SoundEvent.SHAKE);
        this.addSoundEvent(33, SoundEvent.PLOP);
        this.addSoundEvent(34, SoundEvent.LAND);
        this.addSoundEvent(35, SoundEvent.SADDLE);
        this.addSoundEvent(36, SoundEvent.ARMOR);
        this.addSoundEvent(37, SoundEvent.ADD_CHEST);
        this.addSoundEvent(38, SoundEvent.THROW);
        this.addSoundEvent(39, SoundEvent.ATTACK);
        this.addSoundEvent(40, SoundEvent.ATTACK_NODAMAGE);
        this.addSoundEvent(41, SoundEvent.WARN);
        this.addSoundEvent(42, SoundEvent.SHEAR);
        this.addSoundEvent(43, SoundEvent.MILK);
        this.addSoundEvent(44, SoundEvent.THUNDER);
        this.addSoundEvent(45, SoundEvent.EXPLODE);
        this.addSoundEvent(46, SoundEvent.FIRE);
        this.addSoundEvent(47, SoundEvent.IGNITE);
        this.addSoundEvent(48, SoundEvent.FUSE);
        this.addSoundEvent(49, SoundEvent.STARE);
        this.addSoundEvent(50, SoundEvent.SPAWN);
        this.addSoundEvent(51, SoundEvent.SHOOT);
        this.addSoundEvent(52, SoundEvent.BREAK_BLOCK);
        this.addSoundEvent(53, SoundEvent.REMEDY);
        this.addSoundEvent(54, SoundEvent.UNFECT);
        this.addSoundEvent(55, SoundEvent.LEVELUP);
        this.addSoundEvent(56, SoundEvent.BOW_HIT);
        this.addSoundEvent(57, SoundEvent.BULLET_HIT);
        this.addSoundEvent(58, SoundEvent.EXTINGUISH_FIRE);
        this.addSoundEvent(59, SoundEvent.ITEM_FIZZ);
        this.addSoundEvent(60, SoundEvent.CHEST_OPEN);
        this.addSoundEvent(61, SoundEvent.CHEST_CLOSED);
        this.addSoundEvent(62, SoundEvent.SHULKERBOX_OPEN);
        this.addSoundEvent(63, SoundEvent.SHULKERBOX_CLOSED);
        this.addSoundEvent(64, SoundEvent.POWER_ON);
        this.addSoundEvent(65, SoundEvent.POWER_OFF);
        this.addSoundEvent(66, SoundEvent.ATTACH);
        this.addSoundEvent(67, SoundEvent.DETACH);
        this.addSoundEvent(68, SoundEvent.DENY);
        this.addSoundEvent(69, SoundEvent.TRIPOD);
        this.addSoundEvent(70, SoundEvent.POP);
        this.addSoundEvent(71, SoundEvent.DROP_SLOT);
        this.addSoundEvent(72, SoundEvent.NOTE);
        this.addSoundEvent(73, SoundEvent.THORNS);
        this.addSoundEvent(74, SoundEvent.PISTON_IN);
        this.addSoundEvent(75, SoundEvent.PISTON_OUT);
        this.addSoundEvent(76, SoundEvent.PORTAL);
        this.addSoundEvent(77, SoundEvent.WATER);
        this.addSoundEvent(78, SoundEvent.LAVA_POP);
        this.addSoundEvent(79, SoundEvent.LAVA);
        this.addSoundEvent(80, SoundEvent.BURP);
        this.addSoundEvent(81, SoundEvent.BUCKET_FILL_WATER);
        this.addSoundEvent(82, SoundEvent.BUCKET_FILL_LAVA);
        this.addSoundEvent(83, SoundEvent.BUCKET_EMPTY_WATER);
        this.addSoundEvent(84, SoundEvent.BUCKET_EMPTY_LAVA);
        this.addSoundEvent(85, SoundEvent.FLOP);
        this.addSoundEvent(86, SoundEvent.ELDERGUARDIAN_CURSE);
        this.addSoundEvent(87, SoundEvent.MOB_WARNING);
        this.addSoundEvent(88, SoundEvent.MOB_WARNING_BABY);
        this.addSoundEvent(89, SoundEvent.TELEPORT);
        this.addSoundEvent(90, SoundEvent.SHULKER_OPEN);
        this.addSoundEvent(91, SoundEvent.SHULKER_CLOSE);
        this.addSoundEvent(92, SoundEvent.HAGGLE);
        this.addSoundEvent(93, SoundEvent.HAGGLE_YES);
        this.addSoundEvent(94, SoundEvent.HAGGLE_NO);
        this.addSoundEvent(95, SoundEvent.HAGGLE_IDLE);
        this.addSoundEvent(96, SoundEvent.CHORUS_GROW);
        this.addSoundEvent(97, SoundEvent.CHORUS_DEATH);
        this.addSoundEvent(98, SoundEvent.GLASS);
        this.addSoundEvent(99, SoundEvent.CAST_SPELL);
        this.addSoundEvent(100, SoundEvent.PREPARE_ATTACK);
        this.addSoundEvent(101, SoundEvent.PREPARE_SUMMON);
        this.addSoundEvent(102, SoundEvent.PREPARE_WOLOLO);
        this.addSoundEvent(103, SoundEvent.FANG);
        this.addSoundEvent(104, SoundEvent.CHARGE);
        this.addSoundEvent(105, SoundEvent.CAMERA_TAKE_PICTURE);
        this.addSoundEvent(106, SoundEvent.DEFAULT);
        this.addSoundEvent(107, SoundEvent.UNDEFINED);
    }

    @Override
    protected void registerResourcePackTypes() {
        // NOOP
    }

    @Override
    protected void registerLevelEvents() {
        this.addLevelEvent(0, LevelEventType.UNDEFINED);

        // Sounds
        int sound = 1000;
        this.addLevelEvent(sound, LevelEventType.SOUND_CLICK);
        this.addLevelEvent(1 + sound, LevelEventType.SOUND_CLICK_FAIL);
        this.addLevelEvent(2 + sound, LevelEventType.SOUND_LAUNCH);
        this.addLevelEvent(3 + sound, LevelEventType.SOUND_DOOR_OPEN);
        this.addLevelEvent(4 + sound, LevelEventType.SOUND_FIZZ);
        this.addLevelEvent(5 + sound, LevelEventType.SOUND_FUSE);
        this.addLevelEvent(6 + sound, LevelEventType.SOUND_PLAY_RECORDING);
        this.addLevelEvent(7 + sound, LevelEventType.SOUND_GHAST_WARNING);
        this.addLevelEvent(8 + sound, LevelEventType.SOUND_GHAST_FIREBALL);
        this.addLevelEvent(9 + sound, LevelEventType.SOUND_BLAZE_FIREBALL);
        this.addLevelEvent(10 + sound, LevelEventType.SOUND_ZOMBIE_DOOR_BUMP);

        this.addLevelEvent(12 + sound, LevelEventType.SOUND_ZOMBIE_DOOR_CRASH);

        this.addLevelEvent(16 + sound, LevelEventType.SOUND_ZOMBIE_INFECTED);
        this.addLevelEvent(17 + sound, LevelEventType.SOUND_ZOMBIE_CONVERTED);
        this.addLevelEvent(18 + sound, LevelEventType.SOUND_ENDERMAN_TELEPORT);

        this.addLevelEvent(20 + sound, LevelEventType.SOUND_ANVIL_BROKEN);
        this.addLevelEvent(21 + sound, LevelEventType.SOUND_ANVIL_USED);
        this.addLevelEvent(22 + sound, LevelEventType.SOUND_ANVIL_LAND);

        this.addLevelEvent(30 + sound, LevelEventType.SOUND_INFINITY_ARROW_PICKUP);

        this.addLevelEvent(32 + sound, LevelEventType.SOUND_TELEPORT_ENDERPEARL);

        this.addLevelEvent(40 + sound, LevelEventType.SOUND_ITEMFRAME_ITEM_ADD);
        this.addLevelEvent(41 + sound, LevelEventType.SOUND_ITEMFRAME_BREAK);
        this.addLevelEvent(42 + sound, LevelEventType.SOUND_ITEMFRAME_PLACE);
        this.addLevelEvent(43 + sound, LevelEventType.SOUND_ITEMFRAME_ITEM_REMOVE);
        this.addLevelEvent(44 + sound, LevelEventType.SOUND_ITEMFRAME_ITEM_ROTATE);

        this.addLevelEvent(51 + sound, LevelEventType.SOUND_EXPERIENCE_ORB_PICKUP);
        this.addLevelEvent(52 + sound, LevelEventType.SOUND_TOTEM_USED);

        // Particles
        int particle = 2000;
        this.addLevelEvent(particle, LevelEventType.PARTICLE_SHOOT);
        this.addLevelEvent(1 + particle, LevelEventType.PARTICLE_DESTROY_BLOCK);
        this.addLevelEvent(2 + particle, LevelEventType.PARTICLE_POTION_SPLASH);
        this.addLevelEvent(3 + particle, LevelEventType.PARTICLE_EYE_OF_ENDER_DEATH);
        this.addLevelEvent(4 + particle, LevelEventType.PARTICLE_MOB_BLOCK_SPAWN);
        this.addLevelEvent(5 + particle, LevelEventType.PARTICLE_CROP_GROWTH);
        this.addLevelEvent(6 + particle, LevelEventType.PARTICLE_SOUND_GUARDIAN_GHOST);
        this.addLevelEvent(7 + particle, LevelEventType.PARTICLE_DEATH_SMOKE);
        this.addLevelEvent(8 + particle, LevelEventType.PARTICLE_DENY_BLOCK);
        this.addLevelEvent(9 + particle, LevelEventType.PARTICLE_GENERIC_SPAWN);
        this.addLevelEvent(10 + particle, LevelEventType.PARTICLE_DRAGON_EGG);
        this.addLevelEvent(11 + particle, LevelEventType.PARTICLE_CROP_EATEN);
        this.addLevelEvent(12 + particle, LevelEventType.PARTICLE_CRIT);
        this.addLevelEvent(13 + particle, LevelEventType.PARTICLE_TELEPORT);
        this.addLevelEvent(14 + particle, LevelEventType.PARTICLE_CRACK_BLOCK);

        // World
        int world = 3000;
        this.addLevelEvent(1 + world, LevelEventType.START_RAINING);
        this.addLevelEvent(2 + world, LevelEventType.START_THUNDERSTORM);
        this.addLevelEvent(3 + world, LevelEventType.STOP_RAINING);
        this.addLevelEvent(4 + world, LevelEventType.STOP_THUNDERSTORM);
        this.addLevelEvent(5 + world, LevelEventType.GLOBAL_PAUSE);

        // Blocks
        int block = 3500;
        this.addLevelEvent(block, LevelEventType.ACTIVATE_BLOCK);
        this.addLevelEvent(1 + block, LevelEventType.CAULDRON_EXPLODE);
        this.addLevelEvent(2 + block, LevelEventType.CAULDRON_DYE_ARMOR);
        this.addLevelEvent(3 + block, LevelEventType.CAULDRON_CLEAN_ARMOR);
        this.addLevelEvent(4 + block, LevelEventType.CAULDRON_FILL_POTION);
        this.addLevelEvent(5 + block, LevelEventType.CAULDRON_TAKE_POTION);
        this.addLevelEvent(6 + block, LevelEventType.CAULDRON_FILL_WATER);
        this.addLevelEvent(7 + block, LevelEventType.CAULDRON_TAKE_WATER);
        this.addLevelEvent(8 + block, LevelEventType.CAULDRON_ADD_DYE);

        int breaking = 3600;
        this.addLevelEvent(breaking, LevelEventType.BLOCK_START_BREAK);
        this.addLevelEvent(1 + breaking, LevelEventType.BLOCK_STOP_BREAK);

        int setData = 4000;
        this.addLevelEvent(setData, LevelEventType.SET_DATA);

        this.addLevelEvent(9800, LevelEventType.SLEEPING_PLAYERS);

        // Legacy particles
        int legacy = 0x4000;
        this.addLevelEvent(1 + legacy, LevelEventType.PARTICLE_BUBBLE);
        this.addLevelEvent(2 + legacy, LevelEventType.PARTICLE_CRITICAL);
        this.addLevelEvent(3 + legacy, LevelEventType.PARTICLE_BLOCK_FORCE_FIELD);
        this.addLevelEvent(4 + legacy, LevelEventType.PARTICLE_SMOKE);
        this.addLevelEvent(5 + legacy, LevelEventType.PARTICLE_EXPLODE);
        this.addLevelEvent(6 + legacy, LevelEventType.PARTICLE_EVAPORATION);
        this.addLevelEvent(7 + legacy, LevelEventType.PARTICLE_FLAME);
        this.addLevelEvent(8 + legacy, LevelEventType.PARTICLE_LAVA);
        this.addLevelEvent(9 + legacy, LevelEventType.PARTICLE_LARGE_SMOKE);
        this.addLevelEvent(10 + legacy, LevelEventType.PARTICLE_REDSTONE);
        this.addLevelEvent(11 + legacy, LevelEventType.PARTICLE_RISING_RED_DUST);
        this.addLevelEvent(12 + legacy, LevelEventType.PARTICLE_ITEM_BREAK);
        this.addLevelEvent(13 + legacy, LevelEventType.PARTICLE_SNOWBALL_POOF);
        this.addLevelEvent(14 + legacy, LevelEventType.PARTICLE_HUGE_EXPLODE);
        this.addLevelEvent(15 + legacy, LevelEventType.PARTICLE_HUGE_EXPLODE_SEED);
        this.addLevelEvent(16 + legacy, LevelEventType.PARTICLE_MOB_FLAME);
        this.addLevelEvent(17 + legacy, LevelEventType.PARTICLE_HEART);
        this.addLevelEvent(18 + legacy, LevelEventType.PARTICLE_TERRAIN);
        this.addLevelEvent(19 + legacy, LevelEventType.PARTICLE_TOWN_AURA);
        this.addLevelEvent(20 + legacy, LevelEventType.PARTICLE_PORTAL);
        this.addLevelEvent(21 + legacy, LevelEventType.PARTICLE_SPLASH);
        this.addLevelEvent(22 + legacy, LevelEventType.PARTICLE_WATER_WAKE);
        this.addLevelEvent(23 + legacy, LevelEventType.PARTICLE_DRIP_WATER);
        this.addLevelEvent(24 + legacy, LevelEventType.PARTICLE_DRIP_LAVA);
        this.addLevelEvent(25 + legacy, LevelEventType.PARTICLE_FALLING_DUST);
        this.addLevelEvent(26 + legacy, LevelEventType.PARTICLE_MOB_SPELL);
        this.addLevelEvent(27 + legacy, LevelEventType.PARTICLE_MOB_SPELL_AMBIENT);
        this.addLevelEvent(28 + legacy, LevelEventType.PARTICLE_MOB_SPELL_INSTANTANEOUS);
        this.addLevelEvent(29 + legacy, LevelEventType.PARTICLE_INK);
        this.addLevelEvent(30 + legacy, LevelEventType.PARTICLE_SLIME);
        this.addLevelEvent(31 + legacy, LevelEventType.PARTICLE_RAIN_SPLASH);
        this.addLevelEvent(32 + legacy, LevelEventType.PARTICLE_VILLAGER_ANGRY);
        this.addLevelEvent(33 + legacy, LevelEventType.PARTICLE_VILLAGER_HAPPY);
        this.addLevelEvent(34 + legacy, LevelEventType.PARTICLE_ENCHANTMENT_TABLE);
        this.addLevelEvent(35 + legacy, LevelEventType.PARTICLE_TRACKING_EMITTER);
        this.addLevelEvent(36 + legacy, LevelEventType.PARTICLE_NOTE);
        this.addLevelEvent(37 + legacy, LevelEventType.PARTICLE_WITCH_SPELL);
        this.addLevelEvent(38 + legacy, LevelEventType.PARTICLE_CARROT);
        this.addLevelEvent(39 + legacy, LevelEventType.PARTICLE_MOB_APPEARANCE);
        this.addLevelEvent(40 + legacy, LevelEventType.PARTICLE_END_ROD);
        this.addLevelEvent(41 + legacy, LevelEventType.PARTICLE_DRAGONS_BREATH);
        this.addLevelEvent(42 + legacy, LevelEventType.PARTICLE_SPIT);
        this.addLevelEvent(43 + legacy, LevelEventType.PARTICLE_TOTEM);
        this.addLevelEvent(44 + legacy, LevelEventType.PARTICLE_FOOD);
    }

    @Override
    protected void registerContainerSlotTypes() {
        // NOOP
    }

    @Override
    public EntityLinkData readEntityLink(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        long vehicleUniqueId = VarInts.readLong(buffer);
        long passengerUniqueId = VarInts.readLong(buffer);

        int type = buffer.readUnsignedByte();

        byte unknownByte = buffer.readByte();

        return new EntityLinkData(vehicleUniqueId, passengerUniqueId, EntityLinkData.Type.byId(type), unknownByte);
    }

    @Override
    public void writeEntityLink(ByteBuf buffer, EntityLinkData entityLink) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(entityLink, "entityLink");

        VarInts.writeLong(buffer, entityLink.getVehicleUniqueId());
        VarInts.writeLong(buffer, entityLink.getPassengerUniqueId());
        buffer.writeByte(entityLink.getType().ordinal());
        buffer.writeByte(entityLink.getUnknownByte());
    }

    @Override
    public ItemData readNetItem(ByteBuf buffer, BedrockSession session) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeNetItem(ByteBuf buffer, ItemData item, BedrockSession session) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemData readItem(ByteBuf buffer, BedrockSession session) {
        Preconditions.checkNotNull(buffer, "buffer");

        int id = VarInts.readInt(buffer);
        if (id == 0) {
            // We don't need to read anything extra.
            return ItemData.AIR;
        }
        int aux = VarInts.readInt(buffer);
        int damage = (short) (aux >> 8);
        if (damage == Short.MAX_VALUE) damage = -1;
        int count = aux & 0xff;
        short nbtSize = buffer.readShortLE();

        NbtMap compoundTag = null;
        if (nbtSize > 0) {
            try (NBTInputStream reader = NbtUtils.createReaderLE(new ByteBufInputStream(buffer.readSlice(nbtSize)))) {
                Object tag = reader.readTag();
                if (tag instanceof NbtMap) {
                    compoundTag = (NbtMap) tag;
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load NBT data", e);
            }
        }

        String[] canPlace = readArray(buffer, new String[0], this::readString);
        String[] canBreak = readArray(buffer, new String[0], this::readString);

        return ItemData.builder()
                .id(id)
                .damage(damage)
                .count(count)
                .tag(compoundTag)
                .canPlace(canPlace)
                .canBreak(canBreak)
                .build();
    }

    @Override
    public void writeItem(ByteBuf buffer, ItemData item, BedrockSession session) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(item, "item");

        // Write id
        int id = item.getId();
        if (id == 0) {
            // We don't need to write anything extra.
            buffer.writeByte(0);
            return;
        }
        VarInts.writeInt(buffer, id);

        // Write damage and count
        int damage = item.getDamage();
        if (damage == -1) damage = Short.MAX_VALUE;
        VarInts.writeInt(buffer, (damage << 8) | (item.getCount() & 0xff));

        // Remember this position, since we'll be writing the true NBT size here later:
        int sizeIndex = buffer.writerIndex();
        buffer.writeShortLE(0);

        if (item.getTag() != null) {
            int afterSizeIndex = buffer.writerIndex();
            try (NBTOutputStream stream = new NBTOutputStream(new LittleEndianByteBufOutputStream(buffer))) {
                stream.writeTag(item.getTag());
            } catch (IOException e) {
                // This shouldn't happen (as this is backed by a Netty ByteBuf), but okay...
                throw new IllegalStateException("Unable to save NBT data", e);
            }
            // Set to the written NBT size
            buffer.setShortLE(sizeIndex, buffer.writerIndex() - afterSizeIndex);
        }

        writeArray(buffer, item.getCanPlace(), this::writeString);
        writeArray(buffer, item.getCanBreak(), this::writeString);
    }

    @Override
    public ItemData readItemInstance(ByteBuf buffer, BedrockSession session) {
        return readItem(buffer, session);
    }

    @Override
    public void writeItemInstance(ByteBuf buffer, ItemData item, BedrockSession session) {
        writeItem(buffer, item, session);
    }

    @Override
    public CommandOriginData readCommandOrigin(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");
        int unknown1 = VarInts.readUnsignedInt(buffer);
        int unknown2 = VarInts.readUnsignedInt(buffer);
        long playerUniqueId = VarInts.readLong(buffer);
        return new CommandOriginData(unknown1, unknown2, playerUniqueId);
    }

    @Override
    public void writeCommandOrigin(ByteBuf buffer, CommandOriginData originData) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(originData, "commandOriginData");
        VarInts.writeUnsignedInt(buffer, originData.getUnknownVarUInt1());
        VarInts.writeUnsignedInt(buffer, originData.getUnknownVarUInt2());
        VarInts.writeLong(buffer, originData.getPlayerUniqueId());
    }

    @Override
    public GameRuleData<?> readGameRule(ByteBuf buffer) {
        Preconditions.checkNotNull(buffer, "buffer");

        String name = readString(buffer);
        int type = VarInts.readUnsignedInt(buffer);

        switch (type) {
            case 1:
                return new GameRuleData<>(name, buffer.readBoolean());
            case 2:
                return new GameRuleData<>(name, VarInts.readUnsignedInt(buffer));
            case 3:
                return new GameRuleData<>(name, buffer.readFloatLE());
        }
        throw new IllegalStateException("Invalid gamerule type received");
    }

    @Override
    public void writeGameRule(ByteBuf buffer, GameRuleData<?> gameRule) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(gameRule, "gameRule");

        Object value = gameRule.getValue();
        int type = this.gameRuleTypes.getInt(value.getClass());

        writeString(buffer, gameRule.getName());
        VarInts.writeUnsignedInt(buffer, type);
        switch (type) {
            case 1:
                buffer.writeBoolean((boolean) value);
                break;
            case 2:
                VarInts.writeUnsignedInt(buffer, (int) value);
                break;
            case 3:
                buffer.writeFloatLE((float) value);
                break;
        }
    }

    @Override
    public void readEntityData(ByteBuf buffer, EntityDataMap entityDataMap) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(entityDataMap, "entityDataDictionary");

        int length = VarInts.readUnsignedInt(buffer);

        for (int i = 0; i < length; i++) {
            int metadataInt = VarInts.readUnsignedInt(buffer);
            EntityData entityData = this.entityData.get(metadataInt);
            int typeId = VarInts.readUnsignedInt(buffer);
            EntityData.Type type = this.entityDataTypes.get(typeId);
            if (entityData != null && entityData.isFlags()) {
                if (type != Type.LONG) {
                    throw new IllegalArgumentException("Expected long value for flags, got " + type.name());
                }
                type = Type.FLAGS;
            }

            if (type == null) {
                throw new IllegalArgumentException("Unknown EntityDataType: " + typeId);
            }

            Object object;
            switch (type) {
                case BYTE:
                    object = buffer.readByte();
                    break;
                case SHORT:
                    object = buffer.readShortLE();
                    break;
                case INT:
                    object = VarInts.readInt(buffer);
                    break;
                case FLOAT:
                    object = buffer.readFloatLE();
                    break;
                case STRING:
                    object = readString(buffer);
                    break;
                case NBT:
                    object = this.readItem(buffer, null);
                    break;
                case VECTOR3I:
                    object = readVector3i(buffer);
                    break;
                case FLAGS:
                    int index = 0;
                    entityDataMap.getOrCreateFlags().set(VarInts.readLong(buffer), index, this.entityFlags);
                    continue;
                case LONG:
                    object = VarInts.readLong(buffer);
                    break;
                case VECTOR3F:
                    object = readVector3f(buffer);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown entity data type received");
            }
            if (entityData != null) {
                entityDataMap.put(entityData, object);
            } else {
                log.debug("Unknown entity data: {} type {} value {}", metadataInt, type, object);
            }
        }
    }

    @Override
    public void writeEntityData(ByteBuf buffer, EntityDataMap entityDataMap) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkNotNull(entityDataMap, "entityDataDictionary");

        VarInts.writeUnsignedInt(buffer, entityDataMap.size());

        for (Map.Entry<EntityData, Object> entry : entityDataMap.entrySet()) {
            int index = buffer.writerIndex();
            VarInts.writeUnsignedInt(buffer, this.entityData.get(entry.getKey()));
            Object object = entry.getValue();
            EntityData.Type type = EntityData.Type.from(object);
            VarInts.writeUnsignedInt(buffer, this.entityDataTypes.get(type));

            switch (type) {
                case BYTE:
                    buffer.writeByte((byte) object);
                    break;
                case SHORT:
                    buffer.writeShortLE((short) object);
                    break;
                case INT:
                    VarInts.writeInt(buffer, (int) object);
                    break;
                case FLOAT:
                    buffer.writeFloatLE((float) object);
                    break;
                case STRING:
                    writeString(buffer, (String) object);
                    break;
                case NBT:
                    ItemData item;
                    if (object instanceof NbtMap) {
                        item = ItemData.builder()
                                .id(1)
                                .damage(0)
                                .count(1)
                                .tag((NbtMap) object)
                                .build();
                    } else {
                        item = (ItemData) object;
                    }
                    this.writeItem(buffer, item, null);
                    break;
                case VECTOR3I:
                    writeVector3i(buffer, (Vector3i) object);
                    break;
                case FLAGS:
                    int flagsIndex = 0;
                    object = ((EntityFlags) object).get(flagsIndex, this.entityFlags);
                case LONG:
                    VarInts.writeLong(buffer, (long) object);
                    break;
                case VECTOR3F:
                    writeVector3f(buffer, (Vector3f) object);
                    break;
                default:
                    buffer.writerIndex(index);
                    break;
            }
        }
    }

    @Override
    public SerializedSkin readSkin(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeSkin(ByteBuf buffer, SerializedSkin skin) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImageData readImage(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeImage(ByteBuf buffer, ImageData image) {
        throw new UnsupportedOperationException();
    }
}
