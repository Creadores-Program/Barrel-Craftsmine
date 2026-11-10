package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerListPacket;
import java.util.List;

/**
 * v113 PlayerListPacket (Action ADD/REMOVE, List&lt;Entry&gt;) → 0.15.x
 * PlayerListPacket (byte type, Entry[] entries).
 *
 * <p>0.15.x Entry: UUID uuid, long entityId, String name, Skin skin.
 * v113 Entry: UUID uuid, long entityId, String name, SerializedSkin skin.</p>
 *
 * <p>Skin conversion: SerializedSkin → Skin is non-trivial (different
 * geometry/data layout). For now we use the default skin from Server.</p>
 */
public class PlayerList implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    PlayerListPacket packet = (PlayerListPacket) pk;
    if (packet.getAction() == null) return;
    byte type;
    switch(packet.getAction()){
      case ADD:    type = 0; break;
      case REMOVE: type = 1; break;
      default: return;
    }
    List<PlayerListPacket.Entry> entries = packet.getEntries();
    if (entries == null || entries.isEmpty()) return;
    org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket();
    npk.type = type;
    org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry[] arr =
        new org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry[entries.size()];
    for (int i = 0; i < entries.size(); i++) {
      PlayerListPacket.Entry e = entries.get(i);
      // PlayerListPacket.Entry has no no-arg constructor; all fields are
      // final. Use reflection to instantiate and assign. If reflection
      // fails, drop the entry.
      org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry entry =
          buildEntry(e.getUuid(), e.getEntityId(),
              e.getName() == null ? "" : e.getName(),
              new org.CreadoresProgram.CraftsMine.nukkitLib.entity.data.Skin(new byte[0]));
      if (entry == null) continue;
      arr[i] = entry;
    }
    npk.entries = arr;
    player.sendDataCraftsman(npk);
  }

  /** Reflection helper: build a 0.15.x PlayerListPacket.Entry by name. */
  private static org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry buildEntry(
      java.util.UUID uuid, long entityId, String name,
      org.CreadoresProgram.CraftsMine.nukkitLib.entity.data.Skin skin) {
    try {
      Class<?> clazz = org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry.class;
      // Try (UUID, long, String, Skin) constructor first.
      try {
        java.lang.reflect.Constructor<?> c = clazz.getConstructor(
            java.util.UUID.class, long.class, String.class,
            org.CreadoresProgram.CraftsMine.nukkitLib.entity.data.Skin.class);
        c.setAccessible(true);
        return (org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry) c.newInstance(uuid, entityId, name, skin);
      } catch (NoSuchMethodException nsme) {
        // Fall back to no-arg + field assignment if available.
        java.lang.reflect.Constructor<?> c = clazz.getDeclaredConstructor();
        c.setAccessible(true);
        org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry e =
            (org.CreadoresProgram.CraftsMine.network.protocol.PlayerListPacket.Entry) c.newInstance();
        // Try public field assignment (some versions expose uuid/entityId/name/skin as public final
        // fields writable only via reflection after construction).
        setField(e, "uuid", uuid);
        setField(e, "entityId", entityId);
        setField(e, "name", name);
        setField(e, "skin", skin);
        return e;
      }
    } catch (Throwable t) {
      return null;
    }
  }

  private static void setField(Object target, String name, Object value) {
    try {
      java.lang.reflect.Field f = target.getClass().getDeclaredField(name);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Throwable ignored) {}
  }
}
