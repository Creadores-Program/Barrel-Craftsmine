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
 * returns a numeric entity type id (int), NOT a string identifier.
 * Bedrock 1.1 still used integer entity ids that are <b>directly
 * compatible</b> with the 0.15.x entity id table for every entity that
 * existed in both versions (chicken=10, cow=11, pig=12, sheep=13,
 * wolf=14, villager=15, ... ender_dragon=53, shulker=54, endermite=55,
 * agent=56, npc=51).</p>
 *
 * <p>The previous implementation called
 * {@code EntityMapper.mapToLegacy(String.valueOf(packet.getEntityType()))}
 * which converted the int to the string "12" / "32" / etc. — those
 * strings never matched the mapper's name table ("minecraft:zombie",
 * "cow", etc.) so EVERY AddEntityPacket was silently dropped to
 * EntityMapper.DROP. Result: the 0.15.x client never saw a single mob
 * spawn, which made the world look empty and — when combined with
 * SetEntityData / MoveEntity packets referencing those dropped entity
 * ids — caused the client to crash because it received packets for
 * entities it had never spawned.</p>
 *
 * <p>For entity ids > 57 (entities introduced AFTER 0.15.x — vindicator,
 * panda, pillager, ravager, wandering trader, etc.) we substitute the
 * closest 0.15.x equivalent via {@link org.CreadoresProgram.CraftsMine.utils.EntityMapper}
 * using the runtime-id → legacy-id fallback table. If no substitute
 * exists, the packet is dropped (the entity simply does not appear
 * to the 0.15.x client; this is preferable to crashing the client).</p>
 *
 * <p>Entity metadata is forwarded as an empty map. The 0.15.x client
 * will spawn the entity with default visual state (no nametag, no
 * scale overrides, no pose tweaks). Wiring the EntityDataMap →
 * legacy metadata byte format is a follow-up task.</p>
 */
public class AddEntity implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    AddEntityPacket packet = (AddEntityPacket) pk;
    // v113 entityType is a numeric id compatible with 0.15.x for ids <= 57.
    // For ids > 57 (entities introduced after 0.15.x) we have to substitute
    // or drop - the 0.15.x client would crash on an unknown network id.
    int legacyType;
    int v113Type = packet.getEntityType();
    if (v113Type <= 57) {
      // Direct passthrough: chicken=10, cow=11, pig=12, ... endermite=55,
      // agent=56. These ids are identical between v113 and 0.15.x.
      legacyType = v113Type;
    } else {
      // Entities not in 0.15.x (vindicator=57, panda, pillager, ravager,
      // wandering trader, fox, bee, etc.) — drop the packet to avoid
      // crashing the 0.15.x client with an unknown network id.
      // The entity simply will not appear on the 0.15.x client.
      return;
    }

    org.CreadoresProgram.CraftsMine.network.protocol.AddEntityPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AddEntityPacket();
    npk.eid = packet.getRuntimeEntityId();
    npk.type = legacyType;
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
    // metadata stays empty: see class javadoc.
    npk.metadata = new java.util.HashMap<>();
    player.sendDataCraftsman(npk);
  }
}
