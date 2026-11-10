package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ChangeDimensionPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * v113 ChangeDimensionPacket (int dimension, Vector3f position, boolean
 * respawn) → 0.15.x ChangeDimensionPacket (byte dimension, float x,y,z).
 *
 * <p>0.15.x wire format appends a trailing 0 byte (hardcoded). The
 * 0.15.x encode() handles this; we just set the fields.</p>
 */
public class ChangeDimension implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ChangeDimensionPacket packet = (ChangeDimensionPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.ChangeDimensionPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.ChangeDimensionPacket();
    npk.dimension = (byte) packet.getDimension();
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    player.sendDataCraftsman(npk);
  }
}
