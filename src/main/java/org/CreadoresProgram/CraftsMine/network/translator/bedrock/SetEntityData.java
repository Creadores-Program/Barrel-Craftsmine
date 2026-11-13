package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import java.util.HashMap;
import java.util.Map;


/**
 * v113 SetEntityDataPacket → 0.15.x SetEntityDataPacket.
 *
 * <p><b>Unknown-entity guard (CRITICAL)</b>: the v113 server streams
 * SetEntityData for EVERY entity it tracks, including entity types the
 * proxy's AddEntity whitelist dropped (Guardian=49, Shulker=54, ...).
 * The 0.15.x client receiving SetEntityData for an eid it never spawned
 * crashes its entity renderer WITHOUT sending a Disconnect back — the
 * proxy then only notices when the RakNet session times out. This was
 * the "client crashes, dropped by proxy timeout" symptom: the crash
 * snapshot showed SetEntityData for eids 1554..1558 with no AddEntity
 * ever sent for them. Packets for unknown eids are silently dropped
 * here.</p>
 *
 * <p><b>Metadata translation</b>: v113 uses an EntityData enum key
 * space; 0.15.x uses raw int keys with a (type<<5)|key header byte
 * (see Binary.writeMetadata and MBNukkit/Nukkit 0.15
 * Entity.java constants). Mapping (only entries the 0.15.10 client
 * actually understands are forwarded):</p>
 * <pre>
 *   v113 EntityData        0.15 key   0.15 type
 *   ---------------------  --------   ---------
 *   NAMETAG (STRING)         2        STRING (4)
 *   AIR_SUPPLY (SHORT)       1        SHORT  (1)
 *   EFFECT_COLOR (INT)       7        INT    (2)
 *   EFFECT_AMBIENT (BYTE)    8        BYTE   (0)
 *   LEASH_HOLDER_EID (LONG) 23        LONG   (7)
 * </pre>
 * <p>v113 HEALTH has no 0.15.x metadata equivalent (health goes in
 * UpdateAttributes / SetHealth packets), so it is dropped. v113 flags
 * beyond bit 5 (BABY, SLEEPING, PLAYING_DEAD...) did not exist in
 * 0.15.x and are dropped.</p>
 *
 * <p>Unmapped v113 entries are dropped silently. If NOTHING maps, the
 * packet is not sent at all — an empty metadata map would carry no
 * information (only the 0x7f terminator) and, for entities the client
 * doesn't know, would risk the crash described above.</p>
 */
public class SetEntityData implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetEntityDataPacket packet = (SetEntityDataPacket) pk;
    long eid = packet.getRuntimeEntityId();
    // Never forward metadata for an entity the 0.15.x client doesn't
    // have (dropped by AddEntity whitelist / never added).
    if (!player.getKnownEntities().contains(eid)) {
      return;
    }
    Map<Integer, Object[]> map = new HashMap<>();
    for (Map.Entry<EntityData, Object> entry : packet.getMetadata().entrySet()) {
      EntityData key = entry.getKey();
      Object value = entry.getValue();
      if (value == null) continue;
      if (key == EntityData.NAMETAG) {
        map.put(2, new Object[]{ 4, value.toString() });
      } else if (key == EntityData.AIR_SUPPLY) {
        map.put(1, new Object[]{ 1, ((Number) value).shortValue() });
      } else if (key == EntityData.EFFECT_COLOR) {
        map.put(7, new Object[]{ 2, ((Number) value).intValue() });
      } else if (key == EntityData.EFFECT_AMBIENT) {
        map.put(8, new Object[]{ 0, (byte) (((Number) value).intValue() & 0xff) });
      } else if (key == EntityData.LEASH_HOLDER_EID) {
        map.put(23, new Object[]{ 7, ((Number) value).longValue() });
      }
      // Everything else (HEALTH, VARIANT, SCALE, MARK_VARIANT, ...) has
      // no 0.15.x metadata equivalent — dropped on purpose.
    }
    // Flags: pack the v113 EntityFlags set into the 0.15.x DATA_FLAGS
    // byte (key 0, type BYTE). Only bits 0-5 existed in 0.15.x.
    // EntityFlags exposes no public iterator, so probe each flag.
    if (packet.getMetadata().getFlags() != null) {
      int flags = 0;
      if (packet.getMetadata().getFlags().getFlag(EntityFlag.ON_FIRE))    flags |= (1 << 0);
      if (packet.getMetadata().getFlags().getFlag(EntityFlag.SNEAKING))   flags |= (1 << 1);
      if (packet.getMetadata().getFlags().getFlag(EntityFlag.RIDING))     flags |= (1 << 2);
      if (packet.getMetadata().getFlags().getFlag(EntityFlag.SPRINTING))  flags |= (1 << 3);
      if (packet.getMetadata().getFlags().getFlag(EntityFlag.INVISIBLE))  flags |= (1 << 5);
      // ACTION(4) exists in 0.15.x but v113 has no equivalent flag.
      // SLEEPING/BABY/PLAYING_DEAD did not exist in 0.15.x metadata.
      map.put(0, new Object[]{ 0, (byte) (flags & 0x3f) });
    }
    // Nothing mapped → nothing to tell the client → do not send an
    // empty SetEntityData at all (an empty map would be a no-op update
    // that still costs the client a re-render pass).
    if (map.isEmpty()) {
      return;
    }
    org.CreadoresProgram.CraftsMine.network.protocol.SetEntityDataPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetEntityDataPacket();
    npk.eid = eid;
    npk.metadata = map;
    player.sendDataCraftsman(npk);
  }
}
