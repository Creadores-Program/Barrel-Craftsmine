package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ExplodePacket;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import java.util.List;

/**
 * v113 ExplodePacket (Vector3f position, float radius, List&lt;Vector3i&gt;
 * records) → 0.15.x ExplodePacket (float x,y,z, float radius, Vector3[]
 * records).
 *
 * <p>v113 records are absolute Vector3i block positions. 0.15.x expects
 * byte offsets relative to the explosion centre. We compute the offset
 * and clamp to byte range.</p>
 */
public class Explode implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ExplodePacket packet = (ExplodePacket) pk;
    Vector3f pos = packet.getPosition();
    if (pos == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.ExplodePacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.ExplodePacket();
    npk.x = pos.getX();
    npk.y = pos.getY();
    npk.z = pos.getZ();
    npk.radius = packet.getRadius();
    List<Vector3i> records = packet.getRecords();
    if (records != null && !records.isEmpty()) {
      org.CreadoresProgram.CraftsMine.player.Vector3[] arr =
          new org.CreadoresProgram.CraftsMine.player.Vector3[records.size()];
      for (int i = 0; i < records.size(); i++) {
        Vector3i r = records.get(i);
        // Compute byte offset relative to the explosion centre.
        int dx = (int) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, r.getX() - (int) pos.getX()));
        int dy = (int) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, r.getY() - (int) pos.getY()));
        int dz = (int) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, r.getZ() - (int) pos.getZ()));
        // Vector3 in CraftsMine has public double x,y,z fields (no (int,int,int)
        // constructor). Build it via field assignment.
        org.CreadoresProgram.CraftsMine.player.Vector3 v =
            new org.CreadoresProgram.CraftsMine.player.Vector3();
        v.x = dx;
        v.y = dy;
        v.z = dz;
        arr[i] = v;
      }
      // 0.15.x ExplodePacket.records is a Vector3[] field.
      try {
        java.lang.reflect.Field f = org.CreadoresProgram.CraftsMine.network.protocol.ExplodePacket.class.getField("records");
        f.set(npk, arr);
      } catch (NoSuchFieldException | IllegalAccessException ignored) {
        // Field name mismatch - silently drop records.
      }
    }
    player.sendDataCraftsman(npk);
  }
}
