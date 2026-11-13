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
 * <p><b>Rotation conversion</b>: v113 sends pitch/headYaw/yaw as raw
 * bytes in the 0..255 range (where 0=0° and 256=360°, the standard
 * Bedrock rotation byte encoding). The 0.15.x MoveEntitiesPacket
 * encode divides the stored double by {@code (360d/256d)} to convert
 * from degrees back to the byte range. So we must convert the v113
 * byte to DEGREES before assigning:</p>
 * <pre>
 *   degrees = v113Byte * (360.0 / 256.0)
 * </pre>
 * <p>This ensures the round-trip is lossless: v113 byte 128 → 180° →
 * encode divides by 1.40625 → writes byte 128. Without this conversion
 * the rotation would be compressed by an extra factor of ~1.4, making
 * entities face the wrong direction.</p>
 */
public class MoveEntityAbsolute implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    MoveEntityPacket packet = (MoveEntityPacket) pk;
    // Unknown-entity guard: the 0.15.x client crashes on move packets
    // for entities it never spawned (e.g. types dropped by the AddEntity
    // whitelist). See Player.knownEntities.
    if (!player.getKnownEntities().contains(packet.getRuntimeEntityId())) {
      return;
    }
    org.CreadoresProgram.CraftsMine.network.protocol.MoveEntitiesPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.MoveEntitiesPacket();
    npk.eid = packet.getRuntimeEntityId();
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    // v113 bytes are in 0..255 range (0=0°, 256=360°). Convert to
    // degrees so the 0.15.x encode (which divides by 360/256) writes
    // the correct byte back.
    double pitchDeg = (packet.getPitch() & 0xFF) * (360.0 / 256.0);
    double headYawDeg = (packet.getHeadYaw() & 0xFF) * (360.0 / 256.0);
    double yawDeg = (packet.getYaw() & 0xFF) * (360.0 / 256.0);
    npk.pitch = pitchDeg;
    npk.headYaw = headYawDeg;
    npk.yaw = yawDeg;
    player.sendDataCraftsman(npk);
  }
}
