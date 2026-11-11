package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * Translates a 0.15.x MovePlayerPacket (sent by the upstream client when
 * it moves) into the v113 MovePlayerPacket and forwards it to the MCPE 1.1
 * downstream server.
 *
 * <p>0.15.x MovePlayerPacket fields: eid, x, y, z, yaw, headYaw, pitch,
 * mode (byte), onGround.</p>
 *
 * <p>v113 MovePlayerPacket expects: runtimeEntityId, position(Vector3f),
 * rotation(Vector3f), animation (NON-NULL: serializer calls .ordinal() at
 * line 22 without null check), onGround, ridingRuntimeEntityId.</p>
 *
 * <p>0.15.x mode byte → v113 Animation mapping (per Nukkit-Retro
 * MovePlayerPacket MODE_* constants vs v113 MovePlayerPacket.Animation):</p>
 * <ul>
 *   <li>MODE_NORMAL(0) → NORMAL — standard movement.</li>
 *   <li>MODE_RESET(1) → RESPAWN — clears state, used after
 *       teleport/respawn. The v113 serializer also writes extra int pairs
 *       for TELEPORT but RESPAWN does not, matching the 0.15.x behaviour
 *       (0.15.x RESET carries no extra payload).</li>
 *   <li>MODE_ROTATION(2) → HEAD_ROTATION — only the head yaw changes.</li>
 * </ul>
 *
 * <p>Previously this translator left {@code animation=null}, which caused
 * a NullPointerException at
 * {@code MovePlayerSerializer_v113.serialize:22} when the v113 codec
 * called {@code packet.getAnimation().ordinal()}. The NPE manifested as
 * "Error occurred whilst encoding MovePlayerPacket" on the proxy's
 * downstream send loop.</p>
 */
public class MovePlayerC implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket) pk;
    MovePlayerPacket npk = new MovePlayerPacket();
    npk.setRuntimeEntityId(packet.eid);
    // 0.15.x sends eye y; v113 expects feet y. Subtract eye height.
    npk.setPosition(Vector3f.from(packet.x, packet.y - 1.62f, packet.z));
    // v113 Vector3f rotation layout: x=pitch, y=yaw, z=headYaw.
    npk.setRotation(Vector3f.from(packet.pitch, packet.yaw, packet.headYaw));
    npk.setOnGround(packet.onGround);
    // CRITICAL: v113 MovePlayerSerializer_v113.serialize() at line 22 calls
    // packet.getAnimation().ordinal() WITHOUT a null check. The packet
    // constructor leaves animation=null. Setting NORMAL (or the mapped
    // value) is mandatory before sendPacket.
    MovePlayerPacket.Animation animation;
    switch (packet.mode) {
      case org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket.MODE_RESET:
        animation = MovePlayerPacket.Animation.RESPAWN;
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket.MODE_ROTATION:
        animation = MovePlayerPacket.Animation.HEAD_ROTATION;
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket.MODE_NORMAL:
      default:
        animation = MovePlayerPacket.Animation.NORMAL;
        break;
    }
    npk.setAnimation(animation);
    // ridingRuntimeEntityId defaults to 0 (no riding). The v113 serializer
    // always writes it as VarUnsignedLong - 0 is fine.
    player.getBedrockClientSession().sendPacket(npk);
  }
}
