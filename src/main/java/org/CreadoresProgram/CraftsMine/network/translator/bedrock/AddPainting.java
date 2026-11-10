package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AddPaintingPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * v113 AddPaintingPacket (Vector3f position, int direction, String motive)
 * → 0.15.x AddPaintingPacket (int x/y/z, int direction, String title).
 *
 * <p>0.15.x uses integer block coordinates for paintings, not floats.
 * We cast the v113 float position to int.</p>
 */
public class AddPainting implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    AddPaintingPacket packet = (AddPaintingPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.AddPaintingPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AddPaintingPacket();
    npk.eid = packet.getRuntimeEntityId();
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = (int) pos.getX();
      npk.y = (int) pos.getY();
      npk.z = (int) pos.getZ();
    }
    npk.direction = packet.getDirection();
    npk.title = packet.getMotive() == null ? "" : packet.getMotive();
    player.sendDataCraftsman(npk);
  }
}
