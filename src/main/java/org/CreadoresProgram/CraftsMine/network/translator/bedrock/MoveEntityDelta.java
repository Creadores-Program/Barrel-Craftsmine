package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MoveEntityPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * Same v113 source as MoveEntityAbsolute - both v113 MoveEntityAbsolutePacket
 * and MoveEntityDeltaPacket were renamed by the migration script to
 * MoveEntityPacket. This translator forwards the same way as MoveEntityAbsolute,
 * with the same rotation byte→degrees conversion.
 */
public class MoveEntityDelta implements BedrockPacketTranslator{
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
    // Same rotation conversion as MoveEntityAbsolute: v113 bytes are
    // in 0..255 range, convert to degrees for the 0.15.x encode.
    double pitchDeg = (packet.getPitch() & 0xFF) * (360.0 / 256.0);
    double headYawDeg = (packet.getHeadYaw() & 0xFF) * (360.0 / 256.0);
    double yawDeg = (packet.getYaw() & 0xFF) * (360.0 / 256.0);
    npk.pitch = pitchDeg;
    npk.headYaw = headYawDeg;
    npk.yaw = yawDeg;
    player.sendDataCraftsman(npk);
  }
}
