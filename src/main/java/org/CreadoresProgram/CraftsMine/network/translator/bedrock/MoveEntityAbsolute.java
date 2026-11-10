package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MoveEntityPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * Translates a v113 MoveEntityPacket (server moves a mob) into the 0.15.x
 * MoveEntitiesPacket (which serialises a single entity: eid + 3 floats +
 * 3 bytes for pitch/headYaw/yaw scaled to 0..255).
 *
 * <p>v113 rotation bytes: pitch, headYaw, yaw are already in 0..255 range
 * (degrees * 256/360 rounded). 0.15.x expects the same scale.</p>
 */
public class MoveEntityAbsolute implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    MoveEntityPacket packet = (MoveEntityPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.MoveEntitiesPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.MoveEntitiesPacket();
    npk.eid = packet.getRuntimeEntityId();
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    npk.pitch = packet.getPitch() & 0xFF;
    npk.headYaw = packet.getHeadYaw() & 0xFF;
    npk.yaw = packet.getYaw() & 0xFF;
    player.sendDataCraftsman(npk);
  }
}
