package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector2f;

/**
 * v113 AddEntityPacket → 0.15.x AddEntityPacket.
 *
 * <p><b>CRITICAL FIX</b>: v113's {@link AddEntityPacket#getEntityType()}
 * returns a numeric entity type id (int). Bedrock 1.1 entity ids are
 * compatible with the 0.15.x entity id table for entities that existed
 * in both versions — BUT NOT ALL entity types in the 0..57 range exist
 * in 0.15.x.</p>
 *
 * <p>Entity types introduced AFTER MCPE 0.15.x that fall within the
 * old threshold (≤57) and would crash the 0.15.x client:</p>
 * <ul>
 *   <li>49: Guardian — added in 0.16 "Boss Update"</li>
 *   <li>50: Elder Guardian — added in 0.16</li>
 *   <li>51: NPC — education edition only</li>
 *   <li>54: Shulker — added in 1.0 "Ender Update"</li>
 *   <li>55: Endermite — added in 1.0</li>
 *   <li>56: Agent — education edition only</li>
 *   <li>57: Vindicator — added in 1.1</li>
 * </ul>
 *
 * <p>If the v113 server sends AddEntity for any of these (e.g. a
 * Guardian in an ocean monument), the previous code passed the type
 * through directly. The 0.15.x client tried to instantiate an unknown
 * entity type and CRASHED during world rendering.</p>
 *
 * <p>This version uses a strict whitelist of entity type ids that are
 * DEFINITELY supported by MCPE 0.15.10:</p>
 * <ul>
 *   <li>10-23: passive mobs (chicken, cow, pig, sheep, wolf, villager,
 *       mooshroom, squid, rabbit, bat, iron_golem, snow_golem, ocelot,
 *       horse)</li>
 *   <li>32-48: hostile mobs (zombie, creeper, skeleton, spider,
 *       zombie_pigman, slime, enderman, silverfish, cave_spider, ghast,
 *       magma_cube, blaze, zombie_villager, witch, stray, husk,
 *       wither_skeleton)</li>
 *   <li>52: Wither (boss)</li>
 *   <li>53: Ender Dragon (boss)</li>
 *   <li>64: Item (dropped item entity)</li>
 *   <li>65: Primed TNT</li>
 *   <li>66: Falling Block</li>
 *   <li>68: XP Bottle</li>
 *   <li>69: XP Orb</li>
 *   <li>80: Arrow</li>
 *   <li>81: Snowball</li>
 *   <li>85: Fireball / Small Fireball</li>
 * </ul>
 *
 * <p>Any entity type NOT in this whitelist is silently dropped — the
 * entity simply does not appear on the 0.15.x client. This is
 * preferable to crashing the client with an unknown network id.</p>
 */
public class AddEntity implements BedrockPacketTranslator{

  /**
   * Returns true if the given entity type id is supported by MCPE 0.15.10.
   * See class javadoc for the full whitelist.
   */
  private static boolean isSupported015(int type) {
    // Passive mobs: chicken=10, cow=11, pig=12, sheep=13, wolf=14,
    // villager=15, mooshroom=16, squid=17, rabbit=18, bat=19,
    // iron_golem=20, snow_golem=21, ocelot=22, horse=23
    if (type >= 10 && type <= 23) return true;
    // Hostile mobs: zombie=32, creeper=33, skeleton=34, spider=35,
    // zombie_pigman=36, slime=37, enderman=38, silverfish=39,
    // cave_spider=40, ghast=41, magma_cube=42, blaze=43,
    // zombie_villager=44, witch=45, stray=46, husk=47,
    // wither_skeleton=48
    if (type >= 32 && type <= 48) return true;
    // Bosses
    if (type == 52) return true; // Wither
    if (type == 53) return true; // Ender Dragon
    // Objects / projectiles
    if (type == 64) return true; // Item
    if (type == 65) return true; // Primed TNT
    if (type == 66) return true; // Falling Block
    if (type == 68) return true; // XP Bottle
    if (type == 69) return true; // XP Orb
    if (type == 80) return true; // Arrow
    if (type == 81) return true; // Snowball
    if (type == 85) return true; // Fireball
    return false;
  }

  @Override
  public void translate(BedrockPacket pk, Player player){
    AddEntityPacket packet = (AddEntityPacket) pk;
    int v113Type = packet.getEntityType();

    // Drop any entity type not in the 0.15.x whitelist. This prevents
    // the client from crashing on entities like Guardian (49), Shulker
    // (54), Endermite (55), Vindicator (57) that were introduced after
    // 0.15.x.
    if (!isSupported015(v113Type)) {
      return;
    }

    org.CreadoresProgram.CraftsMine.network.protocol.AddEntityPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AddEntityPacket();
    npk.eid = packet.getRuntimeEntityId();
    npk.type = v113Type;
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    Vector3f mot = packet.getMotion();
    if (mot != null) {
      npk.speedX = mot.getX();
      npk.speedY = mot.getY();
      npk.speedZ = mot.getZ();
    }
    Vector2f rot = packet.getRotation();
    if (rot != null) {
      // 0.15.x serializes yaw/pitch with a *0.71f scale factor on encode.
      // We set the raw value; the encode() method applies the factor.
      npk.yaw = rot.getY();
      npk.pitch = rot.getX();
    }
    npk.modifiers = 0;
    // Pick the AddEntity wire layout matching the client's protocol
    // (0.15.0-0.15.3 = raw floats, 0.15.4+ = scaled + modifiers int).
    npk.protocol = player.getProtocol();
    // metadata stays empty (with 0x7f end marker written by
    // Binary.writeMetadata) — entity spawns with default visual state.
    npk.metadata = new java.util.HashMap<>();
    player.sendDataCraftsman(npk);
    // Track the eid as known to the 0.15.x client so downstream
    // SetEntityData/MobEquipment/MoveEntity for it are not dropped by
    // the unknown-entity guard.
    player.getKnownEntities().add(packet.getRuntimeEntityId());
  }
}
